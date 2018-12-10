package logic.api;

import java.util.List;

public interface BusinessEvents
{
	void updateRemoteFileList(List<String> fileNames);
	boolean confirmConnectionRequest(String url);
	void printMessageOnDisplay(String message);

	String getLocalFilePath(String fileName);
	String getDownloadPath();

	void setConnectionState(String state);
}
