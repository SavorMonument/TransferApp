package window;

import java.io.File;
import java.util.List;

public interface UIEvents
{
	void attemptConnectionToHost(String host);
//	boolean attemptConnectionToHost(String host, int port);
	void disconnect();
	void updateAvailableFileList(List<File> file);
	void setDownloadLocation(String path);
	void requestFileForDownload(String fileName);
}
