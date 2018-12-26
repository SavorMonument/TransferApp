package window.remote;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import window.AppLogger;
import window.UIEvents;
import window.root.events.ConnectionStateEvent;
import window.root.events.RemoteInformationEvent;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteController implements Initializable, ConnectionStateEvent, RemoteInformationEvent
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private ObservableList<RemoteFileInfo> availableFiles = FXCollections.observableArrayList();


	private static UIEvents.FileEvents fileEvents;
	private List<FileInfo> filesAvailableOnRemote;
	private String connectionState = "";
	private String downloadPath = "";

	@FXML
	private TableView fileView;
	@FXML
	private TableColumn nameColumn;
	@FXML
	private TableColumn sizeColumn;
	@FXML
	private TableColumn speedColumn;
	@FXML
	private TableColumn progressColumn;
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
		FileInfo fileInfo;
		int index;

		if ((index = fileList.getSelectionModel().getSelectedIndex()) != -1)
		{
			fileInfo = filesAvailableOnRemote.get(index);

			LOGGER.log(Level.ALL, "Download button pressed on: " + fileInfo);
			fileEvents.requestFileForDownload(fileInfo, downloadPath);
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

	@Override
	public void setDownloadDisabled(boolean isDisabled)
	{
		downloadButton.setDisable(isDisabled);
	}

	@Override
	public void updateRemoteFileList(List<FileInfo> fileNames)
	{
		filesAvailableOnRemote = new ArrayList<>(fileNames);

		//This makes the javafx thread update the list view so I don't get: -- Not on FX application thread exception --
		Platform.runLater(() -> fileList.setItems(getFormattedUiFileList(filesAvailableOnRemote)));
	}


	private ObservableList<String> getFormattedUiFileList(List<FileInfo> filesInformation)
	{
		List<String> formattedFileList = new ArrayList<>();

		for (FileInfo f : filesInformation)
		{
			formattedFileList.add(String.format("%s : %s", f.getName(), f.getSize()));
		}
		return new ObservableListWrapper<>(formattedFileList);
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

	public static void setFileEvents(UIEvents.FileEvents fileEvents)
	{
		RemoteController.fileEvents = fileEvents;
	}
}
