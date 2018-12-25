package logic;

import logic.connection.*;
import logic.messaging.ControllerResolver;
import network.connection.NetworkConnectionResolver;
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

	private ControllerResolver currentController;
	private ServerConnector serverConnector;

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
					businessEvents.printMessageOnDisplay("Started listening for connections");

					serverConnector = new ServerConnector(new NetworkConnectionResolver());
					Connections connections = serverConnector.listen();
					updateState(State.CONNECTING);

					currentController = new ControllerResolver(connections, businessEvents, new DisconnectEvent());
					currentController.initialize();
					updateState(State.CONNECTED);
				}
				Thread.sleep(2000);
			} catch (InterruptedException | IOException e)
			{
				//I'm going to get exceptions here because if the user attempts an outgoing connection
				//it's going to close the listening socket
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
					if (programState == State.DISCONNECTED && null == currentController)
					{
						serverConnector.stop();
						updateState(State.CONNECTING);

						LOGGER.log(Level.ALL, "Connection request to: " + host);
						try
						{
							businessEvents.printMessageOnDisplay("Attempting connection to host");
							ClientConnector clientConnector = new ClientConnector(
									new NetworkConnectionResolver(),
									InetAddress.getByName(host));

							Connections connections = clientConnector.connect();
							currentController = new ControllerResolver(connections, businessEvents, new DisconnectEvent());
							currentController.initialize();

							updateState(State.CONNECTED);
						} catch (UnknownHostException e)
						{
							LOGGER.log(Level.WARNING, "Connection request denied: bad ip " + e.getMessage());
							businessEvents.printMessageOnDisplay("Connection request denied: bad ip");
							updateState(State.DISCONNECTED);
						} catch (IOException e)
						{
							LOGGER.log(Level.WARNING, "Connection request denied: " + e.getMessage());
							businessEvents.printMessageOnDisplay("Connection request denied");
							updateState(State.DISCONNECTED);
						}
					} else
						LOGGER.log(Level.WARNING, "Connection request denied: Already connected");
				}
			}).start();

		}

		@Override
		public void disconnect()
		{
			Thread disconnectThread = new Thread(new Runnable()
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
			});

			disconnectThread.setDaemon(true);
			disconnectThread.start();
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
				businessEvents.printMessageOnDisplay(message);
				LOGGER.log(Level.WARNING, "Connection disrupted, resetting connections: " + message);

				updateState(State.DISCONNECTING);
				Thread disconnectThread = new Thread(() ->
				{
					currentController.close();
					updateState(State.DISCONNECTED);
					currentController = null;
				});

				disconnectThread.setDaemon(true);
				disconnectThread.start();
			}
		}
	}

}

