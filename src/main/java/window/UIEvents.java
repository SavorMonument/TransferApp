package window;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface UIEvents
{
	interface FileEvents
	{
		void updateAvailableFiles(Set<File> file);
		void requestFileForDownload(String fileName);
	}

	interface ConnectionEvents
	{
		void attemptConnectionToHost(String host);
		void disconnect();
	}
}
