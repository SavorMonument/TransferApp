package logic.connection;

import java.io.IOException;
import java.net.InetAddress;

public class ClientConnector
{
	private ConnectionResolver connectionResolver;
	private InetAddress remoteAddress;

	private Connection mainConnection;
	private Connection fileConnectionOne;
	private Connection fileConnectionTwo;

	public ClientConnector(ConnectionResolver connectionResolver, InetAddress remoteAddress)
	{
		this.connectionResolver = connectionResolver;
		this.remoteAddress = remoteAddress;
	}

	public Connections connect() throws IOException
	{
		resolveConnections();
		return new Connections(mainConnection, fileConnectionOne, fileConnectionTwo);
	}

	private void resolveConnections() throws IOException
	{
		mainConnection = connectionResolver.attemptNextConnection(remoteAddress);
		fileConnectionTwo = connectionResolver.attemptNextConnection(remoteAddress);
		fileConnectionOne = connectionResolver.attemptNextConnection(remoteAddress);
	}
}
