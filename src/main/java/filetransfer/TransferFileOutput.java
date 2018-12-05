package filetransfer;

import java.io.Closeable;
import java.io.IOException;

public interface TransferFileOutput extends Closeable
{
	boolean createTempFile() throws IOException;
	void writeToFile(byte[] buffer, int amountRead) throws IOException;
	boolean finishFile() throws IOException;

	@Override
	void close();
}
