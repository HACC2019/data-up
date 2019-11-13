package us.hacc.ev.application.charts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import us.hacc.ev.application.ChargeCycle;
import us.hacc.ev.application.ElectricVehicle;
import us.hacc.ev.util.Tools;

public class StationLineChart extends LineChart<Number, Number>
{
	private Rectangle rect;
	private ContextMenu cMenu;
	
	public StationLineChart(NumberAxis xAxis, NumberAxis yAxis, boolean loadData)
	{
		super(xAxis, yAxis);
		setTitle("Station Overview");
		yAxis.setLabel("Amount");
		yAxis.setMinorTickVisible(false);
		
		rect = new Rectangle();
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        
        Calendar cal = Calendar.getInstance();
        
        cMenu = new ContextMenu();
		
		EventHandler<MouseEvent> zoomHandler = new EventHandler<MouseEvent>()
        {
        	private Number xPosStart, xPosEnd, yPosStart, yPosEnd;
        	private SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
        	private SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
        	private SimpleDoubleProperty rectX = new SimpleDoubleProperty();
        	private SimpleDoubleProperty rectY = new SimpleDoubleProperty();
        	
        	public void handle(MouseEvent event)
			{	        		
        		if ( event.getEventType() == MouseEvent.MOUSE_PRESSED )
        		{
        			if ( cMenu.isShowing() )
        			{
        				cMenu.hide();
        			}
        			
        			if ( event.getClickCount() > 1 )
	                {
	                	//xAxis.setAutoRanging(true);
	                	return;
	                }
        			        			
        			Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
	                double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX();
	                double yAxisLoc = yAxis.sceneToLocal(pointInScene).getY();
	                xPosStart = Math.round(xAxis.getValueForDisplay(xAxisLoc).doubleValue());
	                yPosStart = Math.round(yAxis.getValueForDisplay(yAxisLoc).doubleValue());
	                
	                rect.setX(event.getX());
	                rect.setY(event.getY());
	                rectinitX.set(event.getX());
	                rectinitY.set(event.getY());              
        		}
        		if ( event.getEventType() == MouseEvent.MOUSE_RELEASED )
        		{        			
        			Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
	                double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX();
	                double yAxisLoc = yAxis.sceneToLocal(pointInScene).getY();
	                xPosEnd = Math.round(xAxis.getValueForDisplay(xAxisLoc).doubleValue());
	                yPosEnd = Math.round(yAxis.getValueForDisplay(yAxisLoc).doubleValue());
	                rect.setVisible(false);
	                
	                if ( !yPosStart.equals(yPosEnd) )
	                {
	                	xAxis.setAutoRanging(false);
	                	xAxis.setTickUnit(Tools.DATE_DayInMillis);
	                	
	                	if ( xPosEnd.doubleValue() < xPosStart.doubleValue() ) //draw from right to left
	                	{
	                		cal.setTime(new Date(xPosStart.longValue()));
	                		cal.set(Calendar.HOUR_OF_DAY, 0);
	                		cal.set(Calendar.MINUTE, 0);
	                		cal.set(Calendar.SECOND, 0);
	                		cal.add(Calendar.DAY_OF_MONTH, 1);
	                		xAxis.setUpperBound(cal.getTime().getTime());
	                		
	                		cal.setTime(new Date(xPosEnd.longValue()));
	                		cal.set(Calendar.HOUR_OF_DAY, 0);
	                		cal.set(Calendar.MINUTE, 0);
	                		cal.set(Calendar.SECOND, 0);	                			
            			    xAxis.setLowerBound(cal.getTime().getTime());			                	  
	                	}
	                	else
	                	{
	                		cal.setTime(new Date(xPosEnd.longValue()));
	                		cal.set(Calendar.HOUR_OF_DAY, 0);
	                		cal.set(Calendar.MINUTE, 0);
	                		cal.set(Calendar.SECOND, 0);
	                		cal.add(Calendar.DAY_OF_MONTH, 1);
	                		xAxis.setUpperBound(cal.getTime().getTime());
	                		
	                		cal.setTime(new Date(xPosStart.longValue()));
	                		cal.set(Calendar.HOUR_OF_DAY, 0);
            			    cal.set(Calendar.MINUTE, 0);
            			    cal.set(Calendar.SECOND, 0);	                		
            			    xAxis.setLowerBound(cal.getTime().getTime());
	                	}		                			                
	                }	 	        			
        		}
        		
        		if ( event.getEventType() == MouseEvent.MOUSE_DRAGGED )
        		{
        			rect.setVisible(true);
        			Double dx = event.getX() - rectinitX.getValue();
        			Double dy = event.getY() - rectinitY.getValue();
        			
        			if (dx < 0)
        			{
        				rectX.set(event.getX());
        				rect.setTranslateX(dx);
        				rect.widthProperty().bind(rectinitX.subtract(rectX));
        			}
        			else
        			{
        				rectX.set(event.getX());
        				rect.setTranslateX(0);
        				rect.widthProperty().bind(rectX.subtract(rectinitX));
        			}
        			
        			if(dy < 0)
        			{
        				rectY.set(event.getY());
        				rect.setTranslateY(dy);
        				rect.heightProperty().bind(rectinitY.subtract(rectY));
        			}
        			else
        			{
        				rectY.set(event.getY());
        				rect.setTranslateY(0);
        				rect.heightProperty().bind(rectY.subtract(rectinitY));
        			}	        			
        		}
        		if ( event.isSecondaryButtonDown() && !cMenu.isShowing() )
        		{
        			cMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        		}
			}
		};
		addEventHandler(MouseEvent.ANY, zoomHandler);
		
		if (loadData)
		{
			loadData();
		}
		
		getChildren().add(rect);
	}
	
	private void loadData()
	{
		CheckMenuItem cMenuItem;		
		for ( Entry<String, ArrayList<ChargeCycle>> eSet : ElectricVehicle.getDataManager().entrySet() )
		{
			final XYChart.Series<Number, Number> series = new Series<Number, Number>();
			series.setName("Station " + eSet.getKey());
			getData().add(series);
			
			cMenuItem = new CheckMenuItem(series.getName());
			cMenuItem.setSelected(true);
			cMenuItem.setOnAction(event -> {
				if ( !((CheckMenuItem)event.getSource()).isSelected() )
				{
					getData().remove(series);
				}
				else
				{
					getData().add(series);
				}
			});
			cMenu.getItems().add(cMenuItem);			
			
			XYChart.Data<Number, Number> data;
			int counter = 1;
			Date prevDate = null;
			for ( ChargeCycle cc : eSet.getValue() )
			{
				try
				{
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
					series.getData().add(data);					
					Tooltip tooltip = new Tooltip("Date: " + Tools.sdfDate.format(prevDate) + "\nAmount: " + counter);										
					data.getNode().setOnMouseEntered(event -> {
						tooltip.show(this, event.getScreenX(), event.getScreenY()+20); //+20 to avoid blinking
					});
					
					data.getNode().setOnMouseExited(event -> {
						tooltip.hide();
					});
					
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
			series.getData().add(data);			
			Tooltip tooltip = new Tooltip("Date: " + Tools.sdfDate.format(prevDate) + "\nAmount: " + counter);								
			data.getNode().setOnMouseEntered(event -> {
				tooltip.show(this, event.getScreenX(), event.getScreenY()+20); //+20 to avoid blinking
			});
			
			data.getNode().setOnMouseExited(event -> {
				tooltip.hide();
			});
		}
	}
	
	public ContextMenu getContextMenu()
	{
		return cMenu;
	}
	
	/*
	private Node getChartArea()
	{
		return lookup(".chart-plot-background");
	}*/
}
