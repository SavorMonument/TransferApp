package window.root.events;

public interface DisplayEvent
{
	void printMessageOnDisplay(String message);
	void printUploadSpeed(long speed);
	void printDownloadSpeed(long speed);
}
