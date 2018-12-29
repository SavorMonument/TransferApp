package logic.messaging.actions;

import logic.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.connection.Connection;
import logic.messaging.actions.filelist.ReceiverFileUpdateAction;
import logic.messaging.actions.filelist.TransmitterFileUpdateAction;
import logic.messaging.actions.transfer.ReceiverTransferAction;
import logic.messaging.actions.transfer.TransmitterTransferAction;
import logic.messaging.messages.NetworkMessage;
import logic.messaging.messages.TransferRequestMessage;
import logic.messaging.messages.UpdateFileListMessage;
import model.FileInfo;

import java.util.List;

public class ActionFactory
{
	private BusinessEvents businessEvents;
	private ConnectCloseEvent connectCloseEvent;
	private Connection fileConnection;

	public ActionFactory(BusinessEvents businessEvents, ConnectCloseEvent connectCloseEvent, Connection fileConnection)
	{
		this.businessEvents = businessEvents;
		this.connectCloseEvent = connectCloseEvent;
		this.fileConnection = fileConnection;
	}

	public Action getTransmitterAction(NetworkMessage networkMessage)
	{
		switch (networkMessage.getMessageID())
		{
			case TransmitterFileUpdateAction.ID:
			{
				return new TransmitterFileUpdateAction();
			}
			case TransmitterTransferAction.ID:
			{
				return new TransmitterTransferAction(businessEvents, connectCloseEvent,
						fileConnection, ((TransferRequestMessage) networkMessage).getFileInfo());
			}
			default:
				throw new IllegalArgumentException("No action for the message");
		}
	}

	public Action getReceiverAction(NetworkMessage networkMessage)
	{
		switch (networkMessage.getMessageID())
		{
			case ReceiverFileUpdateAction.ID:
			{
				return new ReceiverFileUpdateAction(businessEvents,
						(List<FileInfo>) ((UpdateFileListMessage) networkMessage).getFilesInfos());
			}
			case ReceiverTransferAction.ID:
			{
				return new ReceiverTransferAction(businessEvents, fileConnection, connectCloseEvent,
						((TransferRequestMessage) networkMessage).getFileInfo());
			}
			default:
				throw new IllegalArgumentException("No action for the message");
		}
	}
}
