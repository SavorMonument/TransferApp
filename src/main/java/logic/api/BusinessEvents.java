package logic.api;

import logic.messaging.FileInformation;

import java.util.Set;

public interface BusinessEvents
{
	void updateRemoteFileList(Set<FileInformation> fileNames);
	boolean confirmConnectionRequest(String url);
	void printMessageOnDisplay(String message);

	String getLocalFilePath(String fileName);
//	String getDownloadPath();

	void setConnectionState(String state);
}
