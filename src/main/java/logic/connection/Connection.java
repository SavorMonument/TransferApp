package logic.connection;

import com.sun.istack.internal.NotNull;
import network.ConnectionException;

import java.io.Closeable;
import java.net.InetAddress;

public abstract class Connection implements Closeable
{
	private StringTransmitter messageTransmitter;
	private StringReceiver messageReceiver;

	public Connection(@NotNull StringTransmitter messageTransmitter, @NotNull StringReceiver messageReceiver)
	{
		assert null != messageTransmitter : "Invalid StringTransmitter";
		assert null != messageReceiver : "Invalid messageReceiver";

		this.messageTransmitter = messageTransmitter;
		this.messageReceiver = messageReceiver;
	}

	//-------------------------------------------------------
	public interface StringTransmitter
	{
		void transmitString(String message) throws ConnectionException;
	}

	public interface StringReceiver
	{
		String pullLine() throws ConnectionException;
		String pullLineBlocking() throws ConnectionException;
	}
	//-------------------------------------------------------

	abstract public boolean isConnected();
	abstract public InetAddress getLocalAddress();
	abstract public InetAddress getRemoteAddress();
	abstract public int getLocalPort();
	abstract public int getRemotePort();

	@Override
	abstract public void close();

	public StringTransmitter getMessageTransmitter()
	{
		return messageTransmitter;
	}

	public StringReceiver getMessageReceiver()
	{
		return messageReceiver;
	}
}
