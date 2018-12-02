package network;

import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class SocketSenderTest
{
	private Socket mockSocket;

	private SocketSender socketSender;
	private ByteArrayOutputStream byteArrayOutputStream;

	@Before
	public void setUp() throws Exception
	{
		mockSocket = Mockito.mock(Socket.class);
		Mockito.when(mockSocket.isConnected()).thenReturn(true);

		byteArrayOutputStream = new ByteArrayOutputStream();
		Mockito.when(mockSocket.getOutputStream()).thenReturn(byteArrayOutputStream);


		socketSender = new SocketSender(mockSocket);
	}

	@After
	public void tearDown() throws Exception
	{
		mockSocket = null;
		socketSender = null;
		byteArrayOutputStream = null;
	}

	@Test
	public void transmitMessage()
	{
		String message = "Test";

		socketSender.transmitMessage(message);

		assertEquals(message, byteArrayOutputStream.toString());
	}
}