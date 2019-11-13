package us.hacc.ev.application.charts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class StationPieChart extends PieChart
{
	
	public StationPieChart(String title)
	{
		super();
		setTitle(title);
		setClockwise(true);
		setStartAngle(90);
		setLegendVisible(false);
		
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( 
				   new PieChart.Data("used", 30), 
				   new PieChart.Data("not used", 70));
		
		getData().addAll(pieChartData);
	}
}
