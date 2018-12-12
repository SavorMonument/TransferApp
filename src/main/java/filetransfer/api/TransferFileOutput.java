package filetransfer.api;

import java.io.Closeable;

public interface TransferFileOutput extends Closeable
{
	void open() throws FileException;
	void writeToFile(byte[] buffer, int amountRead) throws FileException;
	void abort();

	@Override
	void close();

}
