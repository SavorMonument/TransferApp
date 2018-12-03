package filesistem;

import java.io.*;
import java.util.Arrays;

public class FileOutput implements Closeable
{
	private File tempFile;
	private BufferedOutputStream outputStream;

	private String fileName;
	private String path;

	public FileOutput(String fileName, String path)
	{
		this.fileName = fileName;
		this.path = path;
	}

	public boolean createTempFile()
	{
		boolean successful;
		tempFile = new File(path + "/" + fileName + ".tmp");
		try
		{
			successful = tempFile.createNewFile();
			outputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
		} catch (IOException e)
		{
			successful = false;
			e.printStackTrace();
		}
		return successful;
	}

	public boolean writeToFile(byte[] bytes, int amount)
	{
		System.out.println("Bytes amount: " + amount);
		if (bytes.length == amount)
			return writeToFile(bytes);
		else
			return writeToFile(Arrays.copyOf(bytes, amount));
	}

	public boolean writeToFile(byte[] bytes)
	{
		assert null != tempFile : "The file has to be created";

		boolean successful = false;
		try
		{
			outputStream.write(bytes);
			successful = true;
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return successful;
	}

	public boolean finishFile()
	{
		assert null != tempFile : "The file has to be created";
		close();
		return tempFile.renameTo(new File(tempFile.getAbsolutePath().replace(".tmp", "")));
	}

	public void abort()
	{
		close();
		if (tempFile.exists())
		{
			tempFile.delete();
		}
		File finishedFile = new File(tempFile.getAbsolutePath().replace(".tmp", ""));
		if (finishedFile.exists())
		{
			finishedFile.delete();
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
				e.printStackTrace();
			}
		}
	}
}
