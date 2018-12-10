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
			{
				//Checks if there is a file with the same name in the set
				File found = null;
				for (File f : availableFiles)
				{
					if (f.getName().equals(file.getName()))
					{
						found = f;
					}
				}
				//If there is a file with the same name it removes it and adds the new one
				//Don't want to send two files with same name to remote even if they are
				//in different directories, it's not going to know the difference
				if (null != found)
					availableFiles.remove(found);
				availableFiles.add(file);
			}
		});

		fileList.setItems(new ObservableListWrapper(new ArrayList(availableFiles)));

		fileEvents.updateAvailableFiles(availableFiles);
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

		if ((index = fileList.getSelectionModel().getSelectedIndex()) != -1)
		{
			File file = (File) fileList.getItems().get(index);
			LOGGER.log(Level.ALL, "Removing file: " + file.getName());

			if (availableFiles.remove(file))
				fileList.getItems().remove(file);

			fileEvents.updateAvailableFiles(availableFiles);
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
		Platform.runLater(() -> fileList.setItems(new ObservableListWrapper(new ArrayList())));
	}

	public String getFilePath(String fileName)
	{
		String path = "";

		for (File file : availableFiles)
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
