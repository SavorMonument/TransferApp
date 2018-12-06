package logic;

import network.*;
import window.AppLogger;
import window.UIEvents;
import window.connection.ConnectionController;
import window.local.LocalController;
import window.remote.RemoteController;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogicController extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int PORT = 50001;

	private String downloadPath = "C:\\Users\\Goia\\Desktop\\test_folder";

	private ConnectionResolver connectionResolver;

	private BusinessEvents businessEvents;
	private Socket mainSocket;

	private TransmitterController transmitterController;
	private ReceiverController receiverController;

	public LogicController(BusinessEvents businessEvents)
	{
		UIEvents localUIHandler = new UIEventReceiver();
		ConnectionController.changeLocalEventHandler(localUIHandler);
		LocalController.setUIEventHandler(localUIHandler);
		RemoteController.addLocalEventHandler(localUIHandler);

		this.businessEvents = businessEvents;
		this.connectionResolver = new ConnectionResolver(new ConnectionListener());

		connectionResolver.startListening(PORT);
		setDaemon(true);
	}

	public void run()
	{

		while (true)
		{

			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
	}

	class UIEventReceiver implements UIEvents
	{
		@Override
		public boolean attemptConnectionToHost(String host, int port)
		{
			if (null == mainSocket)
			{
				LOGGER.log(Level.ALL, "Connection request to: " + host);
				try
				{
					connectionResolver.attemptConnection(InetAddress.getByName(host), port, port + 1);
					return true;
				} catch (UnknownHostException e)
				{
					LOGGER.log(Level.ALL, "Invalid address: " + host);
//					e.printStackTrace();
				}
			} else
				LOGGER.log(Level.ALL, "Connection request denied: Already connected");

			return false;
		}

		@Override
		public void disconnect()
		{
			if (null != mainSocket)
			{
				LOGGER.log(Level.ALL, "Disconnecting: " + mainSocket.getInetAddress().toString());

				receiverController.close();

				transmitterController = null;
				receiverController = null;
				try
				{
					mainSocket.close();
				} catch (IOException e)
				{
					LOGGER.log(Level.WARNING, "Exception while closing socket " + e.getMessage());
//					e.printStackTrace();
				}
				mainSocket = null;
			}else
				LOGGER.log(Level.ALL, "Disconnect request denied, not connected");
		}

		@Override
		public void updateAvailableFileList(List<File> files)
		{

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

	class ConnectionListener extends ConnectionResolver.ConnectionEvent
	{
		public void connectionEstablished(Socket socket, SocketMessageTransmitter messageTransmitter, SocketMessageReceiver messageReceiver)
		{
			assert null == mainSocket || mainSocket.isClosed() : "Got connection request while connected";
			assert socket.isConnected() : "Got event with unconnected socket";

			LOGGER.log(Level.ALL, "Received Connection request");
			if (businessEvents.confirmConnectionRequest(socket.getInetAddress().toString()))
			{
				LOGGER.log(Level.ALL, "Successfully connected to socket");
				mainSocket = socket;
				receiverController = new ReceiverController(messageReceiver, businessEvents);
				transmitterController = new TransmitterController(messageTransmitter, businessEvents);

				if (connectionResolver.isListening())
					connectionResolver.stopListening();
			} else
			{
				LOGGER.log(Level.ALL, String.format("Could not connect to: %s, already connected to: %s",
						socket.getInetAddress(), socket.getInetAddress()));

				if (!connectionResolver.isListening())
					connectionResolver.startListening(PORT);
			}
		}
	}

}
