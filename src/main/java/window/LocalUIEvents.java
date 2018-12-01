package window;

import java.io.File;
import java.util.List;

public interface LocalUIEvents
{
	void testButton();

	void updateAvailableFileList(List<File> file);
	default boolean attemptConnectionToHost(String host)
	{
		return attemptConnectionToHost(host, 50001);
	}
	boolean attemptConnectionToHost(String host, int port);
	void setDownloadLocation(String path);
	void requestFileForDownload(String fileName);
}
