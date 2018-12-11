package window.root.events;

import logic.messaging.FileInformation;

import java.util.Set;

public interface RemoteInformationEvent
{
	void updateRemoteFileList(Set<FileInformation> filesInformation);
}
