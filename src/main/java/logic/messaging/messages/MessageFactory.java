package logic.messaging.messages;

import logic.connection.Connection;

public class MessageFactory
{
	private Connection fileConnection;

	public MessageFactory(Connection fileConnection)
	{
		this.fileConnection = fileConnection;
	}

	public NetworkMessage resolveMessage(String messageWithCode)
	{
		NetworkMessage networkMessage = null;

		String[] messageTokens = messageWithCode.split(NetworkMessage.CODE_DELIMITER);
		String ID = messageTokens[0];

		switch (ID)
		{
			case UpdateFileListMessage.MESSAGE_CODE:
			{
				networkMessage = new UpdateFileListMessage(messageTokens[1]);
			}
			break;

			case DownloadRequestMessage.MESSAGE_CODE:
			{
				networkMessage = new DownloadRequestMessage(messageTokens[1], fileConnection);
			}
			break;
		}

		return networkMessage;
	}
}
