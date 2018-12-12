package filetransfer;

import filetransfer.api.TransferFileOutput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.notNull;

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

	@Test
	public void fileOpenFailure() throws IOException
	{
		int size = 1;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.doThrow(IOException.class).when(fileOutput).open();

//		assertFalse(fileReceiver.transfer());
	}

	@Test
	public void transferSmallAmountOfData() throws IOException
	{
		int inputBufferSize = 100;
		int size = 50;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.when(transferInput.getBufferSize()).thenReturn(inputBufferSize);
		Mockito.when(transferInput.available()).thenReturn(size);
		Mockito.when(transferInput.read((byte[]) notNull(), 0)).thenReturn(size);


//		assertTrue(fileReceiver.transfer());
	}

	@Test
	public void notReceivingEnoughData_TimeOut() throws IOException
	{
		int inputBufferSize = 100_000;
		int size = 50;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.when(transferInput.getBufferSize()).thenReturn(inputBufferSize);
		Mockito.when(transferInput.available()).thenReturn(49);
		Mockito.when(transferInput.read((byte[]) notNull(), 0)).thenReturn(size);


//		assertFalse(fileReceiver.transfer());
	}

	@Test
	public void successfulTransferWithRequest() throws IOException
	{
		int inputBufferSize = 100_000;
		int size = 100;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.when(transferInput.getBufferSize()).thenReturn(inputBufferSize);
		Mockito.when(transferInput.available()).thenAnswer(new Answer<Integer>()
		{
			private int count;
			@Override
			public Integer answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				if (count < 5)
				{
					count++;
					return 99;
				}
				return 100;
			}
		});

		Mockito.when(transferInput.read((byte[]) notNull(), 0)).thenReturn(size);

//		assertTrue(fileReceiver.transfer());
	}

	@Test
	public void successfulTransfer() throws IOException
	{
		int inputBufferSize = 100_000;
		int size = 100;

		FileReceiver fileReceiver = new FileReceiver(transferInput, transferOutput,
				fileOutput, size);

		Mockito.when(transferInput.getBufferSize()).thenReturn(inputBufferSize);
		Mockito.when(transferInput.available()).thenAnswer(new Answer<Integer>()
		{
			private int count;
			@Override
			public Integer answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				if (count < 5)
				{
					count++;
					return 99;
				}
				return 100;
			}
		});

		Mockito.when(transferInput.read((byte[]) notNull(), 0)).thenReturn(size);

//		assertTrue(fileReceiver.transfer());
	}
}