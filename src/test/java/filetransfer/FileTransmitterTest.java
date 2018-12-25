package filetransfer;

import filetransfer.api.TransferFileInput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
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
}