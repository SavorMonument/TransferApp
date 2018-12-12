package filesistem;

import filetransfer.api.FileException;
import filetransfer.api.TransferFileInput;

import java.io.*;

public class FileInput implements TransferFileInput
{
	private File file;
	private InputStream inputStream;

	private String filePath;

	public FileInput(String filePath)
	{
		this.filePath = filePath;
	}

	public void open() throws FileNotFoundException
	{
		file = new File(filePath);
		inputStream = new FileInputStream(file);
	}

	public int read(byte[] bytes, int bytesToRead) throws FileException
	{
		assert null != inputStream : "Input streaming uninitialized";

		try
		{
			if (inputStream.available() < bytesToRead)
				return inputStream.read(bytes, 0, inputStream.available());
			else
				return inputStream.read(bytes, 0, bytesToRead);
		} catch (IOException e)
		{
			throw new FileException(e.getMessage(), file.getName(), e);
		}
	}

	public int available() throws FileException
	{
		try
		{
			return inputStream.available();
		} catch (IOException e)
		{
			throw new FileException(e.getMessage(), file.getName(), e);
		}
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
