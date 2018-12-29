package window;

import model.FileInfo;

import java.util.List;

public interface UIEvents
{
	interface FileEvents
	{
		void updateAvailableFiles(List<FileInfo> fileInfos);
		void requestFileForDownload(FileInfo fileInfo);
	}

	interface ConnectionEvents
	{
		void attemptConnectionToHost(String host);
		void disconnect();
	}
}
