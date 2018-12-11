package window;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import window.root.MainView;

public class EntryPoint extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("J");

		Parent mainView = new MainView().getView();
		Scene mainScene = new Scene(mainView);

		primaryStage.setMinWidth(mainView.minWidth(-1) + 10);
		primaryStage.setMinHeight(mainView.minHeight(-1) + 20);

		primaryStage.setScene(mainScene);
		primaryStage.show();
	}
}
