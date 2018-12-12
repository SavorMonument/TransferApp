package filetransfer.api;

import java.io.Closeable;
import java.io.FileNotFoundException;

public interface TransferFileInput extends Closeable
{
	void open() throws FileNotFoundException;
	int read(byte[] buffer, int bufferSize) throws FileException;
	int available() throws FileException;

	void close();
}
