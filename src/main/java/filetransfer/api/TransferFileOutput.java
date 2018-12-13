package filetransfer.api;

import filesistem.FileException;

import java.io.Closeable;

public interface TransferFileOutput extends Closeable
{
	void open() throws FileException;
	void writeToFile(byte[] buffer, int amountRead) throws FileException;
	boolean exists();
	long diskSpaceAtLocation();
	void abort();

	@Override
	void close();

}
