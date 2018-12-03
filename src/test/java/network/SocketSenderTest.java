package network;

import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class SocketSenderTest
{
	private SocketTransmitter socketSender;
	private ByteArrayOutputStream byteArrayOutputStream;

	@Before
	public void setUp() throws Exception
	{

		byteArrayOutputStream = new ByteArrayOutputStream();
		socketSender = new SocketTransmitter(byteArrayOutputStream);
	}

	@After
	public void tearDown() throws Exception
	{
		socketSender = null;
		byteArrayOutputStream = null;
	}

	@Test
	public void transmitMessage()
	{
		byte[] bytes = "Test".getBytes();

		socketSender.transmitBytes(bytes);

		assertArrayEquals(bytes, byteArrayOutputStream.toByteArray());
	}

	@Test
	public void temp() throws IOException
	{
		SocketReceiver socketReceiver = new SocketReceiver(new StringBufferInputStream("AAaajl;hjkkkkkkkkkk"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(socketReceiver));

		System.out.println(reader.read());
		System.out.println(reader.readLine());


		System.out.println("Here");
	}
}