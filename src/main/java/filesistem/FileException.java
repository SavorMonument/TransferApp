package filesistem;

import java.io.IOException;

public class FileException extends IOException
{
	public FileException(String message)
	{
		super(message);
	}

	public FileException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FileException(String message, String fileName, Throwable cause)
	{
		super(String.format("%s, fileName: %s", message, fileName), cause);
	}
}
