package us.hacc.ev.application.charts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import us.hacc.ev.util.Tools;

public class StationLineChart extends LineChart<Number, Number>
{
	private Rectangle rect;
	private ContextMenu cMenu;
	
	public StationLineChart(NumberAxis xAxis, NumberAxis yAxis, boolean loadData)
	{
		super(xAxis, yAxis);
		setTitle("Station Overview");
		setCursor(Cursor.CROSSHAIR);
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
		
		/*
		if (loadData)
		{
			loadData();
		}*/
		
		getChildren().add(rect);
	}
	
	/*
	public void loadData(ObservableList<Data<Number, Number>> lineChartData)
	{
		CheckMenuItem cMenuItem;		
		Series<Number, Number> actualSeries = null;
		boolean seriesFound;
		for ( Data<Number, Number> data : lineChartData )
		{
			seriesFound = false;
			for (Series<Number, Number> s : getData())
			{
				if (s.getName().endsWith(data.getExtraValue().toString()))
				{
					actualSeries = s;
					seriesFound = true;
				}
			}
			
			if (!seriesFound)
			{
				actualSeries = new Series<Number, Number>();
				actualSeries.setName("Station " + data.getExtraValue().toString());
				getData().add(actualSeries);
				
				cMenuItem = new CheckMenuItem(actualSeries.getName());
				cMenuItem.setSelected(true);
				cMenuItem.setOnAction(event -> {
					if ( !((CheckMenuItem)event.getSource()).isSelected() )
					{
						for (Series<Number, Number> s : getData())
						{
							if (s.getName().equals(((CheckMenuItem)event.getSource()).getText()))
							{
								getData().remove(s);
							}
						}
					}
					else
					{
						for (Series<Number, Number> s : getData())
						{
							if (s.getName().equals(((CheckMenuItem)event.getSource()).getText()))
							{
								getData().add(s);
							}
						}
					}
				});
				cMenu.getItems().add(cMenuItem);
				
				cMenuItem = new CheckMenuItem("TestData");
				cMenuItem.setOnAction(event -> {
					if ( !((CheckMenuItem)event.getSource()).isSelected() )
					{
						ChartDataManager.lineChartData.clear();
						if (ChartDataManager.lineChartData.isEmpty())
						{
							System.out.println("is empty");
						}
					}
					else
					{
						ChartDataManager.lineChartData.clear();
						if (ChartDataManager.lineChartData.isEmpty())
						{
							System.out.println("is empty");
						}
					}
				});
				cMenu.getItems().add(cMenuItem);

				
			}
			//actualSeries.getData().add(data);
			
			
			Data<Number, Number> d = new Data<Number, Number>(data.getXValue(), data.getYValue(), data.getExtraValue());
			d.XValueProperty().bind(data.XValueProperty());
			d.YValueProperty().bind(data.YValueProperty());
			d.extraValueProperty().bind(data.extraValueProperty());
			
			
			actualSeries.getData().add(d);
			
			data.XValueProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{
					System.out.println("data " + newValue);					
				}
			});
			
			d.XValueProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
				{
					System.out.println("d " + newValue);					
				}
			});
			
			
			Tooltip tooltip = new Tooltip("Date: " + Tools.sdfDate.format(d.getXValue()) + "\nAmount: " + d.getYValue());								
			d.getNode().setOnMouseEntered(event -> {
				tooltip.show(this, event.getScreenX(), event.getScreenY()+20); //+20 to avoid blinking
			});
			
			d.getNode().setOnMouseExited(event -> {
				tooltip.hide();
			});
		}
	}*/
	
	public void addData(ObservableList<Series<Number, Number>> olSeries)
	{
		ObservableList<Series<Number, Number>> olNew = FXCollections.observableArrayList();
		
		for (Series<Number, Number> s : olSeries)
		{
			Series<Number, Number> sNew = new Series<Number, Number>();
			//sNew.dataProperty().bind(s.dataProperty());
			sNew.setName(s.getName());
			olNew.add(sNew);
			
			for (Data<Number, Number> data : s.getData()) 
			{
				Data<Number, Number> dNew = new Data<Number, Number>(data.getXValue(), data.getYValue(), data.getExtraValue());
				dNew.XValueProperty().bind(data.XValueProperty());
				dNew.YValueProperty().bind(data.YValueProperty());
				dNew.extraValueProperty().bind(data.extraValueProperty());
				sNew.getData().add(dNew);
			}
		}
		
		olSeries.addListener(new ListChangeListener<Series<Number, Number>>() {

			@Override
			public void onChanged(Change<? extends Series<Number, Number>> c)
			{
				while(c.next())
				{
					if (c.wasAdded())
					{
						for (Series<Number, Number> s : c.getAddedSubList())
						{
							Series<Number, Number> sNew = new Series<Number, Number>();
							//sNew.dataProperty().bind(s.dataProperty());
							sNew.setName(s.getName());
							olNew.add(sNew);
							
							for (Data<Number, Number> data : s.getData()) 
							{
								Data<Number, Number> dNew = new Data<Number, Number>(data.getXValue(), data.getYValue(), data.getExtraValue());
								dNew.XValueProperty().bind(data.XValueProperty());
								dNew.YValueProperty().bind(data.YValueProperty());
								dNew.extraValueProperty().bind(data.extraValueProperty());
								sNew.getData().add(dNew);
							}
						}
					}
					else if (c.wasRemoved())
					{
						ArrayList<Series<Number, Number>> sRemove = new ArrayList<Series<Number, Number>>();
						
						for (Series<Number, Number> series : c.getRemoved())
						{	
							for (Series<Number, Number> s : olNew)
							{
								if (s.getName().equals(series.getName()))
								{
									//getData().remove(s); //TODO check if also removes in ol
									sRemove.add(s);
								}
							}
						}
						
						for (Series<Number, Number> s : sRemove)
						{
							olNew.remove(s);
						}
					}
				}
			}
		});
		setData(olNew);
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
