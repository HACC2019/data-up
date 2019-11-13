package us.hacc.ev.application.charts;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import us.hacc.ev.application.ChargeCycle;
import us.hacc.ev.application.ElectricVehicle;
import us.hacc.ev.util.Tools;

public class AdvancedScatterChart extends ScatterChart<Number, Number>
{
	private Rectangle rect;
	private ContextMenu cMenu;
	
	public AdvancedScatterChart(NumberAxis xAxis, NumberAxis yAxis, String title, String station)
	{
		super(xAxis, yAxis);
		setTitle(title);
		HBox hboxLegend = new HBox(20);
		hboxLegend.getStyleClass().add("chart-legend");
		Rectangle rectLegend = new Rectangle(15, 2);
		rectLegend.setFill(Color.BLACK);
		Label lblStart = new Label("Start", new Circle(5, Color.GREEN));
		Label lblEnd = new Label("End", new Circle(5, Color.RED));
		Label lblDuration = new Label("Duration", rectLegend);
		lblStart.getStyleClass().add("chart-legend-item");
		lblEnd.getStyleClass().add("chart-legend-item");
		lblDuration.getStyleClass().add("chart-legend-item");
		hboxLegend.getChildren().add(lblStart);
		hboxLegend.getChildren().add(lblEnd);
		hboxLegend.getChildren().add(lblDuration);
		setLegend(hboxLegend);
		
		yAxis.setLabel("Time");
		yAxis.setMinorTickVisible(false);
		yAxis.lowerBoundProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				if (newValue.intValue() < 0)
				{
					yAxis.setLowerBound(0);
				}
			}
		});
		
		yAxis.upperBoundProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				if (newValue.intValue() > 24)
				{
					yAxis.setUpperBound(24);
				}
			}
		});

		Calendar cal = Calendar.getInstance();
		
		rect = new Rectangle();
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        getChildren().add(rect);
		

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
        			if (event.getClickCount() > 1)
        			{
        				//xAxis.setLowerBound(0);
    					//xAxis.setUpperBound(24);
        				//xAxis.setAutoRanging(true);
    					return;
        			}
        			
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
	                	
	                	if (yPosEnd.doubleValue() > yPosStart.doubleValue()) //draw from up to down
	                	{
	                		yAxis.setLowerBound(yPosStart.doubleValue());
	                		yAxis.setUpperBound(yPosEnd.doubleValue());
	                	}
	                	else
	                	{
	                		yAxis.setLowerBound(yPosEnd.doubleValue());
	                		yAxis.setUpperBound(yPosStart.doubleValue());
	                	}
	                }
        		}
        		
        		if ( event.getEventType() == MouseEvent.MOUSE_DRAGGED )
        		{
        			rect.setVisible(true);
        			Double dx = event.getX() - rectinitX.getValue();
        			Double dy = event.getY() - rectinitY.getValue();
        			
        			if (dx < 0)	//draw from right to left
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
		
		Line line;
		XYChart.Series<Number, Number> series = new Series<Number, Number>();
		getData().add(series);
		int dayStart, dayEnd, counter = 1;
		for ( ChargeCycle cc : ElectricVehicle.getDataManager().get(station) )
		{
			try
			{
				if (counter == 100)
				{
					//break;
				}
				
				XYChart.Data<Number, Number> dataStart = new XYChart.Data<Number, Number>();
				dataStart.setNode(new Circle(5, Color.GREEN));
				cal.setTime(cc.getStartTime());
				dayStart = cal.get(Calendar.DAY_OF_MONTH);
				dataStart.setXValue(Tools.sdfDate.parse(Tools.sdfDate.format(cc.getStartTime())).getTime());
				dataStart.setYValue(cal.get(Calendar.HOUR_OF_DAY) + (((double)cal.get(Calendar.MINUTE))/60));
				series.getData().add(dataStart);
				Tooltip tooltipStart = new Tooltip("START\nSession " + cc.getSessionId() + "\nFrom: " + Tools.sdfDatetime.format(cc.getStartTime()) + "\nTo: " + Tools.sdfDatetime.format(cc.getEndTime()));				
				dataStart.getNode().setOnMouseEntered(event -> {
					tooltipStart.show(this, event.getScreenX(), event.getScreenY()+20); //+20 to avoid blinking
				});				
				dataStart.getNode().setOnMouseExited(event -> {
					tooltipStart.hide();
				});
				
				XYChart.Data<Number, Number> dataEnd = new XYChart.Data<Number, Number>();
				dataEnd.setNode(new Circle(5, Color.RED));
				cal.setTime(cc.getEndTime());
				dayEnd = cal.get(Calendar.DAY_OF_MONTH);
				dataEnd.setXValue(Tools.sdfDate.parse(Tools.sdfDate.format(cc.getEndTime())).getTime());
				dataEnd.setYValue(cal.get(Calendar.HOUR_OF_DAY) + (((double)cal.get(Calendar.MINUTE))/60));
				series.getData().add(dataEnd);
				Tooltip tooltipEnd = new Tooltip("END\nSession " + cc.getSessionId() + "\nFrom: " + Tools.sdfDatetime.format(cc.getStartTime()) + "\nTo: " + Tools.sdfDatetime.format(cc.getEndTime()));
				dataEnd.getNode().setOnMouseEntered(event -> {
					tooltipEnd.show(this, event.getScreenX(), event.getScreenY()+20); //+20 to avoid blinking
				});
				dataEnd.getNode().setOnMouseExited(event -> {
					tooltipEnd.hide();
				});				
				
				if (dayStart != dayEnd) //draw 2 lines
				{	
					line = new Line();			
					line.setStrokeWidth(2.5);
					line.setPickOnBounds(true);
					line.startXProperty().bind(dataStart.getNode().layoutXProperty());
					line.startYProperty().bind(dataStart.getNode().layoutYProperty());
					line.endXProperty().bind(dataStart.getNode().layoutXProperty());
					line.setEndY(0);
					getPlotChildren().add(line);
					
					line = new Line();
					line.setStrokeWidth(2.5);
					line.setPickOnBounds(true);
					line.startXProperty().bind(dataEnd.getNode().layoutXProperty());
					line.startYProperty().bind(yAxis.heightProperty());
					line.endXProperty().bind(dataEnd.getNode().layoutXProperty());
					line.endYProperty().bind(dataEnd.getNode().layoutYProperty());
					getPlotChildren().add(line);
				}
				else
				{
					line = new Line();
					line.setStrokeWidth(2.5);
					line.setPickOnBounds(true);
					line.startXProperty().bind(dataStart.getNode().layoutXProperty());
					line.startYProperty().bind(dataStart.getNode().layoutYProperty());
					line.endXProperty().bind(dataEnd.getNode().layoutXProperty());
					line.endYProperty().bind(dataEnd.getNode().layoutYProperty());
					getPlotChildren().add(line);
				}
				counter++;
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public ContextMenu getContextMenu()
	{
		return cMenu;
	}
}
