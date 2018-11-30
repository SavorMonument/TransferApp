package window.remote;

import javafx.fxml.Initializable;
import window.AppLogger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class RemotePresenter implements Initializable
{


	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());

	}
}
