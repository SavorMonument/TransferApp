package filetransfer.api;

import logic.messaging.ConnectionException;

import java.io.Closeable;

public interface TransferInput extends Closeable
{
	int available() throws ConnectionException;
	int read() throws ConnectionException;
	int read(byte[] buffer, int len) throws ConnectionException;
	void skip(long n) throws ConnectionException;
	int getBufferSize();
}
