package filetransfer.api;

import logic.messaging.ConnectionException;

import java.io.Closeable;
import java.io.IOException;

public interface TransferOutput extends Closeable
{
	void transmitBytes(byte[] buffer, int bytesToTransmit) throws ConnectionException;
	void transmitByte(int b) throws ConnectionException;


	void flush() throws ConnectionException;
}