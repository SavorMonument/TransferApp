package filetransfer.api;

import java.io.Closeable;
import java.io.IOException;

public interface TransferOutput extends Closeable
{
	void transmitBytes(byte[] buffer, int bytesToTransmit) throws TransferException;
	void transmitByte(int b) throws TransferException;


	public void flush() throws TransferException;
}