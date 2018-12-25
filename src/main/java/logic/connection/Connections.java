package logic.connection;

import com.sun.istack.internal.NotNull;

import java.io.Closeable;

public class Connections implements Closeable
{
	private Connection mainConnection;
	private Connection fileReceivingConnection;
	private Connection fileTransmittingConnection;

	public Connections(@NotNull Connection mainConnection,
					   @NotNull Connection fileReceivingConnection,
					   @NotNull Connection fileTransmittingConnection)
	{
		assert null != mainConnection && mainConnection.isConnected() : "Invalid connection";
		assert null != fileReceivingConnection && fileReceivingConnection.isConnected() : "Invalid connection";
		assert null != fileTransmittingConnection && fileTransmittingConnection.isConnected() : "Invalid connection";

		this.mainConnection = mainConnection;
		this.fileReceivingConnection = fileReceivingConnection;
		this.fileTransmittingConnection = fileTransmittingConnection;
	}

	public Connection getMainConnection()
	{
		return mainConnection;
	}

	public Connection getFileReceivingConnection()
	{
		return fileReceivingConnection;
	}

	public Connection getFileTransmittingConnection()
	{
		return fileTransmittingConnection;
	}

	@Override
	public void close()
	{
		mainConnection.close();
		fileReceivingConnection.close();
		fileTransmittingConnection.close();
	}
}
