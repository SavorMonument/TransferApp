package logic.connection;

import logic.ConnectCloseEvent;
import logic.BusinessEvents;
import logic.messaging.MessageReceiverController;
import logic.messaging.MessageTransmitterController;
import window.local.LocalController;
import window.remote.RemoteController;

import java.io.IOException;

public class ServerController extends Controller
{
	private ConnectionResolver connectionResolver;

	public ServerController(Connection initialConnection, ConnectionResolver resolver,
							BusinessEvents businessEvents, ConnectCloseEvent connectCloseEvent)
	{
		super(businessEvents, connectCloseEvent);

		this.mainConnection = initialConnection;
		this.connectionResolver = resolver;
	}

	@Override
	public void go()
	{
		if (resolveConnection())
		{
			transmitterController = new MessageTransmitterController(mainConnection, fileConnectionTwo,
					businessEvents, mainConnectCloseEvent);
			receiverController = new MessageReceiverController(mainConnection, fileConnectionOne,
					businessEvents, mainConnectCloseEvent);
			receiverController.startListening();

			UIFileEventsHandler handler = new UIFileEventsHandler();
			LocalController.setFileEvents(handler);
			RemoteController.setFileEvents(handler);

			registerTransmittingCounter(fileConnectionOne.getMessageTransmitter());
			registerReceivingCounter(fileConnectionTwo.getMessageReceiver());
		} else
			mainConnectCloseEvent.disconnect("Connection establish failed");
	}

	private boolean resolveConnection()
	{
		boolean successful = true;
		try
		{
			fileConnectionOne = connectionResolver.listenNextConnection(10_000);
			fileConnectionTwo = connectionResolver.listenNextConnection(10_000);

		} catch (IOException e)
		{
			successful = false;
		}

		return successful;
	}
}
