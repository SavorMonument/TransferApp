package network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

		Mockito.when(mockSocket.getInputStream()).thenReturn(new BufferedInputStream(new StringBufferInputStream(message)));
		socketReceiver = new SocketReceiver(mockSocket);

		assertTrue(socketReceiver.hasMessage());
	}

	@Test
	public void getLine() throws IOException, InterruptedException
	{
		String message = "test";

		Mockito.when(mockSocket.getInputStream()).thenReturn(new BufferedInputStream(new StringBufferInputStream(message)));
		socketReceiver = new SocketReceiver(mockSocket);

		assertEquals(message, socketReceiver.getLine());
	}
}