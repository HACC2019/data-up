package us.hacc.ev.application.charts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import us.hacc.ev.application.ChargeCycle;
import us.hacc.ev.application.ElectricVehicle;

public class AdvancedStackedBarChart extends StackedBarChart<String, Number>
{
	private Series<String, Number> seriesOnPeak, seriesOffPeak, seriesMidDay;
	private Calendar cal;
	private Date today;
	private ContextMenu cMenu;
	
	public AdvancedStackedBarChart(CategoryAxis xAxis, NumberAxis yAxis, boolean loadAll)
	{
		super(xAxis, yAxis);
		setTitle("Daytime Usage"); //temp !!!!!!
		setAnimated(false);
		cal = Calendar.getInstance();
		cal.set(2019, 8, 1, 0, 0, 0);
		today = cal.getTime();
		yAxis.setAnimated(false);
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(100);
		yAxis.setTickUnit(10);
		yAxis.setTickLabelFormatter(new StringConverter<Number>() 
		{
			public String toString(Number object) 
			{			    
				return object == null ? "" : object.longValue() + " %"; 
			}
			
			public Number fromString(String string) 
			{
				return Long.parseLong(string);
			}
		});
		xAxis.setAnimated(false);
		xAxis.categorySpacingProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				setCategoryGap(newValue.doubleValue()/2);
			}
		});
		xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList("Station A", "Station B"))); //TODO observable needed?
		
		cMenu = new ContextMenu();
		
		addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event)
			{
				if ( event.isSecondaryButtonDown() && !cMenu.isShowing() )
        		{
        			cMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        		}
				else if ( event.getEventType() == MouseEvent.MOUSE_PRESSED && cMenu.isShowing() )
    			{
    				cMenu.hide();
    			}
			}
		});
		
		
		seriesOnPeak = new Series<String, Number>();
		seriesOffPeak = new Series<String, Number>();
		seriesMidDay = new Series<String, Number>();
		seriesOnPeak.setName("on-peak");
		seriesOffPeak.setName("off-peak");
		seriesMidDay.setName("mid-day");
					
		if (loadAll)
		{
			prepareData(0, false);
		}
		else
		{
			prepareData(2, false);
		}
		
		
		getData().add(seriesOnPeak);
		getData().add(seriesOffPeak);
		getData().add(seriesMidDay);
	}
	
	public void prepareData(int weeks, boolean clearData)
	{
		boolean loadAll = (weeks == 0) ? true : false;
		if (clearData)
		{
			seriesOnPeak.getData().forEach(a -> a.setYValue(0));	//TODO workaround...
			seriesOffPeak.getData().forEach(a -> a.setYValue(0));	//TODO workaround...
			seriesMidDay.getData().forEach(a -> a.setYValue(0));	//TODO workaround...
			seriesOnPeak.getData().clear();
			seriesOffPeak.getData().clear();
			seriesMidDay.getData().clear();
		}
    	
		cal.add(Calendar.DAY_OF_YEAR, -(7*weeks));
		HashMap<String, HashMap<String, Integer>> hmapStation = new HashMap<String, HashMap<String, Integer>>();
		Calendar calendar = Calendar.getInstance();
		for ( Entry<String, ArrayList<ChargeCycle>> eSet : ElectricVehicle.getDataManager().entrySet() )
		{
			HashMap<String, Integer>hmapDaytime = new HashMap<String, Integer>();
			hmapDaytime.put("onPeak", 0);
			hmapDaytime.put("offPeak", 0);
			hmapDaytime.put("midDay", 0);
			hmapStation.put(eSet.getKey(), hmapDaytime);
			
			for ( ChargeCycle cc : eSet.getValue() )
			{
				if (cc.getStartTime().getTime()-cal.getTime().getTime() < 0 && !loadAll)
				{
					continue;
				}
				
				calendar.setTime(cc.getStartTime());
				
				if (calendar.get(Calendar.HOUR_OF_DAY) >= 17 && calendar.get(Calendar.HOUR_OF_DAY) < 22)
				{
					hmapDaytime.put("onPeak", hmapDaytime.get("onPeak")+1);
				}
				else if (calendar.get(Calendar.HOUR_OF_DAY) >= 9 && calendar.get(Calendar.HOUR_OF_DAY) < 17)
				{
					hmapDaytime.put("midDay", hmapDaytime.get("midDay")+1);
				}
				else
				{
					hmapDaytime.put("offPeak", hmapDaytime.get("offPeak")+1);
				}
			}
		}
		cal.setTime(today);
		
		int stationAges = hmapStation.get("A").get("onPeak") + hmapStation.get("A").get("midDay") + hmapStation.get("A").get("offPeak"); // = 100%
		seriesOnPeak.getData().add(createData("Station A", (hmapStation.get("A").get("onPeak")*100.0)/stationAges));
		seriesOffPeak.getData().add(createData("Station A", (hmapStation.get("A").get("offPeak")*100.0)/stationAges));
		seriesMidDay.getData().add(createData("Station A", (hmapStation.get("A").get("midDay")*100.0)/stationAges));
		
		int stationBges = hmapStation.get("B").get("onPeak") + hmapStation.get("B").get("midDay") + hmapStation.get("B").get("offPeak"); // = 100%
		seriesOnPeak.getData().add(createData("Station B", (hmapStation.get("B").get("onPeak")*100.0)/stationBges));
		seriesOffPeak.getData().add(createData("Station B", (hmapStation.get("B").get("offPeak")*100.0)/stationBges));
		seriesMidDay.getData().add(createData("Station B", (hmapStation.get("B").get("midDay")*100.0)/stationBges));
	}
	
	private Data<String, Number> createData(String category, double value)
	{
		Data<String, Number> data =  new Data<String, Number>(category, value);
        Label label = new Label(String.format("%.1f", value) + " %");
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold");
        data.setNode(new StackPane(label));
        return data;
    }
	
	public ContextMenu getContextMenu()
	{
		return cMenu;
	}
}
