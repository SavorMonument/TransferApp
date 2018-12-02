package window.connection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import window.AppLogger;
import window.LocalUIEvents;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ConnectionPresenter implements Initializable
{
	@FXML
	private Button connectButton;

	@FXML
	private TextField addressField;

	private static LocalUIEvents localUIEvents;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());
	}

	public void connectButtonClicked()
	{
		if (isLocalEventsInitialized())
		{
			localUIEvents.attemptConnectionToHost(addressField.getCharacters().toString());
		}
	}

	private static boolean isLocalEventsInitialized()
	{
		return null != localUIEvents;
	}

	public static void changeLocalEventHandler(LocalUIEvents localUIEvents)
	{
		ConnectionPresenter.localUIEvents = localUIEvents;
	}
}
