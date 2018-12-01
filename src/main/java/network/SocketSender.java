package network;

import window.AppLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketSender
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private Socket socket;
	private BufferedWriter output;

	public SocketSender(Socket socket)
	{
		this.socket = socket;

		try
		{
			output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket output stream problem" + e.getMessage());
//			e.printStackTrace();
		}
	}

	public void updateRemoteFileList(List<String> files)
	{
		try
		{
			LOGGER.log(Level.FINE, "Attempting to remotely update file list");

			output.write("UPDATE_FILE_LIST");
			output.newLine();
			output.write(files.toString());
			output.newLine();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING,String.format("Couldn't write to the socket output stream\n%s",
					e.getMessage()));
//			e.printStackTrace();
		}
	}

	public void requestFileTransfer(String fileName)
	{
		try
		{
			output.write("SEND_FILE");
			output.newLine();
			output.write(fileName);
		} catch (IOException e)
		{
//			e.printStackTrace();
		}
	}

	//DEBUG ONLY
	public void testPrint(String str)
	{
		try
		{
			LOGGER.log(Level.ALL, String.format("Printing -- %s -- on the output stream", str));
			output.write(str);
			output.newLine();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
