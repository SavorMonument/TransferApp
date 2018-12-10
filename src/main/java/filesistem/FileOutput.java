package filesistem;

import filetransfer.api.TransferFileOutput;

import java.io.*;

public class FileOutput implements Closeable, TransferFileOutput
{
	private File tempFile;
	private OutputStream outputStream;
	private boolean isFinished = false;

	private String fileName;
	private String path;

	public FileOutput(String fileName, String path)
	{
		this.fileName = fileName;
		this.path = path;
	}

	public boolean createTempFile() throws IOException
	{
		boolean successful;
		tempFile = new File(path + "/" + fileName + ".tmp");

		successful = tempFile.createNewFile();
		outputStream = new FileOutputStream(tempFile);

		return successful;
	}

	public void writeToFile(byte[] bytes) throws IOException
	{
		writeToFile(bytes, bytes.length);
	}

	public void writeToFile(byte[] bytes, int amount) throws IOException
	{
		assert null != tempFile : "The file has to be created";

		outputStream.write(bytes, 0, amount);
		outputStream.flush();
	}

	public boolean finishFile()
	{
		assert null != tempFile : "The file has to be created";
		isFinished = true;
		return tempFile.renameTo(new File(tempFile.getAbsolutePath().replace(".tmp", "")));
	}

	private void abort()
	{
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
//				e.printStackTrace();
			}
		}

		if (!isFinished)
			abort();
	}
}
