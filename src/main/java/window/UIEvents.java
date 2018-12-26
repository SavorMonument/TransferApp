package window;

import java.util.List;

public interface UIEvents
{
	interface FileEvents
	{
		void updateAvailableFiles(List<FileInfo> fileInfos);
		void requestFileForDownload(FileInfo fileInfo, String downloadPath);
	}

	interface ConnectionEvents
	{
		void attemptConnectionToHost(String host);
		void disconnect();
	}
}
