package logic;

import network.SocketSender;
import window.AppLogger;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SenderController
{
	private static final Logger LOGGER = AppLogger.getInstance();

	List<File> filesAvailableForTransfer;

	private SocketSender socketSender;

	public SenderController(Socket socket)
	{
		try
		{
			this.socketSender = new SocketSender(socket);
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Failed to construct a SocketSender" + e.getMessage());
//			e.printStackTrace();
		}
	}

	public void updateAvailableFileList(List<File> files)
	{
		LOGGER.log(Level.FINE, "Updating remote files" + files.toString());

		filesAvailableForTransfer = new ArrayList<>(files);

		List<String> fileNames = new ArrayList<>();

		for (int i = 0; i < files.size(); i++)
		{
			fileNames.add(files.get(i).getName());
		}

		StringBuilder message = new StringBuilder();
		message.append("UPDATE_FILE_LIST")
				.append("\n")
				.append(files.toString())
				.append("\n");

		socketSender.transmitMessage(message.toString());

	}

	public void uploadFile(String filename)
	{

	}
}
