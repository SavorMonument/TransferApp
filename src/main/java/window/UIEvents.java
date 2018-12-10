package window;

import logic.messaging.FileInformation;

import java.io.File;
import java.util.Set;

public interface UIEvents
{
	interface FileEvents
	{
		void updateAvailableFiles(Set<File> file);
		void requestFileForDownload(FileInformation fileInformation, String downloadPath);
	}

	interface ConnectionEvents
	{
		void attemptConnectionToHost(String host);
		void disconnect();
	}
}
