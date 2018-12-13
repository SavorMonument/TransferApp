package logic.connection;

import logic.connection.Connection;

import java.io.IOException;
import java.net.InetAddress;

public interface ConnectionResolver
{
	Connection attemptNextConnection(InetAddress remoteAddress) throws IOException;

	Connection attemptNextConnection(InetAddress remoteAddress, int timeOut) throws IOException;

	Connection listenNextConnection(int timeout) throws IOException;

	Connection listenNextConnection() throws IOException;

	void stopListening();
}
