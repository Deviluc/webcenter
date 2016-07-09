package visual.main;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import core.job.JobManager;
import core.lib.Misc;
import core.plugin.PluginManager;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BaseWindow extends Application {
	
	private Pane viewPane;

	@Override
	public void start(Stage primaryStage) {
		
		JobManager.getInstance();
		
		HBox baseSplit = new HBox(0);
		viewPane = new Pane();
		VBox iconSplit = new VBox(8);
		
		boolean hasMoreIcons = true;
		int i = 0;
		
		while (hasMoreIcons) {
			try {
				String title = Misc.getProperty("base", "toolList.entry." + i +".title");
				String imageName = Misc.getProperty("base", "toolList.entry." + i +".image");
				String paneName = Misc.getProperty("base", "toolList.entry." + i + ".pane");
				
				Button button = new Button();
				button.setTooltip(new Tooltip(title));
				button.setGraphic(new ImageView(Misc.loadImage(imageName)));
				
				iconSplit.getChildren().add(button);
				
				button.setOnAction(new EventHandler<ActionEvent>() {
					
					@Override
					public void handle(ActionEvent event) {
						try {
							loadPane(paneName);
						} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
				
				if (i == 0) {
					try {
						loadPane(paneName);
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				
				i++;
			} catch (Exception e) {
				hasMoreIcons = false;
			}
		}
		
		baseSplit.getChildren().addAll(iconSplit, viewPane);
		
		
		primaryStage.setScene(new Scene(baseSplit));
		primaryStage.show();
		
	}

	public static void main(String[] args) {
		PluginManager.getInstance();
		launch(args);
	}
	
	private void loadPane(final String paneName) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		GridPane pane = (GridPane) Class.forName("visual.panes." + paneName).newInstance();
		viewPane.getChildren().clear();
		viewPane.getChildren().add(pane);
	}
}
