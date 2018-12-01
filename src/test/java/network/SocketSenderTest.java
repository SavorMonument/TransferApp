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
		byteArrayOutputStream = new ByteArrayOutputStream();

		mockSocket = Mockito.mock(Socket.class);
		Mockito.when(mockSocket.getOutputStream()).thenReturn(new BufferedOutputStream(byteArrayOutputStream));

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
	public void updateRemoteFileList()
	{
		StringBuilder expected = new StringBuilder();
		List<String> testNames = new ArrayList<>();
		testNames.add("A");
		testNames.add("B");

		socketSender.updateRemoteFileList(testNames);

		expected.append("UPDATE_FILE_LIST")
				.append("\n")
				.append(testNames.toString())
				.append("\n");

		assertEquals(expected.toString(), byteArrayOutputStream.toString());
	}

	@Test
	public void requestFileTransfer()
	{
	}
}