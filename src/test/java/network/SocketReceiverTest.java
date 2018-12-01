package network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
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
	private SocketReceivingEvents mockEvents;

	private SocketReceiver socketReceiver;

	@Before
	public void setUp() throws Exception
	{
		mockSocket = Mockito.mock(Socket.class);
		mockEvents = Mockito.mock(SocketReceivingEvents.class);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void updateFileList() throws IOException, InterruptedException
	{
		List<String> expected = new ArrayList<>();
		expected.add("A");
		expected.add("B");

		socketInput = new StringBufferInputStream(String.format("UPDATE_FILE_LIST\n%s\n", expected.toString()));
		Mockito.when(mockSocket.getInputStream()).thenReturn(socketInput);
		socketReceiver = new SocketReceiver(mockSocket, mockEvents);
		socketReceiver.start();

		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		Thread.sleep(1000);
		Mockito.verify(mockEvents).updateRemoteFileList(captor.capture());

		System.out.println(captor.getValue());
		assertEquals(expected, captor.getValue());
	}
}