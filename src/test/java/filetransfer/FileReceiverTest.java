package filetransfer;

import filesistem.FileException;
import filetransfer.api.TransferFileOutput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import network.ConnectionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;

public class FileReceiverTest
{
	FileReceiver fileReceiver;

	TransferInput transferInput;
	TransferOutput transferOutput;
	TransferFileOutput fileOutput;

	@Before
	public void setUp()
	{
		transferInput = Mockito.mock(TransferInput.class);
		transferOutput = Mockito.mock(TransferOutput.class);
		fileOutput = Mockito.mock(TransferFileOutput.class);
	}

	@Test(expected = FileException.class)
	public void fileOpenFailure() throws IOException
	{
		int size = 1;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		fileReceiver.transfer();

		Mockito.doThrow(IOException.class).when(fileOutput).open();
	}

	@Test
	public void outputCodes() throws IOException
	{
		int[] expectedCodes = new int[]{1, -1};//Start code than error
		int[] receivedCodes = new int[2];

		int size = 50;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.doAnswer(new Answer()
		{
			int i;

			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				receivedCodes[i] = (int) invocationOnMock.getArguments()[0];
				i++;
				return null;
			}
		}).when(transferOutput).transmitByte(anyByte());

		Mockito.doThrow(FileException.class).when(fileOutput).writeToFile((byte[]) notNull(),
				anyInt());

		Mockito.when(transferInput.available()).thenReturn(1);

		try
		{
			fileReceiver.transfer();
		} catch (FileException e)
		{
		}

		assertArrayEquals(expectedCodes, receivedCodes);
	}

	@Test
	public void transferSmallAmountOfData() throws IOException
	{
		int fileSize = 50;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, fileSize);

		Mockito.when(transferInput.available()).thenReturn(fileSize);
		Mockito.when(transferInput.read((byte[]) notNull(), anyInt())).thenReturn(fileSize);

		fileReceiver.transfer();
	}

	@Test(expected = ConnectionException.class)
	public void notReceivingEnoughData_TimeOut() throws IOException
	{
		int size = 50;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.when(transferInput.available()).thenReturn(0);
		Mockito.when(transferInput.read((byte[]) notNull(), anyInt())).thenReturn(size - 1);

		fileReceiver.transfer();
	}

	@Test
	public void successfulTransferMultipleReads() throws IOException
	{
		int size = 100;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.when(transferInput.available())
				.thenAnswer((Answer<Integer>) invocationOnMock -> size / 4);

		Mockito.when(transferInput.read((byte[]) notNull(), anyInt())).thenReturn(size / 4);

		fileReceiver.transfer();
	}

	@Test
	public void successfulTransferMultipleWrites() throws IOException
	{
		int size = 100;
		int sum = 0;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.when(transferInput.available()).thenAnswer(
				(Answer<Integer>) invocationOnMock -> size / 4);

		Mockito.doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock)
			{
				assertEquals(size / 4, (int) invocationOnMock.getArguments()[1]);
				return null;
			}
		}).when(fileOutput).writeToFile((byte[]) notNull(), anyInt());

		Mockito.when(transferInput.read((byte[]) notNull(), anyInt())).thenReturn(size / 4);
		fileReceiver.transfer();
	}
}