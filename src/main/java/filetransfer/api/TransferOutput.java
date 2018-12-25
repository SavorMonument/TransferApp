package filetransfer.api;

import network.ConnectionException;

public interface TransferOutput
{
	void transmitBytes(byte[] buffer, int bytesToTransmit) throws ConnectionException;
	void transmitByte(int b) throws ConnectionException;


	void flush() throws ConnectionException;
}