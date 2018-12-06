package window;

import java.io.File;
import java.util.List;

public interface UIEvents
{
	default boolean attemptConnectionToHost(String host)
	{
		return attemptConnectionToHost(host, 50001);
	}
	boolean attemptConnectionToHost(String host, int port);
	void disconnect();
	void updateAvailableFileList(List<File> file);
	void setDownloadLocation(String path);
	void requestFileForDownload(String fileName);
}
