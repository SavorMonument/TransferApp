package logic.messaging.messages;

public class MessageFactory
{
	public NetworkMessage resolveMessage(String messageWithCode)
	{
		NetworkMessage networkMessage = null;

		String[] messageTokens = messageWithCode.split(NetworkMessage.CODE_DELIMITER);
		String ID = messageTokens[0];

		switch (ID)
		{
			case UpdateFileListMessage.MESSAGE_ID:
			{
				networkMessage = new UpdateFileListMessage(messageTokens[1]);
			}
			break;

			case TransferRequestMessage.MESSAGE_ID:
			{
				networkMessage = new TransferRequestMessage(messageTokens[1]);
			}
			break;
		}

		return networkMessage;
	}
}
