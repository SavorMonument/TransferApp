package logic;

import com.sun.istack.internal.NotNull;
import filesistem.FileOutput;
import filetransfer.FileReceiver;
import filetransfer.TransferInput;
import filetransfer.TransferOutput;
import network.ConnectionResolver;
import network.SocketMessageTransmitter;
import network.SocketReceiver;
import network.SocketTransmitter;
import window.AppLogger;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransmittingController
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int FILE_PORT = 59_901;

	private Connection mainConnection;
	private Connection fileTransmittingConnection;

	private Connection.MessageTransmitter messageTransmitter;

	public TransmittingController(@NotNull Connection mainConnection, @NotNull Connection fileTransmittingConnection, @NotNull BusinessEvents businessEvents)
	{
		assert null != mainConnection && mainConnection.isConnected() : "Invalid main connection";
		assert null != fileTransmittingConnection && fileTransmittingConnection.isConnected() : "Invalid main connection";
		assert null != businessEvents : "Invalid BusinessEvents";

		this.mainConnection = mainConnection;
		this.fileTransmittingConnection = fileTransmittingConnection;

		this.messageTransmitter = mainConnection.getMessageTransmitter();
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

		new FileReceiver((TransferInput) fileTransmittingConnection.getMessageReceiver(),
				(TransferOutput) fileTransmittingConnection.getMessageTransmitter(), new FileOutput(fileName, downloadPath)).start();
	}
}
