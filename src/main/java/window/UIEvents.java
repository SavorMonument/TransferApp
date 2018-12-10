package window;

import com.sun.istack.internal.NotNull;
import logic.Connection;

import java.io.File;
import java.util.List;

public interface UIEvents
{
	interface FileEvents
	{
		void updateAvailableFileList(List<File> file);
		void requestFileForDownload(String fileName);
	}

	interface ConnectionEvents
	{
		void attemptConnectionToHost(String host);
		void disconnect();
	}
}
