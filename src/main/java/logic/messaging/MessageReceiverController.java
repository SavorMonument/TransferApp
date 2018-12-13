package logic.messaging;

import com.sun.istack.internal.NotNull;
import logic.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.connection.Connection;
import logic.connection.Connection.StringReceiver;
import logic.messaging.messages.MessageFactory;
import logic.messaging.messages.NetworkMessage;
import window.AppLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReceiverController
{
	private static final Logger LOGGER = AppLogger.getInstance();
	//TODO: Have this passed trough the main socket(so you can have multiple file transferring at the sam time)

	private Connection mainConnection;
	private Connection fileConnection;

	private StringReceiver messageReceiver;
	private BusinessEvents businessEvents;
	private ConnectCloseEvent connectEvent;

	private MessageReceiverThread listener;
	private MessageFactory messageFactory;

	public MessageReceiverController(@NotNull Connection mainConnection,
									 @NotNull Connection fileConnection,
									 @NotNull BusinessEvents businessEvents,
									 @NotNull ConnectCloseEvent connectEvent)
	{
		assert null != mainConnection && mainConnection.isConnected() : "Invalid main connection";
		assert null != fileConnection && fileConnection.isConnected() : "Invalid main connection";
		assert null != businessEvents : "Invalid BusinessEvents";
		assert null != connectEvent : "Need an actual event handler";

		this.mainConnection = mainConnection;
		this.fileConnection = fileConnection;

		this.messageReceiver = mainConnection.getMessageReceiver();
		this.businessEvents = businessEvents;
		this.connectEvent = connectEvent;

		messageFactory = new MessageFactory(fileConnection);
	}

	private void checkMessages()
	{
		String codedMessage;
		try
		{
			codedMessage = messageReceiver.pullLineBlocking();
			if (null != codedMessage)
			{
				NetworkMessage networkMessage = messageFactory.resolveMessage(codedMessage);
				networkMessage.doAction(businessEvents);
			}
			else
			{
				System.out.println("Null message");
			}

		} catch (ConnectionException e)
		{
			LOGGER.log(Level.WARNING, "Connection error: " + e.getMessage());
			connectEvent.disconnect("Connection error, disconnecting");
			stopListening();
//			e.printStackTrace();
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
