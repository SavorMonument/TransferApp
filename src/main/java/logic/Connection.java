package logic;

import com.sun.istack.internal.NotNull;

import java.io.Closeable;
import java.net.InetAddress;

public abstract class Connection implements Closeable
{
	private MessageTransmitter messageTransmitter;
	private MessageReceiver messageReceiver;

	public Connection(@NotNull MessageTransmitter messageTransmitter, @NotNull MessageReceiver messageReceiver)
	{
		assert null != messageTransmitter : "Invalid MessageTransmitter";
		assert null != messageReceiver : "Invalid messageReceiver";

		this.messageTransmitter = messageTransmitter;
		this.messageReceiver = messageReceiver;
	}

	//-------------------------------------------------------
	public interface MessageTransmitter
	{
		void transmitMessage(NetworkMessage networkMessage);
	}

	public interface MessageReceiver
	{
		NetworkMessage pullMessage();
	}
	//-------------------------------------------------------

	abstract public boolean isConnected();
	abstract public InetAddress getLocalAddress();
	abstract public InetAddress getRemoteAddress();
	abstract public int getLocalPort();
	abstract public int getRemotePort();

	@Override
	abstract public void close();

	public MessageTransmitter getMessageTransmitter()
	{
		return messageTransmitter;
	}

	public MessageReceiver getMessageReceiver()
	{
		return messageReceiver;
	}
}
