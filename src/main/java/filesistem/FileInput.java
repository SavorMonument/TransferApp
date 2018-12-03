package filesistem;

import java.io.*;

public class FileInput implements Closeable
{
	private File file;
	private BufferedInputStream inputStream;

	private String filePath;

	public FileInput(String filePath)
	{
		this.filePath = filePath;
	}

	public boolean open()
	{
		file = new File(filePath);
		boolean successful = file.exists();

		try
		{
			inputStream = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e)
		{
			successful = false;
			e.printStackTrace();
		}

		return successful;
	}

	public boolean ready()
	{
		assert null != inputStream : "Input stream uninitialized";

		boolean ready = false;

		try
		{
			ready = inputStream.available() > 0;
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return ready;
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
