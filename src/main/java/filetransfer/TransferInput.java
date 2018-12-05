package filetransfer;

import java.io.Closeable;
import java.io.IOException;

public interface TransferInput extends Closeable
{
	int available() throws IOException;
	int read() throws IOException;
	int read(byte[] buffer) throws IOException;

	@Override
	void close();
}
