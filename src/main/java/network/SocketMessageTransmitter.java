package network;

import filetransfer.TransferOutput;
import logic.Connection;
import logic.NetworkMessage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketMessageTransmitter extends SocketTransmitter implements Connection.MessageTransmitter, TransferOutput
{
	private BufferedWriter outputWriter;

	public SocketMessageTransmitter(Socket socket)
	{
		super(socket);
		this.outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
	}

	public void transmitMessage(NetworkMessage networkMessage) throws IOException
	{
		StringBuilder message = new StringBuilder();
		message.append(networkMessage.getType().toString())
				.append("\n")
				.append(networkMessage.getMessage())
				.append("\n");

		outputWriter.write(message.toString());
		outputWriter.flush();
	}
}
