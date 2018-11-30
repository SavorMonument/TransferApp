package window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import window.local.LocalView;
import window.connection.ConnectionView;
import window.remote.RemoteView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class MainPresenter implements Initializable
{
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
	}
}
