package network;

import logic.NetworkMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.net.Socket;

import static org.junit.Assert.*;

public class SocketMessageTransmitterTest
{
	Socket socket;
	SocketMessageTransmitter socketMessageTransmitter;
	ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception
	{
		outputStream = new ByteArrayOutputStream();

		socket = Mockito.mock(Socket.class);
		Mockito.when(socket.getOutputStream()).thenReturn(outputStream);
		Mockito.when(socket.isConnected()).thenReturn(true);

		socketMessageTransmitter = new SocketMessageTransmitter(socket);
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