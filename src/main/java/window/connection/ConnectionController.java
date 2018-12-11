package window.connection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import window.AppLogger;
import window.UIEvents;
import window.display.DisplayController;
import window.display.DisplayView;
import window.root.events.BusinessEventHandler;
import window.root.events.ConnectionRequestEvent;
import window.root.events.ConnectionStateEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ConnectionController implements Initializable, ConnectionStateEvent, ConnectionRequestEvent
{
	@FXML
	public AnchorPane displayPane;

	@FXML
	private Button connectButton;

	@FXML
	private Button disconnectButton;

	@FXML
	private TextField addressField;

	private static UIEvents.ConnectionEvents connectionEvents;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());
		addressField.setPromptText("IP address...");

		DisplayView displayView = new DisplayView();
		displayPane.getChildren().add(displayView.getView());
		DisplayController displayController = displayView.getController();

		BusinessEventHandler businessEventHandler = BusinessEventHandler.getInstance();

		businessEventHandler.addConnectionStateHandler(displayController);
		businessEventHandler.addDisplayHandler(displayController);
	}

	@FXML
	public void connectButtonClicked()
	{
		//DEBUG ONLY
		if (addressField.getCharacters().toString().equals(""))
			connectionEvents.attemptConnectionToHost("192.168.0.108");
		else
			connectionEvents.attemptConnectionToHost(addressField.getCharacters().toString());
	}

	@FXML
	public void disconnectButtonClicked()
	{
		connectionEvents.disconnect();
	}

	public void updateConnectionState(String state)
	{
		switch (state)
		{
			case "DISCONNECTED":
			{
				connectButton.setDisable(false);
				disconnectButton.setDisable(true);
			}break;
			case "CONNECTED":
			{
				connectButton.setDisable(true);
				disconnectButton.setDisable(false);
			}break;
			case "DISCONNECTING":
			{
				connectButton.setDisable(true);
				disconnectButton.setDisable(true);
			}break;
			case "CONNECTING":
			{
				connectButton.setDisable(true);
				disconnectButton.setDisable(true);
			}break;
		}
	}

	public static void setConnectionEventHandler(UIEvents.ConnectionEvents connectionEvents)
	{
		ConnectionController.connectionEvents = connectionEvents;
	}

	@Override
	public boolean confirmConnectionRequest(String url)
	{
		//TODO: make an alert to the user
		return true;
	}
}
