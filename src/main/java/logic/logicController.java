package logic;

import network.*;
import window.AppLogger;
import window.LocalUIEvents;
import window.connection.ConnectionPresenter;
import window.local.LocalPresenter;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class logicController extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();

	List<File> filesAvailableForTranfer;

	private String downloadPath;

	private RemoteSocketConnection connection;

	private DeltaTime deltaT = new DeltaTime(5);

	private RemoteUIEvents remoteEvents;

	public logicController(RemoteUIEvents remoteEvents)
	{
		LocalUIEventHandler localUIHandler = new LocalUIEventHandler();
		ConnectionPresenter.changeLocalEventHandler(localUIHandler);
		LocalPresenter.changeLocalEventHandler(localUIHandler);

		this.remoteEvents = remoteEvents;
		setDaemon(true);
	}

	public void run()
	{
		connection = new RemoteSocketConnection(new SocketEventReceiver(), new ConnectionListener());
		filesAvailableForTranfer = new ArrayList<>();

		while (true)
		{

			deltaT.update();
			if (!deltaT.enoughTimePassed())
			{
				//DEBUG
				if (connection.isConnected())
					LOGGER.log(Level.FINE, "Connected to: " + connection.getSocket().getInetAddress());

				try
				{
					long millis = deltaT.getTimeToTick() / (long) 1e+6;
//					AppLogger.getInstance().log(Level.FINEST, String.format("sleeping for %d millis", millis));
					Thread.sleep(millis);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			deltaT.subtractOneTick();
		}
	}

	class LocalUIEventHandler implements LocalUIEvents
	{
		@Override
		public void updateAvailableFileList(List<File> file)
		{
			LOGGER.log(Level.FINE, "Updating file list" + file.toString());

			filesAvailableForTranfer = new ArrayList<>(file);
		}

		@Override
		public boolean attemptConnectionToHost(String host, int port)
		{
			LOGGER.log(Level.FINE, "Connection request to: " + host);

			return connection.attemptConnection(host);
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

		}
	}

	class SocketEventReceiver implements SocketReceivingEvents
	{
		@Override
		public void updateRemoteFileList(List<String> files)
		{
			remoteEvents.updateRemoteFileList(files);
		}

		@Override
		public void uploadFile(String filename)
		{

		}
	}

	class ConnectionListener implements network.ConnectionListener.ConnectionReceivedEvent
	{
		public void receivedConnection(Socket socket)
		{
			Logger logger = AppLogger.getInstance();
			logger.log(Level.ALL, "Received Connection request");
			if (!connection.isConnected())
			{
				logger.log(Level.ALL, "Successfully connected to socket");
				connection.acceptConnection(socket);
			} else
			{
				logger.log(Level.ALL, String.format("Could not connect to: %s, already connected to: %s",
						connection.getSocket().getInetAddress(), socket.getInetAddress()));
			}
		}
	}
}
