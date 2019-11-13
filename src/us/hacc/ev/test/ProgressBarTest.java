package us.hacc.ev.test;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProgressBarTest extends Application
{
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
		Task<Parent> yourTaskName = new Task<Parent>() {
		    @Override
		    public Parent call() {
		        // DO YOUR WORK

		        //wait(3000);
				
		    	System.out.println("start update progress");
				
				for(int i=0; i<100000000; i++)
				{
					updateProgress(i, 100000000);
				}
				
				System.out.println("end update progress");
				//wait(3000);
				//updateProgress(1000, 1000);

				//method to set labeltext
				//updateMessage("hello progress messag text");
		        
		        
		        return new Pane();
		    }
		};

		//ProgressBar
		ProgressBar pBar = new ProgressBar();
		//Load Value from Task
		pBar.progressProperty().bind(yourTaskName.progressProperty());
		//New Loading Label
		Label statusLabel = new Label();
		//Get Text
		statusLabel.setText("Loading...");
		//Layout
		VBox root = new VBox(statusLabel, pBar);
		//SetFill Width TRUE
		root.setFillWidth(true);
		//Center Items
		root.setAlignment(Pos.CENTER);

		//SetOnSucceeded methode 
		yourTaskName.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
		        @Override
		        public void handle(WorkerStateEvent event) {
		            System.out.println("Finish");
		        }
		});

		//Start Thread
		Thread loadingThread = new Thread(yourTaskName);
		loadingThread.start();		
		System.out.println("thread startet");
		
        Scene scene = new Scene(new StackPane(pBar));
		primaryStage.setScene(scene);
		primaryStage.show();
		System.out.println("stage showing");
		System.out.println("thread joined");
		
	}

}
