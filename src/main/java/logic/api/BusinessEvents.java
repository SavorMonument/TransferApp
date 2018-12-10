package logic.api;

import java.util.Collection;
import java.util.Set;

public interface BusinessEvents
{
	void updateRemoteFileList(Set<String> fileNames);
	boolean confirmConnectionRequest(String url);
	void printMessageOnDisplay(String message);

	String getLocalFilePath(String fileName);
	String getDownloadPath();

	void setConnectionState(String state);
}
