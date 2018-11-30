package network;

import java.net.Socket;
import java.util.List;

public interface Connection
{
	boolean isConnected();
	boolean closeConnection();
	Socket getSocket();
}
