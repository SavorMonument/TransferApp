package filetransfer.api;

import java.io.Closeable;
import java.io.IOException;

public interface TransferFileInput extends Closeable
{
	boolean open() throws IOException;
	int read(byte[] buffer, int bufferSize) throws IOException;
	int available() throws IOException;

	void close();
}
