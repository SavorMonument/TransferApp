package window.local;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import window.AppLogger;
import window.LocalUIEvents;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalPresenter implements Initializable
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private static LocalUIEvents localUIEvents;

	private List<File> availableFiles = new ArrayList<>();

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

		ObservableList items = fileList.getItems();

		availableFiles.addAll(event.getDragboard().getFiles());
		items.addAll(event.getDragboard().getFiles());

		localUIEvents.updateAvailableFileList(availableFiles);
	}

	@FXML
	public void fileDragEntered()
	{
		LOGGER.log(Level.FINE, "File drag entered");
	}


	private static boolean isLocalEventsInitialized()
	{
		return null != localUIEvents;
	}

	public static void changeLocalEventHandler(LocalUIEvents localUIEvents)
	{
		LocalPresenter.localUIEvents = localUIEvents;
	}
}
