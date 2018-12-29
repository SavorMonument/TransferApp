package filetransfer;

import java.io.IOException;

public class InvalidFilePath extends IOException
{
	public InvalidFilePath(String message)
	{
		super(message);
	}

	public InvalidFilePath(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidFilePath(String message, String fileName, Throwable cause)
	{
		super(String.format("%s, fileName: %s", message, fileName), cause);
	}
}
