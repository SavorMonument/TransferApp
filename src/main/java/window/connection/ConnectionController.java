package window.connection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import window.AppLogger;
import window.UIEvents;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ConnectionController implements Initializable
{
	@FXML
	private Button connectButton;

	@FXML
	private Button disconnectButton;

	@FXML
	private TextField addressField;

	private static UIEvents localUIEvents;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());
	}

	@FXML
	public void connectButtonClicked()
	{
		if (isLocalEventsInitialized())
		{
			//DEBUG ONLY
			if (addressField.getCharacters().toString().equals(""))
				localUIEvents.attemptConnectionToHost("192.168.0.108");
			else
				localUIEvents.attemptConnectionToHost(addressField.getCharacters().toString());
		}
	}

	@FXML
	public void disconnectButtonClicked()
	{
		localUIEvents.disconnect();
	}

	private static boolean isLocalEventsInitialized()
	{
		return null != localUIEvents;
	}

	public static void changeLocalEventHandler(UIEvents localUIEvents)
	{
		ConnectionController.localUIEvents = localUIEvents;
	}
}
