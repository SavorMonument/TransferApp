package network;

import network.streaming.SocketOutputStream;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class SocketSenderTest
{
	Socket mockSocket;
	SocketOutputStream socketSender;
	ByteArrayOutputStream byteArrayOutputStream;

	@Before
	public void setUp() throws Exception
	{
		byteArrayOutputStream = new ByteArrayOutputStream();

		mockSocket = Mockito.mock(Socket.class);
		Mockito.when(mockSocket.isConnected()).thenReturn(true);
		Mockito.when(mockSocket.getOutputStream()).thenReturn(byteArrayOutputStream);

		socketSender = new SocketOutputStream(mockSocket);
	}

	@After
	public void tearDown() throws Exception
	{
		socketSender = null;
		byteArrayOutputStream = null;
	}

	@Test
	public void transmitMessage() throws IOException
	{
		byte[] bytes = "Test".getBytes();

		socketSender.transmitBytes(bytes);

		assertArrayEquals(bytes, byteArrayOutputStream.toByteArray());
	}
}