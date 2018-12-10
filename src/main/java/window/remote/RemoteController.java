package window.remote;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import window.AppLogger;
import window.UIEvents;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteController implements Initializable
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private static UIEvents.FileEvents fileEvents;
	private String connectionState = "";
	private String downloadPath = "";

	@FXML
	private ListView fileList;

	@FXML
	private Button downloadButton;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());
		downloadButton.setDisable(true);
	}

	@FXML
	public void triggerDownload(ActionEvent actionEvent)
	{
		String elem = "nothing";
		int index;

		if ((index = fileList.getSelectionModel().getSelectedIndex()) != -1)
		{
			elem = (String) fileList.getItems().get(index);
			LOGGER.log(Level.ALL, "Download button pressed on: " + elem);
			fileEvents.requestFileForDownload(elem);
		}
	}

	@FXML
	public void chooseFile(ActionEvent actionEvent)
	{
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Chose location");
		File directory = chooser.showDialog(fileList.getScene().getWindow());

		if (null != directory)
		{
			downloadPath = directory.getAbsolutePath();
			updateConnectionState(connectionState);
		}
	}

	@FXML
	public void updateRemoteFileList(Set<String> fileNames)
	{
		ObservableList<String> files = new ObservableListWrapper<>(new ArrayList<>());
		files.addAll(fileNames);

		//This makes the javafx thread update the list view so I don't get: -- Not on FX application thread exception --
		Platform.runLater(() -> fileList.setItems(files));
	}

	public void updateConnectionState(String state)
	{
		connectionState = state;
		if (state.equals("DISCONNECTED"))
		{
			downloadButton.setDisable(true);
			restFileListItems();
		} else if (state.equals("CONNECTED"))
		{
			if (!downloadPath.equals(""))
			{
				downloadButton.setDisable(false);
			}
		}
	}

	private void restFileListItems()
	{
		Platform.runLater(() -> fileList.setItems(new ObservableListWrapper(new ArrayList())));
	}

	public String getDownloadLocation()
	{
		return downloadPath;
	}

	public static void setFileEvents(UIEvents.FileEvents fileEvents)
	{
		RemoteController.fileEvents = fileEvents;
	}
}
