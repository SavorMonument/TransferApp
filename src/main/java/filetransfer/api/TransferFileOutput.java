package filetransfer.api;

import java.io.Closeable;
import java.io.IOException;

public interface TransferFileOutput extends Closeable
{
	void open() throws IOException;
	void writeToFile(byte[] buffer, int amountRead) throws IOException;
	void abort();

	@Override
	void close();

}
