package window.remote;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import logic.logicController;
import logic.RemoteUIEvents;
import window.AppLogger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemotePresenter implements Initializable
{
	private static final Logger LOGGER = AppLogger.getInstance();

	@FXML
	private ListView fileList;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());
		new logicController(new RemoteUIEventsHandler()).start();
	}

	class RemoteUIEventsHandler implements RemoteUIEvents
	{
		@Override
		public boolean updateRemoteFileList(List<String> fileNames)
		{
			ObservableList<String> files = (ObservableList<String>) fileNames;
			fileList.setItems(files);
			return false;
		}

		@Override
		public boolean shouldAcceptConnectionFrom(String url)
		{
			LOGGER.log(Level.ALL, "Received connection request from: " + url);
			return true;
		}
	}
}
