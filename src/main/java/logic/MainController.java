package logic;

import com.sun.javafx.binding.StringFormatter;
import network.NetworkListener;
import network.SocketConnection;
import network.SocketListener;
import window.AppLogger;
import window.LocalUI;
import window.RemoteUI;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController extends Thread
{
	private static final int PORT = 50001;

	private LocalUI localUI;
	private RemoteUI remoteUI;

	private SocketConnection socketConnection;
	private SocketListener socketListener;

	private DeltaTime deltaT = new DeltaTime(5);

	public MainController(LocalUI localUI, RemoteUI remoteUI)
	{
		this.localUI = localUI;
		this.remoteUI = remoteUI;

		socketConnection = new SocketConnection();
		socketListener = new SocketListener();
		setDaemon(true);
	}

	public void run()
	{
		while (true)
		{
			if (localUI.hasPendingConnectionRequest())
			{
				String URL =  localUI.getConnectionRequest();
				socketConnection.establishConnection(URL, PORT);
				socketListener.interrupt();
			}
			if (!socketConnection.isConnected() && !socketListener.isAlive())
			{
				socketListener = new SocketListener(new listener(), PORT);
				socketListener.start();
			}


			deltaT.update();
			if (!deltaT.enoughTimePassed())
			{
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

	class listener implements NetworkListener
	{
		public void receivedConnection(Socket socket)
		{
			Logger logger = AppLogger.getInstance();
			logger.log(Level.ALL, "Received connection request");
			if (!socketConnection.isConnected())
			{
				logger.log(Level.ALL, "Successfully connected to socket: " + socket.getInetAddress());
				socketConnection.setSocket(socket);
			}
		}
	}
}
