package network;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;

public class SocketReceiverTest
{
	private Socket mockSocket;
	private InputStream socketInput;

	private SocketReceiver socketReceiver;

	@Before
	public void setUp() throws Exception
	{
		mockSocket = Mockito.mock(Socket.class);
		Mockito.when(mockSocket.isConnected()).thenReturn(true);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void hasMessage() throws IOException, InterruptedException
	{
		String message = "test";

		socketReceiver = new SocketReceiver(new StringBufferInputStream(message));

		assertTrue(socketReceiver.hasBytes());
	}

	@Test
	public void getLine() throws IOException, InterruptedException
	{
		socketReceiver = new SocketReceiver(new ByteInputStream(new byte[]{1}, 1));

		assertEquals((int) 1, socketReceiver.read());
	}
}