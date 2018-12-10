package window.local;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import window.AppLogger;
import window.UIEvents;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalController implements Initializable
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private static UIEvents.FileEvents fileEvents;
	private Set<File> availableFiles = new HashSet<>();
	private String programState = "";

	@FXML
	private ListView fileList;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());
	}

	@FXML
	public void fileDragOver(DragEvent event)
	{
//		LOGGER.log(Level.FINE, "File drag over");

		if (programState.equals("CONNECTED") && event.getDragboard().hasFiles())
			event.acceptTransferModes(TransferMode.ANY);
	}

	@FXML
	public void fileDragDropped(DragEvent event)
	{
		LOGGER.log(Level.FINE, "File drag dropped");
		event.getDragboard().getFiles().forEach(file ->
		{
			if (!file.isDirectory())
				availableFiles.add(file);
		});

		List<File> itemList = new ArrayList<>(availableFiles);

		fileList.setItems(new ObservableListWrapper(itemList));
		fileEvents.updateAvailableFileList(itemList);
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

		if ((index =  fileList.getSelectionModel().getSelectedIndex()) != -1)
		{
			File file = (File) fileList.getItems().get(index);
			LOGGER.log(Level.ALL, "Removing file: " + file.getName());

			availableFiles.remove(file);
			List<File> itemList = new ArrayList<>(availableFiles);

			//TODO: Change this to take a set
			fileEvents.updateAvailableFileList(itemList);

			fileList.setItems(new ObservableListWrapper(itemList));
			fileEvents.updateAvailableFileList(new ArrayList<>(availableFiles));
		}
	}

	public void updateConnectionState(String state)
	{
		programState = state;
		if (state.equals("DISCONNECTED"))
		{
			restFileListItems();
		}
	}

	private void restFileListItems()
	{
		availableFiles = new HashSet<>();
		Platform.runLater(() ->	fileList.setItems(new ObservableListWrapper(new ArrayList())));
	}

	public String getFilePath(String fileName)
	{
		String path = "";

		for(File file: availableFiles)
		{
			if (file.getName().equals(fileName))
				path = file.getAbsolutePath();
		}

		return path;
	}

	public static void setFileEvents(UIEvents.FileEvents fileEvents)
	{
		LocalController.fileEvents = fileEvents;
	}
}
