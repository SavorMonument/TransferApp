package window;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppLogger
{
	private final static Logger LOGGER = Logger.getLogger("AppLogger");

	static {
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		LOGGER.addHandler(handler);
		LOGGER.setLevel(Level.ALL);
	}

	public static Logger getInstance()
	{
		return LOGGER;
	}
}
