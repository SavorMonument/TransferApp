package window.root;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import logic.LogicController;
import window.AppLogger;
import window.connection.ConnectionController;
import window.display.DisplayController;
import window.display.DisplayView;
import window.local.LocalController;
import window.local.LocalView;
import window.connection.ConnectionView;
import window.remote.RemoteController;
import window.remote.RemoteView;
import window.root.events.BusinessEventHandler;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable
{
	private static final Logger LOGGER = AppLogger.getInstance();

	@FXML
	private AnchorPane local;

	@FXML
	private AnchorPane remote;

	@FXML
	private AnchorPane connection;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());

		LocalView localView = new LocalView();
		RemoteView remoteView = new RemoteView();
		ConnectionView connectionView = new ConnectionView();

		local.getChildren().add(localView.getView());
		remote.getChildren().add(remoteView.getView());
		connection.getChildren().add(connectionView.getView());

		LocalController localController = localView.getController();
		RemoteController remoteController = remoteView.getController();
		ConnectionController connectionController = connectionView.getController();

		BusinessEventHandler businessEventHandler = BusinessEventHandler.getInstance();

		businessEventHandler.addConnectionStateHandler(localController);
		businessEventHandler.addConnectionStateHandler(connectionController);
		businessEventHandler.addConnectionStateHandler(remoteController);

		businessEventHandler.addRemoteInformationHandler(remoteController);

		businessEventHandler.setConnectionRequestHandler(connectionController);

		businessEventHandler.setLocalInformationHandler(localController);

		new LogicController(businessEventHandler).start();
	}


}
