package window.remote;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import window.AppLogger;
import window.UIEvents;
import window.handle.UIBeanRepository;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemotePresenter implements Initializable, UIBeanRepository.UIBean
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private static List<UIEvents> localUIEventHandlers = new ArrayList<>();

	@FXML
	private ListView fileList;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		AppLogger.getInstance().log(Level.FINE, "Initializing " + getClass().getName());
	}

	@FXML
	public void triggerDownload(ActionEvent actionEvent)
	{
		String elem = "nothing";
		int index;

		if ((index =  fileList.getSelectionModel().getSelectedIndex()) != -1)
		{
			LOGGER.log(Level.ALL, "Download button pressed on: " + elem);

			elem = (String) fileList.getItems().get(index);
			for (UIEvents event: localUIEventHandlers)
			{
				event.requestFileForDownload(elem);
			}
		}
	}

	public void updateRemoteFileList(List<String> fileNames)
	{
		ObservableList<String> files = new ObservableListWrapper<>(new ArrayList<>());
		files.addAll(fileNames);

		//This makes the javafx thread update the list view so I don't get: -- Not on FX application thread exception --
		Platform.runLater(() -> fileList.setItems(files));
	}

	public static void addLocalEventHandler(UIEvents eventHandle)
	{
		localUIEventHandlers.add(eventHandle);
	}
}
