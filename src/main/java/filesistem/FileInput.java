package filesistem;

import filetransfer.TransferFileInput;

import java.io.*;

public class FileInput implements TransferFileInput
{
	private File file;
	private BufferedInputStream inputStream;

	private String filePath;

	public FileInput(String filePath)
	{
		this.filePath = filePath;
	}

	public boolean open() throws FileNotFoundException
	{
		file = new File(filePath);
		boolean successful = file.exists();

		inputStream = new BufferedInputStream(new FileInputStream(file));

		return successful;
	}

	public int read(byte[] bytes, int bytesToRead) throws IOException
	{
		assert null != inputStream : "Input stream uninitialized";

		if (inputStream.available() < bytesToRead)
			return inputStream.read(bytes, 0, inputStream.available());
		else
			return inputStream.read(bytes, 0, bytesToRead);
	}

	private int available() throws IOException
	{
		return inputStream.available();
	}

	@Override
	public void close()
	{
		if (null != inputStream)
		{
			try
			{
				inputStream.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
