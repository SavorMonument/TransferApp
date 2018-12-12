package filetransfer.api;

import java.io.Closeable;

public interface TransferInput extends Closeable
{
	int available() throws TransferException;
	int read() throws TransferException;
	int read(byte[] buffer, int len) throws TransferException;
	void skip(long n) throws TransferException;
	int getBufferSize();
}
