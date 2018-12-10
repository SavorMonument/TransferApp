package logic.connection;

import logic.ConnectCloseEvent;
import logic.api.BusinessEvents;
import logic.api.ConnectionResolver;
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
		this.businessEvents = businessEvents;
		this.connectionResolver = connectionResolver;
		this.remoteAddress = remoteAddress;
		this.mainConnectCloseEvent = connectCloseEvent;
	}

	public void go()
	{
		if (resolveConnections())
		{
			transmitterController = new MessageTransmitterController(mainConnection, transmittingConnection,
					businessEvents, mainConnectCloseEvent);

			receiverController = new MessageReceiverController(mainConnection, receivingConnection,
					businessEvents, mainConnectCloseEvent);

			receiverController.startListening();

			UIFileEventsHandler handler = new UIFileEventsHandler();
			LocalController.setFileEvents(handler);
			RemoteController.setFileEvents(handler);
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
			transmittingConnection = connectionResolver.attemptNextConnection(remoteAddress);
			receivingConnection = connectionResolver.attemptNextConnection(remoteAddress);

		} catch (IOException e)
		{
			successful = false;
//			e.printStackTrace();
		}

		return successful;
	}




}
