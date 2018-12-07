package filetransfer;

import java.io.Closeable;
import java.io.IOException;

public interface TransferOutput extends Closeable
{
	void transmitBytes(byte[] buffer, int bytesToTransmit) throws IOException;

	void transmitByte(int b) throws IOException;
}