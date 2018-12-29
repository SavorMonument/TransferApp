package logic;

public interface FileHandle
{
	void setTransferSpeed(long bytesPerSecond);

	void setTransferProgress(double progress);
}
