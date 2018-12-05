package logic;

import filetransfer.FileReceiverController;
import network.SocketMessageTransmitter;
import window.AppLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransmitterController
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int FILE_PORT = 59_901;

	private SocketMessageTransmitter messageTransmitter;

	public TransmitterController(SocketMessageTransmitter messageTransmitter, BusinessEvents businessEvents)
	{
			this.messageTransmitter = messageTransmitter;
	}

	public void updateAvailableFileList(List<File> files)
	{
		LOGGER.log(Level.FINE, "Sending file update..." + files.toString());

		List<String> fileNames = getListOfFileNames(files);
		NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.UPDATE_FILE_LIST,
				fileNames.toString());

		messageTransmitter.transmitMessage(networkMessage);

	}

	private List<String> getListOfFileNames(List<File> files)
	{
		List<String> fileNames = new ArrayList<>();

		for (int i = 0; i < files.size(); i++)
		{
			fileNames.add(files.get(i).getName());
		}

		return fileNames;
	}

	public void requestFileForDownload(String fileName, String downloadPath)
	{
		LOGGER.log(Level.FINE, "Sending file download request: " + fileName);

		NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.SEND_FILE, fileName);
		messageTransmitter.transmitMessage(networkMessage);

		new FileReceiverController(downloadPath, fileName, FILE_PORT).start();
	}
}
