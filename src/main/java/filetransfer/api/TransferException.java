package filetransfer.api;

import java.io.IOException;

public class TransferException extends IOException
{
	public TransferException(String message)
	{
		super(message);
	}

	public TransferException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TransferException(String message, String connectionName, Throwable cause)
	{
		super(String.format("%s, connection name: %s", message, connectionName), cause);
	}
}
