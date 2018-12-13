package logic.connection;

import logic.ConnectCloseEvent;
import logic.BusinessEvents;
import logic.messaging.MessageReceiverController;
import logic.messaging.MessageTransmitterController;
import window.local.LocalController;
import window.remote.RemoteController;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

public class ClientController extends Controller implements Closeable
{
	private ConnectionResolver connectionResolver;
	private InetAddress remoteAddress;

	public ClientController(BusinessEvents businessEvents, ConnectCloseEvent connectCloseEvent,
							ConnectionResolver connectionResolver, InetAddress remoteAddress)
	{
		super(businessEvents, connectCloseEvent);

		this.connectionResolver = connectionResolver;
		this.remoteAddress = remoteAddress;
	}

	public void go()
	{
		if (resolveConnections())
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
		{
			mainConnectCloseEvent.disconnect("Could not establish connection to remote ip: " + remoteAddress.getHostAddress());
		}
	}

	private boolean resolveConnections()
	{
		boolean successful = true;

		try
		{
			mainConnection = connectionResolver.attemptNextConnection(remoteAddress);
			fileConnectionTwo = connectionResolver.attemptNextConnection(remoteAddress);
			fileConnectionOne = connectionResolver.attemptNextConnection(remoteAddress);

		} catch (IOException e)
		{
			successful = false;
//			e.printStackTrace();
		}

		return successful;
	}




}
