package logic;

import com.sun.istack.internal.NotNull;
import filesistem.FileOutput;
import filetransfer.FileReceiver;
import filetransfer.TransferInput;
import filetransfer.TransferOutput;
import logic.Connection.MessageTransmitter;
import window.AppLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransmittingController
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private Connection mainConnection;
	private Connection fileTransmittingConnection;

	private MessageTransmitter messageTransmitter;
	private ConnectCloseEvent connectEvent;

	public TransmittingController(@NotNull Connection mainConnection, @NotNull Connection fileTransmittingConnection, @NotNull BusinessEvents businessEvents, @NotNull ConnectCloseEvent connectEvent)
	{
		assert null != mainConnection && mainConnection.isConnected() : "Invalid main connection";
		assert null != fileTransmittingConnection && fileTransmittingConnection.isConnected() : "Invalid main connection";
		assert null != businessEvents : "Invalid BusinessEvents";
		assert null != connectEvent : "Need an actual event handler";

		this.mainConnection = mainConnection;
		this.fileTransmittingConnection = fileTransmittingConnection;

		this.messageTransmitter = mainConnection.getMessageTransmitter();
		this.connectEvent = connectEvent;
	}

	public void updateAvailableFileList(List<File> files)
	{
		LOGGER.log(Level.FINE, "Sending file update..." + files.toString());

		List<String> fileNames = getListOfFileNames(files);
		NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.UPDATE_FILE_LIST,
				fileNames.toString());

		try
		{
			messageTransmitter.transmitMessage(networkMessage);
		} catch (IOException e)
		{
			connectEvent.disconnect(e.getMessage());
		}

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

		try
		{
			NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.SEND_FILE, fileName);
			messageTransmitter.transmitMessage(networkMessage);

			LOGGER.log(Level.FINE, String.format("Starting file receiver with file: %s from address: %s, port%d",
					fileName, fileTransmittingConnection.getRemoteAddress(), fileTransmittingConnection.getRemotePort()));

			new FileReceiver((TransferInput) fileTransmittingConnection.getMessageReceiver(),
					(TransferOutput) fileTransmittingConnection.getMessageTransmitter(), new FileOutput(fileName, downloadPath)).start();
		} catch (IOException e)
		{
			connectEvent.disconnect(e.getMessage());
		}
	}

	public void transmitDisconnectMessage()
	{
		try
		{
			NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.DISCONNECT, "");
			messageTransmitter.transmitMessage(networkMessage);
		} catch (IOException e)
		{
			connectEvent.disconnect(e.getMessage());
		}
	}
}
