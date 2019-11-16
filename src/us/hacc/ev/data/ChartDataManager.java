package us.hacc.ev.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import us.hacc.ev.application.ChargeCycle;
import us.hacc.ev.application.ElectricVehicle;
import us.hacc.ev.application.charts.AdvancedScatterChart;
import us.hacc.ev.application.charts.AdvancedStackedBarChart;
import us.hacc.ev.application.charts.StationLineChart;
import us.hacc.ev.util.Tools;

public class ChartDataManager
{
	public static StationLineChart stationLineChart, stationLineChartFullScreen;
	public static AdvancedScatterChart stationTimeChartA, stationTimeChartAFullScreen, stationTimeChartB, stationTimeChartBFullScreen;
	public static AdvancedStackedBarChart stationBarChart, stationBarChartFullScreen;
	public static ObservableList<XYChart.Series<Number, Number>> lineChartSeries;
	private ObservableList<XYChart.Data<Number, Number>> stackedChartDataA, stackedChartDataB;
	private Date today, weeks2;
	private Calendar cal;
	
		
	public ChartDataManager()
	{
		lineChartSeries = FXCollections.observableArrayList();
		cal = Calendar.getInstance();
		cal.set(2019, 8, 1, 0, 0, 0);
		today = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, -14);
		weeks2 = cal.getTime();
		cal.set(2019, 8, 1, 0, 0, 0);
		
		stationLineChart = new StationLineChart(createXAxis(true), new NumberAxis(0, 50, 5), true);
		stationLineChartFullScreen = new StationLineChart(createXAxis(false), new NumberAxis(0, 50, 5), true);	//TODO maybe in new thread?
		
		createLineChartData(0);
		createStackedChartData(0);
		stationLineChart.addData(lineChartSeries);
		stationLineChartFullScreen.addData(lineChartSeries);
	}
	
	private void createStackedChartData(double min)
	{
		
	}

	
		
	public static void createLineChartData(double min)
	{
		lineChartSeries.clear();
		for ( Entry<String, ArrayList<ChargeCycle>> eSet : ElectricVehicle.getDataManager().entrySet() )
		{	
			final XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			series.setName("Station " + eSet.getKey());
			
			XYChart.Data<Number, Number> data;
			int counter = 1;
			Date prevDate = null;
			for ( ChargeCycle cc : eSet.getValue() )
			{
				try
				{
					if (cc.getDuration() < (min*60000)) //TODO duration in ms ???
					{
						continue;
					}
					
					if (prevDate == null)
					{
						prevDate = Tools.sdfDate.parse(Tools.sdfDate.format(cc.getStartTime()));
						continue;
					}
					
					if (prevDate.equals(Tools.sdfDate.parse(Tools.sdfDate.format(cc.getStartTime()))))
					{
						counter++;
						continue;
					}
					
					data = new XYChart.Data<Number, Number>();
					data.setXValue(prevDate.getTime());
					data.setYValue(counter);
					data.setExtraValue(eSet.getKey());
					series.getData().add(data);
					
					counter = 1;
					prevDate = Tools.sdfDate.parse(Tools.sdfDate.format(cc.getStartTime()));			
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
			data = new XYChart.Data<Number, Number>();
			data.setXValue(prevDate.getTime());
			data.setYValue(counter);
			data.setExtraValue(eSet.getKey());
			series.getData().add(data);
			lineChartSeries.add(series);
		}
	}
	
	private NumberAxis createXAxis(boolean dateFilter) //TODO only for test public
	{
		NumberAxis xAxis = new NumberAxis();
		if (dateFilter)
		{
			xAxis.setAutoRanging(false);
			xAxis.setLowerBound(weeks2.getTime());
			xAxis.setUpperBound(today.getTime());
		}
		else
		{
			xAxis.setAutoRanging(true);
		}		
		xAxis.setLabel("Date");
		xAxis.setTickUnit(Tools.DATE_DayInMillis);
		xAxis.setForceZeroInRange(false);
		xAxis.setTickLabelRotation(90);
		xAxis.setMinorTickVisible(true);
		xAxis.setTickLabelFormatter(new StringConverter<Number>() 
		{
			public String toString(Number object) 
			{			    
				return object == null ? "" : Tools.sdfDateShort.format(new Date(object.longValue())); 
			}
			
			public Number fromString(String string) 
			{
				return Long.parseLong(string);
			}
		});
		return xAxis;
	}
}
