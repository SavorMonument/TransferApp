package window;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

public interface LocalUI
{
	boolean hasUnprocessedFiles();
	File getMarkedFile();
	boolean hasPendingConnectionRequest();
	String getConnectionRequest();
}
