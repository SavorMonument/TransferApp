package network;

import java.util.List;

public interface SocketReceivingEvents
{
	void updateRemoteFileList(List<String> files);
	void uploadFile(String filename);
}
