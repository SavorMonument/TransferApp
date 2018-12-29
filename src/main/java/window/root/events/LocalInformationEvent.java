package window.root.events;

import logic.FileHandle;
import model.FileInfo;

public interface LocalInformationEvent
{
	FileHandle getLocalHandler(FileInfo FileInfo);
	String getLocalFilePath(FileInfo fileInfo);
}
