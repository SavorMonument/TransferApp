package filesistem;

import filetransfer.api.FileException;
import filetransfer.api.TransferFileOutput;

import java.io.*;

public class FileOutput implements Closeable, TransferFileOutput
{
	private File file;
	private OutputStream outputStream;

	private String fileName;
	private String path;

	public FileOutput(String fileName, String path)
	{
		this.fileName = fileName;
		this.path = path;
	}

//	public boolean createTempFile() throws IOException
//	{
//		boolean successful;
//		tempFile = new File(path + "/" + fileName + ".tmp");
//
//		successful = tempFile.createNewFile();
//		outputStream = new FileOutputStream(tempFile);
//
//		return successful;
//	}

	public void open() throws FileException
	{
		file = new File(path + "/" + fileName);

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
