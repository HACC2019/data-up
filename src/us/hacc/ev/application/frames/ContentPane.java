package us.hacc.ev.application.frames;

import java.util.Calendar;
import java.util.Date;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import us.hacc.ev.application.charts.AdvancedScatterChart;
import us.hacc.ev.application.charts.AdvancedStackedBarChart;
import us.hacc.ev.application.charts.StationLineChart;
import us.hacc.ev.data.DataManager;
import us.hacc.ev.util.Tools;

public class ContentPane extends BorderPane
{	
	private Date today;
	private StationLineChart stationLineChart, stationLineChartFullScreen;
	private AdvancedScatterChart stationTimeChartA, stationTimeChartAFullScreen, stationTimeChartB, stationTimeChartBFullScreen;
	private AdvancedStackedBarChart stationBarChart, stationBarChartFullScreen;
	private Calendar cal;
	private Slider slider;
	private TabPane tabPane;
	
	public ContentPane()
	{
		setId("content-pane-border-pane");
		cal = Calendar.getInstance();
		cal.set(2019, 8, 1, 0, 0, 0);
		today = cal.getTime();
		setTop(new VBox(createMenu(), createToolbar()));
		createTabPane();
		setCenter(tabPane);
		getStyleClass().add("panel-primary");
	}

	private MenuBar createMenu()
	{
		MenuItem about = new MenuItem("About");
		about.setOnAction(event -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("Questions?");
			alert.setContentText("This application was created by Christian Mooslechner. In case of any questions, feel free to contact me any time!\nchristian.mooslechner@outlook.com");
			alert.showAndWait();
		});
		Menu help = new Menu("Help", null, about);				
		MenuBar mBar = new MenuBar(help);
		mBar.setId("menu-bar");
		return mBar;
	}
	
	private ToolBar createToolbar()
	{
		slider = new Slider(1, 7, 2);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(false);
		slider.setMajorTickUnit(1);
		slider.setMinorTickCount(0);
		slider.setSnapToTicks(true);
		slider.setLabelFormatter(new StringConverter<Double>() 
		{
			public String toString(Double object) 
			{	
				if (object != null && object.intValue() == 7)
				{
					return "ALL";
				}
				return object == null ? "" : String.valueOf(object.intValue());
			}
			
			public Double fromString(String string) 
			{
				if (string.equals("ALL"))
				{
					return 7.0;
				}
				return Double.parseDouble(string);
			}
		});
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				if (newValue != null && (newValue.doubleValue()==1.0 || newValue.doubleValue()==2.0 || 
						newValue.doubleValue()==3.0 || newValue.doubleValue()==4.0 || newValue.doubleValue()==5.0 ||
							newValue.doubleValue()==6.0 || newValue.doubleValue() == 7.0))
				{
					resetZoom(stationLineChart, newValue.intValue());
					resetZoom(stationTimeChartA, newValue.intValue());
					resetZoom(stationTimeChartB, newValue.intValue());
					resetZoom(stationLineChartFullScreen, newValue.intValue());
					resetZoom(stationTimeChartAFullScreen, newValue.intValue());
					resetZoom(stationTimeChartBFullScreen, newValue.intValue());
					
					if (newValue.doubleValue() == 7.0)
					{
						stationBarChart.prepareData(0, true);
						stationBarChartFullScreen.prepareData(0, true);
					}
					else
					{
						stationBarChart.prepareData(newValue.intValue(), true);
						stationBarChartFullScreen.prepareData(newValue.intValue(), true);
					}
				}
			}
		});
		
		Label lblSlider = new Label("Show last <n> weeks:");
		lblSlider.setId("lblSlider");
		
		ToolBar toolbar = new ToolBar();
		toolbar.setId("content-pane-toolbar");
		HBox hbox = new HBox(5, lblSlider, slider);
		hbox.setAlignment(Pos.CENTER);
		hbox.setId("content-pane-toolbar-hbox");
		toolbar.getItems().add(hbox);
		return toolbar;
	}
	
	private void createTabPane()
	{
		Tab dashboard = new Tab("Dashboard");
		dashboard.setContent(createDashboard());
		dashboard.setClosable(false);
				
		tabPane = new TabPane(dashboard);
		tabPane.setSide(Side.BOTTOM);
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		/*
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
		{
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
			{
				if (newValue.getText().equals("Dashboard"))
				{
					slider.setDisable(false);
				}
				else
				{
					slider.setDisable(true);
				}
			}
		});*/
	}
	
	private BorderPane createDashboard()
	{
		stationLineChart = DataManager.stationLineChart;
		stationBarChart = DataManager.stationBarChart;
		stationTimeChartA = DataManager.stationTimeChartA;
		stationTimeChartB = DataManager.stationTimeChartB;
		stationLineChart.getContextMenu().getItems().addAll(new SeparatorMenuItem(), createResetMenuItem(stationLineChart, true));
		stationTimeChartA.getContextMenu().getItems().addAll(createResetMenuItem(stationTimeChartA, true));
        stationTimeChartB.getContextMenu().getItems().addAll(createResetMenuItem(stationTimeChartB, true));		
		stationLineChart.getContextMenu().getItems().addAll(new SeparatorMenuItem(), createFullscreenMenuItem(stationLineChart));
		stationBarChart.getContextMenu().getItems().addAll(createFullscreenMenuItem(stationBarChart));
		stationTimeChartA.getContextMenu().getItems().add(createFullscreenMenuItem(stationTimeChartA));
        stationTimeChartB.getContextMenu().getItems().add(createFullscreenMenuItem(stationTimeChartB));	
        
        stationLineChartFullScreen = DataManager.stationLineChartFullScreen;
        stationBarChartFullScreen = DataManager.stationBarChartFullScreen;
        stationTimeChartAFullScreen = DataManager.stationTimeChartAFullScreen;
        stationTimeChartBFullScreen = DataManager.stationTimeChartBFullScreen;
        stationLineChartFullScreen.getContextMenu().getItems().addAll(new SeparatorMenuItem(), createResetMenuItem(stationLineChartFullScreen, false));
		stationTimeChartAFullScreen.getContextMenu().getItems().addAll(new SeparatorMenuItem(), createResetMenuItem(stationTimeChartAFullScreen, false));
		stationTimeChartBFullScreen.getContextMenu().getItems().addAll(new SeparatorMenuItem(), createResetMenuItem(stationTimeChartBFullScreen, false));
     
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(10));
		gp.setHgap(10);
		gp.setVgap(10);
		gp.add(stationLineChart, 0, 0);
		gp.add(stationTimeChartA, 0, 1);
		gp.add(stationBarChart, 1, 0);
		gp.add(stationTimeChartB, 1, 1);		
		
		GridPane.setHgrow(gp.getChildren().get(0), Priority.ALWAYS);
		GridPane.setHgrow(gp.getChildren().get(1), Priority.ALWAYS);
		GridPane.setHgrow(gp.getChildren().get(2), Priority.ALWAYS);
		GridPane.setHgrow(gp.getChildren().get(3), Priority.ALWAYS);		
		
		GridPane.setVgrow(gp.getChildren().get(0), Priority.ALWAYS);
		GridPane.setVgrow(gp.getChildren().get(1), Priority.ALWAYS);
		GridPane.setVgrow(gp.getChildren().get(2), Priority.ALWAYS);
		GridPane.setVgrow(gp.getChildren().get(3), Priority.ALWAYS);	
		
		BorderPane bp = new BorderPane();
		bp.setCenter(gp);
		
		return bp;
	}
	
	private MenuItem createResetMenuItem(XYChart<?,?> chart, boolean isDashboardChart)
	{
		chart.setOnMouseReleased(event -> {
			if (event.getClickCount() > 1)
			{
				if (isDashboardChart)
				{
					resetZoom(chart, (int) slider.getValue());
				}
				else
				{
					chart.getXAxis().setAutoRanging(true);
					if (chart instanceof AdvancedScatterChart)
					{
						((NumberAxis)chart.getYAxis()).setLowerBound(0);
						((NumberAxis)chart.getYAxis()).setUpperBound(24);
						((NumberAxis)chart.getYAxis()).setTickUnit(1);
					}
				}	
			}
		});
		
		MenuItem mItem = new MenuItem("Reset View");
		mItem.setOnAction(event -> {
			if (isDashboardChart)
			{
				resetZoom(chart, (int) slider.getValue());
			}
			else
			{
				chart.getXAxis().setAutoRanging(true);
				if (chart instanceof AdvancedScatterChart)
				{
					((NumberAxis)chart.getYAxis()).setLowerBound(0);
					((NumberAxis)chart.getYAxis()).setUpperBound(24);
					((NumberAxis)chart.getYAxis()).setTickUnit(1);
				}
			}			
		});
		return mItem;
	}
	
	private MenuItem createFullscreenMenuItem(XYChart<?,?> chart)
	{		
		MenuItem mItem = new MenuItem("Show in Full-Screen");
		mItem.setOnAction(event -> {
			showFullScreen(chart.getTitle());
		});
		return mItem;
	}
	
	private void showFullScreen(String title)
	{
		for (Tab t : tabPane.getTabs())
		{
			if (t.getText().equals(title))
			{
				tabPane.getSelectionModel().select(t);
				return;
			}
		}
		final Tab tab = new Tab(title);
		
		if (title.equals("Station Overview")) //TODO temp !!!!!!!!!
		{
			tab.setContent(new BorderPane(stationLineChartFullScreen));
			resetZoom(stationLineChartFullScreen, (int) slider.getValue());
		}
		else if (title.equals("Daytime Usage"))
		{
			tab.setContent(new BorderPane(stationBarChartFullScreen));
			if ((int)slider.getValue() == 7)
			{
				stationBarChartFullScreen.prepareData(0, true);
			}
			else
			{
				stationBarChartFullScreen.prepareData((int)slider.getValue(), true);
			}
		}
		else if (title.equals("Station A - Daily Usage"))
		{
			tab.setContent(new BorderPane(stationTimeChartAFullScreen));
			resetZoom(stationTimeChartAFullScreen, (int) slider.getValue());
		}
		else if (title.equals("Station B - Daily Usage"))
		{
			tab.setContent(new BorderPane(stationTimeChartBFullScreen));
			resetZoom(stationTimeChartBFullScreen, (int) slider.getValue());
		}
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
	}
	
	private void resetZoom(XYChart<?,?> chart, int value)
	{
		if (value == 7)
		{
			chart.getXAxis().setAutoRanging(true);
		}
		else
		{
			cal.add(Calendar.DAY_OF_YEAR, -(7*value));
			((NumberAxis)chart.getXAxis()).setAutoRanging(false);
			((NumberAxis)chart.getXAxis()).setLowerBound(cal.getTime().getTime());
			((NumberAxis)chart.getXAxis()).setUpperBound(today.getTime());
			((NumberAxis)chart.getXAxis()).setTickUnit(Tools.DATE_DayInMillis);
			cal.add(Calendar.DAY_OF_YEAR, (7*value));
			
			if (chart instanceof AdvancedScatterChart)
			{
				((NumberAxis)chart.getYAxis()).setLowerBound(0);
				((NumberAxis)chart.getYAxis()).setUpperBound(24);
				((NumberAxis)chart.getYAxis()).setTickUnit(1);
			}
		}
	}
}
