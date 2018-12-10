package logic;

import logic.api.BusinessEvents;
import logic.api.Connection;
import logic.api.ConnectionResolver;
import logic.connection.ClientController;
import logic.connection.Controller;
import logic.connection.ServerController;
import network.*;
import window.AppLogger;
import window.UIEvents;
import window.connection.ConnectionController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogicController extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private enum State
	{
		CONNECTED,
		CONNECTING,
		DISCONNECTED,
		DISCONNECTING;
	}

	private State programState;

	private Controller currentController;
	private ConnectionResolver connectionResolver;
	private BusinessEvents businessEvents;

	private ConnectCloseEvent connectCloseEvent;

	public LogicController(BusinessEvents businessEvents)
	{
		this.businessEvents = businessEvents;
		this.connectCloseEvent = new DisconnectEvent();

		ConnectionController.setConnectionEventHandler(new ConnectionEventsHandler());
		updateState(State.DISCONNECTED);
		setDaemon(true);
	}

	public void run()
	{

		while (true)
		{
			try
			{
				if (programState == State.DISCONNECTED)
				{
					connectionResolver = new NetworkConnectionResolver();
					Connection connection;
					connection = connectionResolver.listenNextConnection();

					updateState(State.CONNECTING);

					currentController = new ServerController(connection, connectionResolver, businessEvents, new DisconnectEvent());
					currentController.go();

					if (programState == State.CONNECTING)
						updateState(State.CONNECTED);
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
					if (programState == State.DISCONNECTED)
					{
						LOGGER.log(Level.ALL, "Connection request to: " + host);

						try
						{
							if (null != connectionResolver)
								connectionResolver.stopListening();

							updateState(State.CONNECTING);

							currentController = new ClientController(businessEvents, new DisconnectEvent(),
									new NetworkConnectionResolver(), InetAddress.getByName(host));
							currentController.go();

							if (programState == State.CONNECTING)
							{
								updateState(State.CONNECTED);
							}
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
					if (programState == State.CONNECTED)
					{
						LOGGER.log(Level.ALL, "Disconnecting");
						connectCloseEvent.disconnect("Received disconnect request");
					} else
						LOGGER.log(Level.ALL, "Disconnect request denied, not connected");
				}
			}).start();
		}
	}

	private void updateState(State state)
	{
		businessEvents.setConnectionState(state.toString());
		programState = state;
	}

	class DisconnectEvent implements ConnectCloseEvent
	{
		@Override
		public void disconnect(String message)
		{
			if (programState != State.DISCONNECTING)
			{
				LOGGER.log(Level.WARNING, "Connection disrupted, resetting connections: " + message);
				updateState(State.DISCONNECTING);
				new Thread(() ->
				{
					currentController.close();
					updateState(State.DISCONNECTED);

				}).start();
			}
		}
	}

}

