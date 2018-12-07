package logic;

import network.*;
import org.omg.CORBA.TIMEOUT;
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
	private static final int MAIN_PORT = 53222;
	private static final int PORT_ONE = 53223;
	private static final int PORT_TWO = 53224;

	private String downloadPath = "C:\\Users\\Goia\\Desktop\\test_folder";

	private enum State
	{
		CONNECTED,
		CONNECTING,
		DISCONNECTED;
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
					connectionResolver.attemptConnection(InetAddress.getByName(host), PORT_ONE);
					connectionResolver.attemptConnection(InetAddress.getByName(host), PORT_TWO);

					if (null != mainConnection && null != receivingConnection && null != transmittingConnection)
					{
						constructControllers();
						state = State.CONNECTED;
					} else
					{
						closeConnections();
						state = State.DISCONNECTED;
						connectionResolver.startListening(MAIN_PORT);
					}
				} catch (UnknownHostException e)
				{
					LOGGER.log(Level.ALL, "Invalid address: " + host);
//					e.printStackTrace();
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

				closeConnections();
				connectionResolver.startListening(MAIN_PORT);
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

	private void constructControllers()
	{
		assert null != mainConnection && mainConnection.isConnected() : "Main not connected";
		assert null != transmittingConnection && transmittingConnection.isConnected() : "Transmitting not connected";
		assert null != receivingConnection && receivingConnection.isConnected() : "Receiving not connected";
		assert null == receiverController : "controller already constructed";
		assert null == transmitterController : "controller already constructed";

		receiverController = new ReceiverController(mainConnection, receivingConnection, businessEvents);
		transmitterController = new TransmittingController(mainConnection, transmittingConnection, businessEvents);
		receiverController.startListening();
	}

	private void closeConnections()
	{
		if (null != receiverController)
		{
			receiverController.close();
			receiverController = null;
		}

		if (null != transmitterController)
			transmitterController = null;

		if (null != mainConnection)
		{
			mainConnection.close();
		}

		if (null != receivingConnection)
		{
			receivingConnection.close();
		}

		if (null != transmittingConnection)
		{
			transmittingConnection.close();
		}

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
				case PORT_ONE:
				{
					transmittingConnection = connection;
				}
				break;
				case PORT_TWO:
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
				connectionResolver.startListening(PORT_ONE, CONNECTION_TIMEOUT_MILLIS);
			} else
			{
				if (null == transmittingConnection)
				{
					connectionResolver.startListening(PORT_TWO, CONNECTION_TIMEOUT_MILLIS);
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
							closeConnections();
							state = State.DISCONNECTED;
							connectionResolver.startListening(MAIN_PORT);
						}
						
					}
				}).start();
				mainConnection = connection;
			}
			break;
			case PORT_ONE:
			{
				receivingConnection = connection;
			}
			break;
			case PORT_TWO:
			{
				transmittingConnection = connection;
			}
			break;
		}
	}
}

