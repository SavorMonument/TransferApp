package network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class SocketMessageTransmitterTest
{
	SocketMessageTransmitter socketMessageTransmitter;
	ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception
	{
		outputStream = new ByteArrayOutputStream();
		socketMessageTransmitter = new SocketMessageTransmitter(new SocketTransmitter(outputStream));
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void transmitMessage()
	{
		NetworkMessage.MessageType type = NetworkMessage.MessageType.SEND_FILE;
		String message = "Test";
		NetworkMessage networkMessage = new NetworkMessage(type, message);

		socketMessageTransmitter.transmitMessage(networkMessage);

		assertEquals(networkMessage.toString(), outputStream.toString());
	}
}