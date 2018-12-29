package window.remote;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import logic.FileHandle;
import model.FileInfo;
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

	private ObservableList<RemoteFileInfoObservable> availableFiles = FXCollections.observableArrayList(param -> new Observable[]{param});


	private static UIEvents.FileEvents fileEvents;

	private RefreshOnEventListener refreshOnEventListener = new RefreshOnEventListener();
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

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
		speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));

		progressColumn.setCellFactory(param -> new GreenWhenFullProgressBar());
		progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));

		fileView.setItems(availableFiles);
	}

	public static void setFileEvents(UIEvents.FileEvents fileEvents)
	{
		RemoteController.fileEvents = fileEvents;
	}

	@FXML
	public void triggerDownload(ActionEvent actionEvent)
	{
		FileInfo fileInfo;
		int index;

		if ((index = fileView.getSelectionModel().getSelectedIndex()) != -1)
		{
			RemoteFileInfo remoteFileInfo = availableFiles.get(index);
			fileInfo = new FileInfo(remoteFileInfo.getName(), remoteFileInfo.getSizeInBytes());

			LOGGER.log(Level.ALL, "Download button pressed on: " + fileInfo);
			fileEvents.requestFileForDownload(fileInfo);
		}
	}

	@FXML
	public void chooseDownloadLocation(ActionEvent actionEvent)
	{
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Chose location");
		File directory = chooser.showDialog(fileView.getScene().getWindow());

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

	public void updateFileList(List<FileInfo> fileInfos)
	{
		availableFiles = constructNewObservableList(fileInfos);
		Platform.runLater(() -> fileView.setItems(availableFiles));
	}

	private ObservableList<RemoteFileInfoObservable> constructNewObservableList(List<FileInfo> fileInfos)
	{
		ObservableList<RemoteFileInfoObservable> remoteFileInfos = FXCollections.observableArrayList(param -> new Observable[]{param});

		for (FileInfo fInfo : fileInfos)
		{
			RemoteFileInfoObservable observableInfo = new RemoteFileInfoObservable(fInfo.getName(), fInfo.getSizeInBytes());
			observableInfo.addListener(refreshOnEventListener);
			remoteFileInfos.add(observableInfo);
		}

		return remoteFileInfos;
	}

	public void updateConnectionState(String state)
	{
		connectionState = state;
		if (state.equals("DISCONNECTED"))
		{
			downloadButton.setDisable(true);
			resetFileListItems();
		} else if (state.equals("CONNECTED"))
		{
			if (!downloadPath.equals(""))
			{
				downloadButton.setDisable(false);
			}
		}
	}

	private void resetFileListItems()
	{
		availableFiles.remove(0, availableFiles.size());
	}

	@Override
	public FileHandle getRemoteFileHandle(FileInfo fileInfo)
	{
		for (RemoteFileInfoObservable fi : availableFiles)
		{
			if (fi.getName().equals(fileInfo.getName()) && (fi.getSizeInBytes() == fileInfo.getSizeInBytes()))
				return fi;
		}
		return null;
	}

	@Override
	public String getDownloadLocation()
	{
		return downloadPath;
	}

	class GreenWhenFullProgressBar extends ProgressBarTableCell
	{
		@Override
		public void updateItem(Double item, boolean empty)
		{
			super.updateItem(item, empty);
			if (!empty && null != item && item > 0.99)
				setStyle("-fx-accent: green");
		}
	}

	//Could not get the table to refresh on changes to LocalFileInfoObservable so used this
	class RefreshOnEventListener implements InvalidationListener
	{
		@Override
		public void invalidated(Observable observable)
		{
			fileView.refresh();
		}
	}
}
