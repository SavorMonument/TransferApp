package window.display;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import window.ByteMultipleFormatter;
import window.root.events.ConnectionStateEvent;
import window.root.events.DisplayEvent;

import java.net.URL;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ResourceBundle;

public class DisplayController implements Initializable, ConnectionStateEvent, DisplayEvent
{
	@FXML
	public Label programStatusLabel;

	@FXML
	public Label messageLabel;

	@FXML
	public Label uploadSpeedLabel;

	@FXML
	public Label downloadSpeedLabel;

	private Queue<String> waitingMessages = new LinkedList<>();
	private MessagePrinter messagePrinter;


	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		messagePrinter = new MessagePrinter();
	}

	public void updateConnectionState(String status)
	{
		Platform.runLater(() -> programStatusLabel.setText(status));
	}

	public void printUploadSpeed(long speed)
	{
		if (speed > 0)
			Platform.runLater(() ->
					uploadSpeedLabel.setText(ByteMultipleFormatter.getFormattedBytes(speed) + "/s"));
		else
			Platform.runLater(() -> uploadSpeedLabel.setText(""));
	}

	public void printDownloadSpeed(long speed)
	{
		if (speed > 0)
			Platform.runLater(() ->
					downloadSpeedLabel.setText(ByteMultipleFormatter.getFormattedBytes(speed) + "/s"));
		else
			Platform.runLater(() -> downloadSpeedLabel.setText(""));
	}

	@Override
	public void printMessageOnDisplay(String message)
	{
		waitingMessages.add(message);
		if (!messagePrinter.isAlive())
		{
			messagePrinter = new MessagePrinter();
			messagePrinter.start();
		}
	}

	private class MessagePrinter extends Thread
	{
		private String message;

		public MessagePrinter()
		{
			setDaemon(true);
		}

		@Override
		public void run()
		{
			while (null != (message = waitingMessages.poll()))
			{
				Platform.runLater(() -> messageLabel.setText(message));
				try
				{
					Thread.sleep(5000);
				} catch (InterruptedException e)
				{
//					e.printStackTrace();
				}
			}
			Platform.runLater(() -> messageLabel.setText(""));
		}
	}
}
