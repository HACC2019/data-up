package us.hacc.ev.application;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import us.hacc.ev.application.frames.ContentPane;
import us.hacc.ev.data.DataManager;

public class ElectricVehicle extends Application
{	
	private static DataManager dataManager;
	public static SimpleDoubleProperty progress;
	private static int counter = 1;
	private Double xOffset, yOffset;
		
	public static void main(String[] args)
	{
		try
		{
			launch(args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Task<Parent> dataTask = new Task<Parent>() {
		    @Override
		    public Parent call()
		    {
		    	try
		    	{
		    		updateMessage("Import Data...");
		    		dataManager = new DataManager("Data_HACC.csv");
			    	progress.setValue(1);			    	
			    	progress.addListener(new ChangeListener<Number>() {

						@Override
						public void changed(ObservableValue<? extends Number> observable, Number oldValue,
								Number newValue)
						{
							if (counter == 8)
							{
								updateMessage("Charts created - preparing visualization...");
							}
							else
							{
								updateMessage("Loading - Chart " + counter + " out of 8 created...");
							}
							updateProgress(newValue.longValue(), 10);							
							counter++;
						}
					});			    	
			    	dataManager.createCharts();
			    	progress.setValue(10);
		    	}
		    	catch (Exception e)
		    	{
		    		e.printStackTrace();
		    	}
		        return new Pane();
		    }
		};		
		progress = new SimpleDoubleProperty(0);
		
		ProgressBar pBar = new ProgressBar();
		pBar.setId("progress-bar");
		pBar.setMinWidth(400);
		pBar.progressProperty().bind(dataTask.progressProperty());
		Label lblpBar = new Label();
		lblpBar.setFont(Font.font(20D));
		lblpBar.textProperty().bind(dataTask.messageProperty());
		ProgressIndicator pIndicator = new ProgressIndicator();
    	pIndicator.setMaxSize(30, 30);
    	pIndicator.setId("progress-indicator");
		lblpBar.setGraphic(pIndicator);
		
		VBox vbox = new VBox(lblpBar, pBar);
		vbox.setId("progress-vbox");
		vbox.setFillWidth(true);
		vbox.setAlignment(Pos.CENTER);
		
		dataTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		        @Override
		        public void handle(WorkerStateEvent event) {
		        	Stage stage = new Stage();
		        	stage.setTitle("EV-Visualizer");
		        	Scene scene = new Scene(new ContentPane());
		        	scene.getStylesheets().add("resources/style.css");
		            stage.setScene(scene);
		        	//stage.setScene(new Scene(new AdvancedScatterChart(dataManager.createXAxis(true), new NumberAxis(0, 24, 1), "Station A - Usage", "A")));
		        	stage.setMaximized(true);
		            stage.show();
		            primaryStage.close();
		        }
		});
		Thread loadingThread = new Thread(dataTask);
		loadingThread.start();
		
		Scene scene = new Scene(vbox);
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add("resources/style.css");		
		scene.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
            public void handle(MouseEvent event) 
            {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() 
		{
            public void handle(MouseEvent event) 
            {
            	primaryStage.setX(event.getScreenX() - xOffset);
            	primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("EV-Visualizer");
		primaryStage.setMaximized(false);
		primaryStage.setResizable(false);
		primaryStage.setIconified(false);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
	
	public static DataManager getDataManager()
	{
		return dataManager;
	}
}
