package logic.messaging;

import java.io.IOException;

public class ConnectionException extends IOException
{
	public ConnectionException(String message)
	{
		super(message);
	}

	public ConnectionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ConnectionException(String message, String connectionName, Throwable cause)
	{
		super(String.format("%s, connection name: %s", message, connectionName), cause);
	}
}
