package model;

public interface LocalFileHandle
{
	String getFullPath();
	void setUploadSpeed(long bytePerSecond);
}
