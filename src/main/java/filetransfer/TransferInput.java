package filetransfer;

import java.io.Closeable;
import java.io.IOException;

public interface TransferInput extends Closeable
{
	int available() throws IOException;
	int read() throws IOException;
	int read(byte[] buffer) throws IOException;
	void skip(long n) throws IOException;
	int getBufferSize();
}
