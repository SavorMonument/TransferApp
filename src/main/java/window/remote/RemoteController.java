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
import logic.messaging.FileInformation;
import window.AppLogger;
import window.ByteMultipleFormatter;
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

	private static UIEvents.FileEvents fileEvents;
	private List<FileInformation> filesAvailableOnRemote;
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
		FileInformation fileInformation;
		int index;

		if ((index = fileList.getSelectionModel().getSelectedIndex()) != -1)
		{
			fileInformation = filesAvailableOnRemote.get(index);

			LOGGER.log(Level.ALL, "Download button pressed on: " + fileInformation);
			fileEvents.requestFileForDownload(fileInformation, downloadPath);
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
	public void updateRemoteFileList(Set<FileInformation> fileNames)
	{
		filesAvailableOnRemote = new ArrayList<>(fileNames);

		//This makes the javafx thread update the list view so I don't get: -- Not on FX application thread exception --
		Platform.runLater(() -> fileList.setItems(getFormattedUiFileList(filesAvailableOnRemote)));
	}


	private ObservableList<String> getFormattedUiFileList(List<FileInformation> filesInformation)
	{
		List<String> formattedFileList = new ArrayList<>();

		for (FileInformation f : filesInformation)
		{
			formattedFileList.add(String.format("%s : %s", f.name, ByteMultipleFormatter.getFormattedBytes(f.sizeInBytes)));
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
