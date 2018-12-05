package filetransfer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.NotNull;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
//OK I don't know how to test this, I give up -------------------------------------
public class FileTransmitterTest
{
	final int numOfTestValues = 100000;
	byte[] testValues = new byte[numOfTestValues];

	FileTransmitter fileTransmitter;


	TransferOutput mockOutput;
	TransferInput mockInput;
	TransferFileInput mockFileInput;

	@Before
	public void setUp() throws Exception
	{
		mockOutput = Mockito.mock(TransferOutput.class);
		mockInput = Mockito.mock(TransferInput.class);
		mockFileInput = Mockito.mock(TransferFileInput.class);

		fileTransmitter = new FileTransmitter(mockOutput, mockInput, mockFileInput);

		Random random = new Random();
		random.nextBytes(testValues);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test// THIS ONLY TOOK A BILLION YEARS
	public void transmitASmallNumberOfBytes() throws IOException
	{
		int numOfBytes = 5;

		Mockito.when(mockFileInput.read((byte[]) notNull(), any(Integer.class))).thenAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				byte[] buffer = (byte[]) invocationOnMock.getArguments()[0];
				System.arraycopy(testValues, 0, buffer, 0, numOfBytes);
				return numOfBytes;
			}
		});

		Mockito.doAnswer(new Answer()
		{
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
			{
				byte[] buffer = (byte[]) invocationOnMock.getArguments()[0];
				int bytesToTransmit = (int) invocationOnMock.getArguments()[1];

				assertEquals(numOfBytes, bytesToTransmit);
				assertEquals(buffer, Arrays.copyOf(testValues, numOfBytes));
				return null;
			}
		}).when(mockOutput).transmitBytes((byte[]) notNull(), any(Integer.class));

		fileTransmitter.start();
	}
//
//	@Test
//	public void transmitALotMoreBytes() throws IOException
//	{
//		int numOfBytes = numOfTestValues;
//
//		Mockito.when(mockFileInput.read((byte[]) notNull(), any(Integer.class))).thenAnswer(new Answer<Object>()
//		{
//			@Override
//			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
//			{
//				byte[] buffer = (byte[]) invocationOnMock.getArguments()[0];
//				int bytesToRead = (int) invocationOnMock.getArguments()[1];
//
//				System.arraycopy(testValues, 0, buffer, 0, bytesToRead);
//				return bytesToRead;
//			}
//		});
//
//		Mockito.doAnswer(new Answer()
//		{
//			@Override
//			public Object answer(InvocationOnMock invocationOnMock) throws Throwable
//			{
//				byte[] buffer = (byte[]) invocationOnMock.getArguments()[0];
//				int bytesToTransmit = (int) invocationOnMock.getArguments()[1];
//				System.out.println(bytesToTransmit);
////				assertEquals(numOfBytes, bytesToTransmit);
//				assertArrayEquals(buffer, Arrays.copyOf(testValues, bytesToTransmit));
//				return null;
//			}
//		}).when(mockOutput).transmitBytes((byte[]) notNull(), any(Integer.class));
//
//		Mockito.when(mockInput.available()).thenReturn(0);
//
//		fileTransmitter.start();
//	}
}