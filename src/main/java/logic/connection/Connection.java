package logic.connection;

import com.sun.istack.internal.NotNull;
import logic.messaging.ConnectionException;

import java.io.Closeable;
import java.net.InetAddress;

public abstract class Connection implements Closeable
{
	private MessageTransmitter messageTransmitter;
	private StringReceiver messageReceiver;

	public Connection(@NotNull MessageTransmitter messageTransmitter, @NotNull StringReceiver messageReceiver)
	{
		assert null != messageTransmitter : "Invalid MessageTransmitter";
		assert null != messageReceiver : "Invalid messageReceiver";

		this.messageTransmitter = messageTransmitter;
		this.messageReceiver = messageReceiver;
	}

	//-------------------------------------------------------
	public interface MessageTransmitter
	{
		void transmitString(String message) throws ConnectionException;
		void registerBytesCounter(ByteCounter bytesCounter);
	}

	public interface StringReceiver
	{
		String pullLine() throws ConnectionException;
		String pullLineBlocking() throws ConnectionException;
		void registerBytesCounter(ByteCounter bytesCounter);
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

	public StringReceiver getMessageReceiver()
	{
		return messageReceiver;
	}
}
