package network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SocketMessageTransmitter
{
	private BufferedWriter output;

	public SocketMessageTransmitter(SocketTransmitter transmitter)
	{
		this.output = new BufferedWriter(new OutputStreamWriter(transmitter));
	}

	public void transmitMessage(NetworkMessage networkMessage)
	{
		StringBuilder message = new StringBuilder();
		message.append(networkMessage.getType().toString())
				.append("\n")
				.append(networkMessage.getMessage())
				.append("\n");

		try
		{
			output.write(message.toString());
			output.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
