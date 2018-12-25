package filetransfer.api;

import network.ConnectionException;

public interface TransferInput
{
	int available() throws ConnectionException;
	int read() throws ConnectionException;
	int read(byte[] buffer, int len) throws ConnectionException;
	void skip(long n) throws ConnectionException;
	int getBufferSize();
}
