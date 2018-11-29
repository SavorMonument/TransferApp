package network;

import java.net.Socket;

public interface NetworkListener
{
	void receivedConnection(Socket socket);
}
