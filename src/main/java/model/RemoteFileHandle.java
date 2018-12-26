package model;

public interface RemoteFileHandle
{
	String getDownloadPath();

	void setDownloadSpeed(long bytesPerSecond);
	void setProgress(double percentage);
}
