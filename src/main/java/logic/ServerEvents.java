package logic;

import java.io.File;
import java.util.List;

public interface ServerEvents
{
	boolean connectionRequest(String user);
	boolean markFilesAvailableForTransfer(List<File> files);
	boolean startedDownload(String fileName);
	boolean stoppedDownload(String fileName);
}
