package window.local;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import logic.FileHandle;
import model.FileInfo;
import window.AppLogger;
import window.UIEvents;
import window.root.events.ConnectionStateEvent;
import window.root.events.LocalInformationEvent;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalController implements Initializable, ConnectionStateEvent, LocalInformationEvent
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private ObservableList<LocalFileInfoObservable> availableFiles = FXCollections.observableArrayList(param -> new Observable[]{param});

	private static UIEvents.FileEvents fileEvents;

	private RefreshOnEventListener refreshOnEventListener = new RefreshOnEventListener();
	private String programState = "";

	@FXML
	public TableView<LocalFileInfoObservable> fileView;
	@FXML
	public TableColumn locationColumn;
	@FXML
	public TableColumn nameColumn;
	@FXML
	public TableColumn sizeColumn;
	@FXML
	public TableColumn progressColumn;
	@FXML
	public TableColumn speedColumn;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
//		progressColumn.setCellValueFactory(new PropertyValueFactory<>("uploadProgress"));
//		progressColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
		speedColumn.setCellValueFactory(new PropertyValueFactory<>("uploadSpeed"));

		fileView.setItems(availableFiles);
	}

	@FXML
	public void fileDragOver(DragEvent event)
	{
		if (programState.equals("CONNECTED") && event.getDragboard().hasFiles())
			event.acceptTransferModes(TransferMode.ANY);
	}

	@FXML
	public void fileDragDropped(DragEvent event)
	{
		LOGGER.log(Level.FINE, "File drag dropped");
		event.getDragboard().getFiles().forEach(file ->
		{
			LocalFileInfoObservable fileInfo = new LocalFileInfoObservable(file.getName(),
					file.length(), file.getAbsolutePath());
			fileInfo.addListener(refreshOnEventListener);

			if (!file.isDirectory())
			{
				if (!availableFiles.contains(fileInfo))
				{
					availableFiles.add(fileInfo);
					System.out.println(availableFiles.size());

				}
			}
		});

		fileEvents.updateAvailableFiles(getListOfFileInfos(availableFiles));
	}

	private List<FileInfo> getListOfFileInfos(List<LocalFileInfoObservable> availableFiles)
	{
		List<FileInfo> fileInfos = new ArrayList<>();

		for (LocalFileInfo availableFile : availableFiles)
		{
			fileInfos.add(availableFile.getFileInfo());
		}
		return fileInfos;
	}

	@FXML
	public void triggerRemove()
	{
		int index;

		if ((index = fileView.getSelectionModel().getSelectedIndex()) != -1)
		{
			LOGGER.log(Level.ALL, "Removing file");
			availableFiles.remove(index);

			fileEvents.updateAvailableFiles(getListOfFileInfos(availableFiles));
		}
	}

	public void updateConnectionState(String state)
	{
		programState = state;
		if (state.equals("DISCONNECTED"))
		{
			resetFileListItems();
		}
	}

	private void resetFileListItems()
	{
		availableFiles.remove(0, availableFiles.size());
	}

	public FileHandle getLocalHandler(FileInfo fileInfo)
	{
		for (LocalFileInfoObservable fi : availableFiles)
		{
			if (fi.getName().equals(fileInfo.getName()) && (fi.getSizeInBytes() == fileInfo.getSizeInBytes()))
				return fi;
		}
		return null;
	}

	@Override
	public String getLocalFilePath(FileInfo fileInfo)
	{
		for (LocalFileInfo fi : availableFiles)
		{
			if (fi.getName().equals(fileInfo.getName()) && (fi.getSizeInBytes() == fileInfo.getSizeInBytes()))
				return fi.getFullPath();
		}
		return null;
	}

	public static void setFileEvents(UIEvents.FileEvents fileEvents)
	{
		LocalController.fileEvents = fileEvents;
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
