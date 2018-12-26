package filetransfer;

public interface TransferObserver
{
	void addBytesToCount(int amount);
	void setProgress(float progressPercentage);
}
