package logic;

import logic.messaging.FileInformation;
import window.UIEvents;

import java.util.Set;

public interface BusinessEvents
{
	void updateRemoteFileList(Set<FileInformation> fileNames);
	void setDownloadingState(boolean isDownloading);

	String getLocalFilePath(String fileName);

	void setConnectionState(String state);
	boolean confirmConnectionRequest(String url);

	void printMessageOnDisplay(String message);
	void printUploadSpeed(long speed);
	void printDownloadSpeed(long speed);

	void setFileEventHandler(UIEvents.FileEvents fileEvents);
}
