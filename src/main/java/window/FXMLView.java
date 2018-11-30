package window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

public class FXMLView
{
	public Parent getView()
	{
		String[] packages = (getClass().getPackage().getName() + '.').split("[.]");
		String FXMLFilePath = packages[packages.length - 1]  + ".fxml";
		Parent parent = null;
		try
		{
			parent = FXMLLoader.load(getClass().getResource(FXMLFilePath));
		} catch (Exception e)
		{

		} finally
		{
			if (null == parent)
				handleFailure(FXMLFilePath, "");
		}

		return parent;
	}

	private void handleFailure(String fileName, String message)
	{
		AppLogger.getInstance().log(Level.WARNING, String.format("Failed to load fxml: %s\n%s", fileName, '\n', message));
		throw new IllegalArgumentException(message);
	}
}
