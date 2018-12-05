package window.local;

import com.sun.javafx.collections.ObservableListWrapper;
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

	private static UIEvents UIEventHandler;

	private Set<File> availableFiles = new HashSet<>();

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

		if (event.getDragboard().hasFiles())
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
		UIEventHandler.updateAvailableFileList(itemList);

		fileList.setItems(new ObservableListWrapper(itemList));
	}

	@FXML
	public void fileDragEntered()
	{
//		LOGGER.log(Level.FINE, "File drag entered");
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

	private static boolean isLocalEventsInitialized()
	{
		return null != UIEventHandler;
	}

	public static void setUIEventHandler(UIEvents localUIEvents)
	{
		LocalController.UIEventHandler = localUIEvents;
	}
}
