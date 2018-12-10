package logic;

import logic.client.ClientController;
import logic.client.Controller;
import logic.client.ServerController;
import network.*;
import window.AppLogger;
import window.UIEvents;
import window.connection.ConnectionController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogicController extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private String downloadPath = "C:\\Users\\Goia\\Desktop\\test_folder";

	private enum State
	{
		CONNECTED,
		CONNECTING,
		DISCONNECTED,
		DISCONNECTING;
	}

	private State state = State.DISCONNECTED;

	Controller currentController;

	private ConnectionResolver connectionResolver;
	private BusinessEvents businessEvents;

	private ConnectCloseEvent connectCloseEvent;

	public LogicController(BusinessEvents businessEvents)
	{
		this.businessEvents = businessEvents;
		this.connectCloseEvent = new DisconnectEvent();

		ConnectionController.setConnectionEventHandler(new ConnectionEventsHandler());

		setDaemon(true);
	}

	public void run()
	{

		while (true)
		{
			try
			{
				if (state == State.DISCONNECTED)
				{
					connectionResolver = new ConnectionResolver();
					Connection connection;

					connection = connectionResolver.listenNextConnection();
					System.out.println("Here");
					state = State.CONNECTED;
					currentController = new ServerController(connection, connectionResolver, businessEvents, new DisconnectEvent());
					currentController.go();
				}

				Thread.sleep(3000);
			} catch (InterruptedException | IOException e)
			{
//				e.printStackTrace();
			}

		}
	}

	class ConnectionEventsHandler implements UIEvents.ConnectionEvents
	{
		@Override
		public void attemptConnectionToHost(String host)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					if (state == State.DISCONNECTED)
					{
						LOGGER.log(Level.ALL, "Connection request to: " + host);

						try
						{
							if (null != connectionResolver)
								connectionResolver.stopListening();
							state = State.CONNECTED;
							currentController = new ClientController(businessEvents,  new DisconnectEvent(), InetAddress.getByName(host));
							currentController.go();
						} catch (UnknownHostException e)
						{
							LOGGER.log(Level.WARNING, "Connection request denied: bad ip " + e.getMessage());
						}

					} else
						LOGGER.log(Level.WARNING, "Connection request denied: Already connected");
				}
			}).start();

		}

		@Override
		public void disconnect()
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					if (state == State.CONNECTED)
					{
						LOGGER.log(Level.ALL, "Disconnecting: ");
						connectCloseEvent.disconnect("Received disconnect request");
					} else
						LOGGER.log(Level.ALL, "Disconnect request denied, not connected");
				}
			}).start();

		}
	}

	class DisconnectEvent implements ConnectCloseEvent
	{
		@Override
		public void disconnect(String message)
		{
			LOGGER.log(Level.WARNING, "Connection disrupted, resetting connections: " + message);

			state = State.DISCONNECTING;
			new Thread(() -> currentController.close()).start();
			state = State.DISCONNECTED;
		}
	}

}

