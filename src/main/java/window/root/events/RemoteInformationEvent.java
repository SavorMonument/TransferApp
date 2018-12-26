package window.root.events;

import java.util.List;

public interface RemoteInformationEvent
{
	void updateRemoteFileList(List<FileInfo> filesInfo);
	void setDownloadDisabled(boolean isDisabled);

}
