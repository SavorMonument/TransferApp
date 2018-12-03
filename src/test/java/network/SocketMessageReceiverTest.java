package network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class SocketMessageReceiverTest
{
	SocketMessageReceiver socketMessageReceiver;
	SocketReceiver socketReceiver;

	@Before
	public void setUp() throws Exception
	{
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

		SocketReceiver socketReceiver = new SocketReceiver(new StringBufferInputStream(networkMessage.toString()));
		socketMessageReceiver = new SocketMessageReceiver(socketReceiver);

		assertEquals(networkMessage.toString(), socketMessageReceiver.pullMessage().toString());
	}

	@Test
	public void pullInvalidMessage() throws IOException
	{
		String testMessage = "Test";

		socketReceiver = new SocketReceiver(new StringBufferInputStream(testMessage));
		socketMessageReceiver = new SocketMessageReceiver(socketReceiver);

		assertNull(socketMessageReceiver.pullMessage());
	}
}