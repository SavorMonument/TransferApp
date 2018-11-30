package window;

import javafx.scene.Node;

import java.io.File;
import java.util.logging.Level;

public class RemoteArea implements RemoteUI
{
	private Node node;

	public RemoteArea(Node scene)
	{
		this.node = scene;
	}

	public boolean shouldAcceptConnectionRequest(String path)
	{
		AppLogger.getInstance().log(Level.ALL, "Connection request" + path);

		return true;
	}

	public boolean showFileAsAvailable(File file)
	{
		return false;
	}

	public File getDownloadRequest()
	{
		return null;
	}

	public boolean hasRequestedFiles()
	{
		return false;
	}

	public String getRequestedFile()
	{
		return null;
	}

	public String getDefaultDownloadLocation()
	{
		//TODO: Request input for this
		return "C:\\Users\\Goia\\Desktop\\temp";
	}
}
