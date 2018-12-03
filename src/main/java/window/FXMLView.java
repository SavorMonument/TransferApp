package window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

public class FXMLView
{
	private String FXMLFilePath;

	protected FXMLLoader loader;

	public FXMLView()
	{
		String[] packages = (getClass().getPackage().getName() + '.').split("[.]");
		FXMLFilePath = packages[packages.length - 1]  + ".fxml";

		loader = new FXMLLoader(getClass().getResource(FXMLFilePath));
	}

	public <T> T getPresenter()
	{
		return loader.getController();
	}

	public Parent getView()
	{
		Parent parent = null;
		try
		{
			parent = loader.load();
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
