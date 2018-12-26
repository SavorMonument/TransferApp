package window.local;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
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

	private ObservableList<LocalFileInfo> availableFiles = FXCollections.observableArrayList();

	private static UIEvents.FileEvents fileEvents;
	private String programState = "";

	@FXML
	private TableView fileView;
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
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
//		progressColumn.setCellValueFactory(new PropertyValueFactory<>("uploadProgress"));
//		progressColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
		speedColumn.setCellValueFactory(new PropertyValueFactory<>("uploadSpeed"));

		fileView.setItems(availableFiles);
	}

	@FXML
	public void fileDragOver(DragEvent event)
	{
//		LOGGER.log(Level.FINE, "File drag over");

//		if (programState.equals("CONNECTED") && event.getDragboard().hasFiles())
		if (event.getDragboard().hasFiles())
			event.acceptTransferModes(TransferMode.ANY);
	}

	@FXML
	public void fileDragDropped(DragEvent event)
	{
		LOGGER.log(Level.FINE, "File drag dropped");
		event.getDragboard().getFiles().forEach(file ->
		{
			LocalFileInfo fileInfo = new LocalFileInfo(file.getName(),
					file.length(), file.getAbsolutePath());

			if (!file.isDirectory())
			{
				if (!availableFiles.contains(fileInfo))
					availableFiles.add(fileInfo);
			}
		});

		fileEvents.updateAvailableFiles(new ArrayList<>(availableFiles));
	}

	@FXML
	public void fileDragEntered()
	{
//		LOGGER.log(Level.FINE, "File drag entered");
	}

	@FXML
	public void triggerRemove()
	{
		int index;

		if ((index = fileView.getSelectionModel().getSelectedIndex()) != -1)
		{
			LOGGER.log(Level.ALL, "Removing file");
			availableFiles.remove(index);

			fileEvents.updateAvailableFiles(new ArrayList<>(availableFiles));
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
		availableFiles.removeAll();
	}

	public String getLocalHandler(String fileName)
	{
		String path = "";

//		for (File file : availableFiles)
//		{
//			if (file.getName().equals(fileName))
//				path = file.getAbsolutePath();
//		}

		return path;
	}

	public static void setFileEvents(UIEvents.FileEvents fileEvents)
	{
		LocalController.fileEvents = fileEvents;
	}
}
