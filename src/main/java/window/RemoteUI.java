package window;

import java.io.File;

public interface RemoteUI
{
	boolean shouldAcceptConnectionRequest(String path);
	boolean showFileAsAvailable(File file);
	File getDownloadRequest();

}
