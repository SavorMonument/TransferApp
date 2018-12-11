package logic.api;

import logic.messaging.FileInformation;

import java.util.Set;

public interface BusinessEvents
{
	void updateRemoteFileList(Set<FileInformation> fileNames);
	boolean confirmConnectionRequest(String url);

	String getLocalFilePath(String fileName);

	void setConnectionState(String state);

	void printMessageOnDisplay(String message);
	void printUploadSpeed(long speed);
	void printDownloadSpeed(long speed);
}
