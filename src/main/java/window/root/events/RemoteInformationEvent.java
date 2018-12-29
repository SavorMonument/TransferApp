package window.root.events;

import logic.FileHandle;
import model.FileInfo;

import java.util.List;

public interface RemoteInformationEvent
{
	void updateFileList(List<FileInfo> filesInfo);

	void setDownloadDisabled(boolean isDisabled);

	FileHandle getRemoteFileHandle(FileInfo fileInfo);

	String getDownloadLocation();
}
