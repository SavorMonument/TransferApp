package filesistem;

import filetransfer.api.TransferFileOutput;

import java.io.*;

public class FileOutput implements Closeable, TransferFileOutput
{
	private File file;
	private OutputStream outputStream;

	private String path;

	public FileOutput(String fileName, String path)
	{
		file = new File(path + "/" + fileName);

		this.path = path;
	}

	@Override
	public boolean exists()
	{
		return file.exists();
	}

	@Override
	public long diskSpaceAtLocation()
	{
		return new File(path).getFreeSpace();
	}

	@Override
	public void open() throws FileException
	{

		try
		{
			if (!file.createNewFile())
				throw new IOException("Could not create file");

			outputStream = new FileOutputStream(file);
		} catch (IOException e)
		{
			throw new FileException(e.getMessage(), file.getName(), e);
		}
	}

	public void writeToFile(byte[] bytes) throws FileException
	{
		writeToFile(bytes, bytes.length);
	}

	@Override
	public void writeToFile(byte[] bytes, int amount) throws FileException
	{
		assert null != file : "The file has to be created";

		try
		{
			outputStream.write(bytes, 0, amount);
			outputStream.flush();
		}catch (IOException e)
		{
			throw new FileException(e.getMessage(), file.getName(), e);
		}
	}

	public void abort()
	{
		if (null != file)
		{
			//Best effort
			file.delete();
		}
	}

	@Override
	public void close()
	{
		if (null != outputStream)
		{
			try
			{
				outputStream.close();
			} catch (IOException e)
			{
//				e.printStackTrace();
			}
		}
	}
}
