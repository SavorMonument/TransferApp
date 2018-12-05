package window;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import logic.BusinessEvents;
import logic.LogicController;
import window.connection.ConnectionController;
import window.local.LocalController;
import window.local.LocalView;
import window.connection.ConnectionView;
import window.remote.RemoteController;
import window.remote.RemoteView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable, BusinessEvents
{
	private static final Logger LOGGER = AppLogger.getInstance();

	@FXML
	private AnchorPane local;
	private LocalController localPresenter;

	@FXML
	private AnchorPane remote;
	private RemoteController remotePresenter;

	@FXML
	private AnchorPane connection;
	private ConnectionController connectionPresenter;

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

		localPresenter = localView.getController();
		remotePresenter = remoteView.getController();
		connectionPresenter = connectionView.getController();

		new LogicController(this).start();
	}

	@Override
	public void updateRemoteFileList(List<String> fileNames)
	{
		LOGGER.log(Level.ALL,String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), fileNames.toString()));

		remotePresenter.updateRemoteFileList(fileNames);
	}

	@Override
	public boolean confirmConnectionRequest(String url)
	{
		LOGGER.log(Level.ALL,String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), url));
		return true;
	}

	@Override
	public void printMessageOnDisplay(String message)
	{
		LOGGER.log(Level.ALL,String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), message));
	}

	@Override
	public String getLocalFilePath(String fileName)
	{
		LOGGER.log(Level.ALL,String.format(
				"Business event: %s with %s", this.getClass().getEnclosingMethod(), fileName));
		return localPresenter.getFilePath(fileName);
	}
}