package window;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.PriorityQueue;

public class LocalArea implements LocalUI
{
	private Node node;

	private PriorityQueue<File> filesNotProcessed = new PriorityQueue<File>();
	private String connectionRequestURL;

	public LocalArea(Pane pane)
	{
		this.node = pane;

		HandleDragEvent dragHandler = new HandleDragEvent();
		pane.setOnDragOver(dragHandler);
		pane.setOnDragDropped(dragHandler);

		TextField socketAddressTextField = new TextField();
		Button socketAddressButton = new Button("Submit");
		socketAddressButton.setOnAction(new SubmitButtonHandler(socketAddressTextField));
		socketAddressButton.setLayoutX(150);

		pane.getChildren().addAll(socketAddressTextField, socketAddressButton);
	}

	public boolean hasUnprocessedFiles()
	{
		return !filesNotProcessed.isEmpty();
	}

	public File getMarkedFile()
	{
		return filesNotProcessed.poll();
	}

	public boolean hasPendingConnectionRequest()
	{
		return null != connectionRequestURL;
	}

	public String getConnectionRequest()
	{
		String request = connectionRequestURL;
		connectionRequestURL = null;
		return request;
	}

	class SubmitButtonHandler implements EventHandler<ActionEvent>
	{
		TextField assignedTextField;

		public SubmitButtonHandler(TextField assignedTextField)
		{
			this.assignedTextField = assignedTextField;
		}

		public void handle(ActionEvent event)
		{
			connectionRequestURL = assignedTextField.getCharacters().toString();
//			assignedTextField.deleteText(assignedTextField.getLength());
			event.consume();
		}
	}

	class HandleDragEvent implements EventHandler<DragEvent>
	{
		public void handle(DragEvent event)
		{
			if (event.getEventType() == DragEvent.DRAG_OVER)
			{
				if (event.getDragboard().hasFiles())
				{
					event.acceptTransferModes(TransferMode.ANY);
				}
			} else
			{
				if (event.getEventType() == DragEvent.DRAG_DROPPED)
				{
					if (event.getDragboard().hasFiles())
					{
						filesNotProcessed.addAll(event.getDragboard().getFiles());
						System.out.println("Files Dropped: " + event.getDragboard().getFiles().toString());
						event.setDropCompleted(true);
					} else
						event.setDropCompleted(false);
				}
			}
			event.consume();
		}
	}

}
