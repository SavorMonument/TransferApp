package logic.client;

import logic.*;
import network.ConnectionResolver;
import window.local.LocalController;
import window.remote.RemoteController;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

public class ClientController extends Controller implements Closeable
{
	private InetAddress remoteAddress;

	public ClientController(BusinessEvents businessEvents, ConnectCloseEvent connectCloseEvent, InetAddress remoteAddress)
	{
		this.businessEvents = businessEvents;
		this.remoteAddress = remoteAddress;
		this.mainConnectCloseEvent = connectCloseEvent;
	}

	public void go()
	{
		if (resolveConnections())
		{
			transmitterController = new TransmittingController(mainConnection, transmittingConnection,
					businessEvents, mainConnectCloseEvent);

			receiverController = new ReceiverController(mainConnection, receivingConnection,
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
		ConnectionResolver connectionResolver = new ConnectionResolver();

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
