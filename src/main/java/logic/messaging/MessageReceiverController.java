package logic.messaging;

import org.jetbrains.annotations.NotNull;
import logic.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.connection.Connection.StringReceiver;
import logic.connection.Connections;
import logic.messaging.actions.Action;
import logic.messaging.actions.ActionFactory;
import logic.messaging.messages.MessageFactory;
import logic.messaging.messages.NetworkMessage;
import network.ConnectionException;
import window.AppLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReceiverController
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private StringReceiver messageReceiver;
	private BusinessEvents businessEvents;
	private ConnectCloseEvent connectCloseEvent;

	private MessageReceiverThread listener;

	private MessageFactory messageFactory;
	private ActionFactory actionFactory;

	public MessageReceiverController(@NotNull Connections connections,
									 @NotNull BusinessEvents businessEvents,
									 @NotNull ConnectCloseEvent connectCloseEvent)
	{
		assert null != connections : "Invalid connections";
		assert null != businessEvents : "Invalid BusinessEvents";
		assert null != connectCloseEvent : "Need an actual event handler";

		this.messageReceiver = connections.getMainConnection().getMessageReceiver();
		this.businessEvents = businessEvents;
		this.connectCloseEvent = connectCloseEvent;

		messageFactory = new MessageFactory();
		actionFactory = new ActionFactory(businessEvents, connectCloseEvent, connections.getFileTransmittingConnection());
	}

	private void checkMessages()
	{
		String codedMessage;
		try
		{
			codedMessage = messageReceiver.pullLineBlocking();
			NetworkMessage networkMessage = messageFactory.resolveMessage(codedMessage);
			Action action = actionFactory.getReceiverAction(networkMessage);

			new Thread(() -> action.performAction()).start();

		} catch (ConnectionException e)
		{
			LOGGER.log(Level.WARNING, "Connection error: " + e.getMessage());
			connectCloseEvent.disconnect("Connection error, disconnecting");
			stopListening();
		}
	}

	public void startListening()
	{
		listener = new MessageReceiverThread();

		listener.setDaemon(true);
		listener.start();
	}

	public void stopListening()
	{
		listener.interrupt();
	}

	class MessageReceiverThread extends Thread
	{
		@Override
		public void run()
		{
			LOGGER.log(Level.ALL, "Started listening for messages on the socket");
			while (!isInterrupted())
			{
				checkMessages();
			}
		}
	}
}
