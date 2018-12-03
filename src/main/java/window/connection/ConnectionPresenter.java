package window.connection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import window.AppLogger;
import window.UIEvents;
import window.handle.UIBeanRepository;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ConnectionPresenter implements Initializable, UIBeanRepository.UIBean
{
	@FXML
	private Button connectButton;

	@FXML
	private TextField addressField;

	private static UIEvents localUIEvents;

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

	public static void changeLocalEventHandler(UIEvents localUIEvents)
	{
		ConnectionPresenter.localUIEvents = localUIEvents;
	}
}
