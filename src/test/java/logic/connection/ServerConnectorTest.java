package logic.connection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;

public class ServerConnectorTest
{
	ServerConnector serverConnector;
	ConnectionResolver connectionResolver;

	@Before
	public void setUp()
	{
		connectionResolver = Mockito.mock(ConnectionResolver.class);
		serverConnector = new ServerConnector(connectionResolver);
	}

	@Test(expected = IOException.class)
	public void exceptionOnListen() throws IOException, InterruptedException
	{
		Mockito.when(connectionResolver.listenNextConnection()).thenThrow(IOException.class);
		serverConnector.listen();
	}

	@Test
	public void normalConnection() throws IOException
	{
		Connection connection = Mockito.mock(Connection.class);
		Mockito.when(connectionResolver.listenNextConnection()).thenReturn(connection);
		Mockito.when(connectionResolver.listenNextConnection(anyInt())).thenReturn(connection);

		Connections connections = serverConnector.listen();

		assertEquals(connection, connections.getMainConnection());
		assertEquals(connection, connections.getFileReceivingConnection());
		assertEquals(connection, connections.getFileTransmittingConnection());
	}

	@Test
	public void stopListeningMiddleOfConnecting() throws IOException, InterruptedException
	{
		Connection connection = Mockito.mock(Connection.class);
		Mockito.when(connectionResolver.listenNextConnection()).thenReturn(connection);
		Mockito.when(connectionResolver.listenNextConnection(anyInt())).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				Thread.sleep(2000);
				return null;
			}
		});

		new Thread(() ->
		{
			try
			{
				serverConnector.listen();
			} catch (IOException e)
			{
//				e.printStackTrace();
			}
		}).start();

		Thread.sleep(1000);
		serverConnector.stop();

		Mockito.verify(connection, Mockito.times(1)).close();
	}
}