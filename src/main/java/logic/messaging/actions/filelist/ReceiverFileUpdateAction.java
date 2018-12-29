package logic.messaging.actions.filelist;

import logic.BusinessEvents;
import logic.messaging.actions.Action;
import logic.messaging.messages.UpdateFileListMessage;
import model.FileInfo;
import window.AppLogger;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiverFileUpdateAction implements Action
{
	public static final String ID = UpdateFileListMessage.MESSAGE_ID;

	private static final Logger LOGGER = AppLogger.getInstance();

	private BusinessEvents businessEvents;
	private List<FileInfo> fileInfos;

	public ReceiverFileUpdateAction(BusinessEvents businessEvents, List<FileInfo> fileInfos)
	{
		this.businessEvents = businessEvents;
		this.fileInfos = fileInfos;
	}

	@Override
	public void performAction()
	{
		LOGGER.log(Level.ALL, "Received remote file list update: " + fileInfos.toString());
		businessEvents.printMessageOnDisplay("Updating file list");

		businessEvents.updateRemoteFileList(fileInfos);
	}
}
