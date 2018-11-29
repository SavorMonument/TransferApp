package window;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.MainController;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntryPoint extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	private LocalArea localArea;
	private RemoteArea remoteArea;

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Place Holder");

		Pane rootGroup = new Pane();
		Scene mainScene = new Scene(rootGroup, 400, 350, Color.BLUE);

		Pane localPane = new Pane();
		Pane remotePane = new Pane();
		configurePanes(rootGroup, localPane, remotePane);

		localArea = new LocalArea(localPane);
		remoteArea = new RemoteArea(remotePane);

		new MainController(localArea, remoteArea).start();
		AppLogger.getInstance().log(Level.ALL, "Started the main controller");

		primaryStage.setScene(mainScene);
		primaryStage.show();
	}

	private void configurePanes(Pane rootGroup, Pane localGroup, Pane remoteGroup)
	{
		rootGroup.getChildren().addAll(localGroup, remoteGroup);

		localGroup.setStyle("-fx-background-color: white;");
		remoteGroup.setStyle("-fx-background-color: black;");

		localGroup.setPrefSize(rootGroup.getWidth(), rootGroup.getHeight() / 2);
		remoteGroup.setPrefSize(rootGroup.getWidth(), rootGroup.getHeight() / 2);
		remoteGroup.setLayoutY(rootGroup.getHeight() / 2);
	}
}
