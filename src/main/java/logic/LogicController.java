package logic;

import network.*;
import window.AppLogger;
import window.UIEvents;
import window.connection.ConnectionController;
import window.local.LocalController;
import window.remote.RemoteController;

import java.io.File;
import java.net.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogicController extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;
	private static final int MAIN_PORT = 48552;
	private static final int SECOND_PORT = 48553;
	private static final int THIRD_PORT = 48554;

	private String downloadPath = "C:\\Users\\Goia\\Desktop\\test_folder";

	private enum State
	{
		CONNECTED,
		CONNECTING,
		DISCONNECTED,
		DISCONNECTING;
	}

	private State state = State.DISCONNECTED;

	private ConnectionResolver connectionResolver;
	private Connection mainConnection;
	private Connection transmittingConnection;
	private Connection receivingConnection;

	private BusinessEvents businessEvents;

	private TransmittingController transmitterController;
	private ReceiverController receiverController;

	public LogicController(BusinessEvents businessEvents)
	{
		UIEvents localUIHandler = new UIEventReceiver();
		ConnectionController.changeLocalEventHandler(localUIHandler);
		LocalController.setUIEventHandler(localUIHandler);
		RemoteController.addLocalEventHandler(localUIHandler);

		this.businessEvents = businessEvents;
		this.connectionResolver = new ConnectionResolver(new ConnectionListener());

		connectionResolver.startListening(MAIN_PORT);
		setDaemon(true);
	}

	public void run()
	{

		while (true)
		{
			System.out.println(connectionResolver.isListening());
			try
			{
				Thread.sleep(3000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
	}

	class UIEventReceiver implements UIEvents
	{
		@Override
		public void attemptConnectionToHost(String host)
		{
			if (state == State.DISCONNECTED)
			{
				LOGGER.log(Level.ALL, "Connection request to: " + host);

				connectionResolver.stopListening();
				state = State.CONNECTING;
				try
				{
					connectionResolver.attemptConnection(InetAddress.getByName(host), MAIN_PORT);
					connectionResolver.attemptConnection(InetAddress.getByName(host), SECOND_PORT);
					connectionResolver.attemptConnection(InetAddress.getByName(host), THIRD_PORT);

					if (null != mainConnection && null != receivingConnection && null != transmittingConnection)
					{
						constructControllers();
						state = State.CONNECTED;
					} else
					{
						closeConnectionsAndReset();
					}
				} catch (UnknownHostException e)
				{
					LOGGER.log(Level.ALL, "Invalid address: " + host);
//					e.printStackTraconnection.getLocalPort();
				}
			} else
				LOGGER.log(Level.WARNING, "Connection request denied: Already connected");
		}

		@Override
		public void disconnect()
		{
			if (state == State.CONNECTED)
			{
				LOGGER.log(Level.ALL, "Disconnecting: " + mainConnection.getRemoteAddress());
				//That means I am the 'server' so tell the client to close connection
				if (mainConnection.getLocalPort() == MAIN_PORT)
					transmitterController.transmitDisconnectMessage();
				else
					closeConnectionsAndReset();
			} else
				LOGGER.log(Level.ALL, "Disconnect request denied, not connected");
		}

		@Override
		public void updateAvailableFileList(List<File> files)
		{
			LOGGER.log(Level.ALL, "Sending new file list to remote: " + files.toString());
			transmitterController.updateAvailableFileList(files);
		}

		@Override
		public void setDownloadLocation(String path)
		{
			LOGGER.log(Level.FINE, "Set file download location to: " + path);

			downloadPath = path;
		}

		@Override
		public void requestFileForDownload(String fileName)
		{
			LOGGER.log(Level.FINE, "Request file" + fileName);

			transmitterController.requestFileForDownload(fileName, downloadPath);
		}
	}

	class ConnectEvent implements ConnectCloseEvent
	{
		@Override
		public void disconnect(String message)
		{
			LOGGER.log(Level.WARNING, "Connection disrupted, resetting connections: " + message);

			if (state != State.DISCONNECTING && state != State.DISCONNECTED)
			{
				state = State.DISCONNECTING;
				new Thread(() -> closeConnectionsAndReset()).start();
				state = State.DISCONNECTED;

			}
		}
	}

	private void constructControllers()
	{
		assert null != mainConnection && mainConnection.isConnected() : "Main not connected";
		assert null != transmittingConnection && transmittingConnection.isConnected() : "Transmitting not connected";
		assert null != receivingConnection && receivingConnection.isConnected() : "Receiving not connected";
		assert null == receiverController : "Controller already constructed";
		assert null == transmitterController : "Controller already constructed";

		ConnectEvent connectEvent = new ConnectEvent();

		receiverController = new ReceiverController(mainConnection, receivingConnection, businessEvents, connectEvent);
		transmitterController = new TransmittingController(mainConnection, transmittingConnection, businessEvents, connectEvent);
		receiverController.startListening();
	}

	private void closeConnectionsAndReset()
	{
		state = State.DISCONNECTING;

		if (null != mainConnection)
		{
			mainConnection.close();
			mainConnection = null;
		}

		if (null != receiverController)
		{
			receiverController.stopListening();
			receiverController = null;
		}

		if (null != transmitterController)
			transmitterController = null;

		if (null != receivingConnection)
		{
			receivingConnection.close();
			receivingConnection = null;
		}

		if (null != transmittingConnection)
		{
			transmittingConnection.close();
			transmittingConnection = null;
		}

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		connectionResolver.startListening(MAIN_PORT);
		state = State.DISCONNECTED;
	}

	class ConnectionListener implements ConnectionResolver.ConnectionEvent
	{
		public void connectionAttemptSuccessful(Connection connection)
		{
			assert null != connection && connection.isConnected() : "Received Invalid connection";

			assignConnectionAsClient(connection);
		}

		private void assignConnectionAsClient(Connection connection)
		{
			switch (connection.getRemotePort())
			{
				case MAIN_PORT:
				{
					mainConnection = connection;
				}
				break;
				case SECOND_PORT:
				{
					transmittingConnection = connection;
				}
				break;
				case THIRD_PORT:
				{
					receivingConnection = connection;
				}
				break;
			}
		}

		public void connectionReceivedOnListener(Connection connection)
		{
			assert null != connection && connection.isConnected() : "Received invalid connection";

			state = State.CONNECTING;
			assignConnectionAsServer(connection);

			if (null == receivingConnection)
			{
				connectionResolver.startListening(SECOND_PORT, CONNECTION_TIMEOUT_MILLIS);
			} else
			{
				if (null == transmittingConnection)
				{
					connectionResolver.startListening(THIRD_PORT, CONNECTION_TIMEOUT_MILLIS);
				} else
				{
					constructControllers();
					state = State.CONNECTED;
				}
			}
		}
	}

	private void assignConnectionAsServer(Connection connection)
	{
		switch (connection.getLocalPort())
		{
			case MAIN_PORT:
			{
				//Patchwork
				//TODO: REDO this
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							Thread.sleep(CONNECTION_TIMEOUT_MILLIS + 1_000);
						} catch (InterruptedException e)
						{
//							e.printStackTrace();
						}
						if (state == State.CONNECTING)
						{
							closeConnectionsAndReset();
							state = State.DISCONNECTED;
							connectionResolver.startListening(MAIN_PORT);
						}

					}
				}).start();
				mainConnection = connection;
			}
			break;
			case SECOND_PORT:
			{
				receivingConnection = connection;
			}
			break;
			case THIRD_PORT:
			{
				transmittingConnection = connection;
			}
			break;
		}
	}
}

