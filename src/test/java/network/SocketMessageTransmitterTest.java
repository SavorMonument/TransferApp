package network;

import network.messaging.SocketStringTransmitter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketMessageTransmitterTest
{
	Socket socket;
	SocketStringTransmitter socketMessageTransmitter;
	ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception
	{
		outputStream = new ByteArrayOutputStream();

		socket = Mockito.mock(Socket.class);
		Mockito.when(socket.getOutputStream()).thenReturn(outputStream);
		Mockito.when(socket.isConnected()).thenReturn(true);

		socketMessageTransmitter = new SocketStringTransmitter(socket);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void transmitMessage() throws IOException
	{
	}
}