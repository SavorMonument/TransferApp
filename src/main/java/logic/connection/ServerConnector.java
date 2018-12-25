package logic.connection;

import network.connection.NetworkConnectionResolver;

import java.io.IOException;

public class ServerConnector
{
	private ConnectionResolver connectionResolver;

	private Connection mainConnection;
	private Connection fileConnectionOne;
	private Connection fileConnectionTwo;

	public ServerConnector(ConnectionResolver connectionResolver)
	{
		this.connectionResolver = connectionResolver;
	}

	public Connections listen() throws IOException
	{
		mainConnection = connectionResolver.listenNextConnection();
		fileConnectionOne = connectionResolver.listenNextConnection(10_000);
		fileConnectionTwo = connectionResolver.listenNextConnection(10_000);

		return new Connections(mainConnection, fileConnectionOne, fileConnectionTwo);
	}

	public void stop()
	{
		connectionResolver.stopListening();

		if (null != fileConnectionTwo)
		{
			fileConnectionTwo.close();
		}

		if (null != fileConnectionOne)
		{
			fileConnectionOne.close();
		}

		if (null != mainConnection)
		{
			mainConnection.close();
		}
	}
}
