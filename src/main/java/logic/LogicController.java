package logic;

import network.*;
import window.AppLogger;
import window.UIEvents;
import window.connection.ConnectionPresenter;
import window.local.LocalPresenter;
import window.remote.RemotePresenter;

import java.io.File;
import java.net.Socket;
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
		ConnectionPresenter.changeLocalEventHandler(localUIHandler);
		LocalPresenter.setUIEventHandler(localUIHandler);
		RemotePresenter.addLocalEventHandler(localUIHandler);

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
		public void updateAvailableFileList(List<File> files)
		{

			transmitterController.updateAvailableFileList(files);
		}

		@Override
		public boolean attemptConnectionToHost(String host, int port)
		{
			assert null == mainSocket : "Already connected to something";

			LOGGER.log(Level.FINE, "Connection request to: " + host);
			connectionResolver.attemptConnection(host, port, port + 1);

			return true;
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

	class ConnectionListener implements ConnectionResolver.ConnectionEvent
	{
		public void connectionEstablished(Socket socket)
		{
			assert null == mainSocket || mainSocket.isClosed() : "Got connection request while connected";
			assert socket.isConnected() : "Got event with unconnected socket";

			LOGGER.log(Level.ALL, "Received Connection request");
			if (businessEvents.confirmConnectionRequest(socket.getInetAddress().toString()))
			{
				LOGGER.log(Level.ALL, "Successfully connected to socket");
				mainSocket = socket;
				receiverController = new ReceiverController(socket, businessEvents);
				transmitterController = new TransmitterController(socket, businessEvents);

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
