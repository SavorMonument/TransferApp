package logic;

import java.io.File;
import java.util.List;

public interface RemoteUIEvents
{
	boolean updateRemoteFileList(List<String> fileNames);
	boolean shouldAcceptConnectionFrom(String url);

//	boolean startedDownload(String fileName);
//	boolean stoppedDownload(String fileName);
}
