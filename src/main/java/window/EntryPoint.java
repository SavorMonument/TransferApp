package window;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EntryPoint extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("Place Holder");

		Scene mainScene = new Scene(new MainView().getView());

		primaryStage.setScene(mainScene);
		primaryStage.show();
	}
}
