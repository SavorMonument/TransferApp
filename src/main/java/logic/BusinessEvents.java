package logic;

import model.FileInfo;
import window.UIEvents;

import java.util.List;

public interface BusinessEvents
{
	void updateRemoteFileList(List<FileInfo> fileNames);
	void setDownloadingState(boolean isDownloading);

	FileHandle getLocalFileHandle(FileInfo fileInfo);
	FileHandle getRemoteFileHandle(FileInfo fileInfo);
	String getLocalFilePath(FileInfo fileInfo);
	String getDownloadLocation();

	void setConnectionState(String state);
	boolean confirmConnectionRequest(String url);

	void printMessageOnDisplay(String message);
	void printUploadSpeed(long speed);
	void printDownloadSpeed(long speed);

	void setFileEventHandler(UIEvents.FileEvents fileEvents);
	void setConnectionEventHandler(UIEvents.ConnectionEvents connectionHandler);
}
