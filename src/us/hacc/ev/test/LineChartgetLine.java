package us.hacc.ev.test;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;


public class LineChartgetLine extends Application {
    @Override
    public void start(Stage primaryStage) {

            NumberAxis xAxis = new NumberAxis();
            NumberAxis yAxis = new NumberAxis();
            MyLineChart chart = new MyLineChart(xAxis, yAxis, Arrays.asList(5));


            XYChart.Series<Number, Number> serie = new Series<>();

            serie.getData().add(new Data<Number, Number>(0, 5));
            serie.getData().add(new Data<Number, Number>(1, 5));
            serie.getData().add(new Data<Number, Number>(2, 5));
            serie.getData().add(new Data<Number, Number>(3, 5));
            serie.getData().add(new Data<Number, Number>(4, 5));
            serie.getData().add(new Data<Number, Number>(5, -5));
            serie.getData().add(new Data<Number, Number>(6, -5));
            serie.getData().add(new Data<Number, Number>(7, -5));

            chart.getData().add(serie);

            Path p = (Path) serie.getNode();        

            Scene scene = new Scene(chart,400,400);
            primaryStage.setScene(scene);
            primaryStage.show();



    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class MyLineChart extends LineChart<Number,Number>{


        private List<Integer> breakpointIndex;

        public MyLineChart(Axis<Number> xAxis, Axis<Number> yAxis, List<Integer> breakpointIndex) {
            super(xAxis, yAxis);
            this.breakpointIndex = breakpointIndex;
        }

        @Override
        protected void layoutPlotChildren() {
            super.layoutPlotChildren();

            Path p = (Path) getData().get(0).getNode();

            breakpointIndex.forEach(i ->{

                Data<Number, Number> discontinuousPoint = getData().get(0).getData().get(i+1);
                p.getElements().add(i+1, new MoveTo(getXAxis().getDisplayPosition( discontinuousPoint.getXValue()), getYAxis().getDisplayPosition(discontinuousPoint.getYValue())));

            });

            System.out.println("\nnew Path :");
            p.getElements().forEach(e -> System.out.println("p : " + e));
            p.setStrokeWidth(2.5);
            p.setStroke(Color.BLACK);

            //getPlotChildren().add(new Circle(50));


        }

    }
}
