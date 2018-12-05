package network;

import logic.NetworkMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class SocketMessageReceiverTest
{
	Socket socket;
	SocketMessageReceiver socketMessageReceiver;

	@Before
	public void setUp() throws Exception
	{
		socket = Mockito.mock(Socket.class);
		Mockito.when(socket.isConnected()).thenReturn(true);
	}

	@After
	public void tearDown() throws Exception
	{
		socketMessageReceiver = null;
	}

	@Test
	public void pullMessage() throws IOException
	{
		NetworkMessage.MessageType type = NetworkMessage.MessageType.SEND_FILE;
		String message = "Test";
		NetworkMessage networkMessage = new NetworkMessage(type, message);

		Mockito.when(socket.getInputStream()).thenReturn(new StringBufferInputStream(networkMessage.toString()));
		socketMessageReceiver = new SocketMessageReceiver(socket);

		assertEquals(networkMessage.toString(), socketMessageReceiver.pullMessage().toString());
	}

	@Test
	public void pullInvalidMessage() throws IOException
	{
		String testMessage = "Test";

		Mockito.when(socket.getInputStream()).thenReturn(new StringBufferInputStream(testMessage));
		socketMessageReceiver = new SocketMessageReceiver(socket);

		assertNull(socketMessageReceiver.pullMessage());
	}
}