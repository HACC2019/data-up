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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import us.hacc.ev.application.charts.AdvancedScatterChart;
import us.hacc.ev.application.charts.AdvancedStackedBarChart;
import us.hacc.ev.application.charts.StationLineChart;
import us.hacc.ev.data.ChartDataManager;
import us.hacc.ev.data.DataManager;
import us.hacc.ev.util.Tools;

public class ContentPane extends BorderPane
{	
	private Date today;
	private StationLineChart stationLineChart, stationLineChartFullScreen;
	private AdvancedScatterChart stationTimeChartA, stationTimeChartAFullScreen, stationTimeChartB, stationTimeChartBFullScreen;
	private AdvancedStackedBarChart stationBarChart, stationBarChartFullScreen;
	private Calendar cal;
	private Slider sliderWeeks, sliderMin;
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
		sliderWeeks = new Slider(1, 7, 2);
		sliderWeeks.setShowTickLabels(true);
		sliderWeeks.setShowTickMarks(false);
		sliderWeeks.setMajorTickUnit(1);
		sliderWeeks.setMinorTickCount(0);
		sliderWeeks.setSnapToTicks(true);
		sliderWeeks.setLabelFormatter(new StringConverter<Double>() 
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
		
		sliderWeeks.valueProperty().addListener(new ChangeListener<Number>() {
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
		
		Label lblSliderWeek = new Label("Show last <n> weeks:");
		lblSliderWeek.setId("lblSlider");
		
		Button btnTest = new Button("Test");
		btnTest.setOnAction(event -> {
			//ChartDataManager.lineChartSeries.clear();
			ChartDataManager.createLineChartData(0.5);
		});
		
		sliderMin = new Slider(0, 120, 0);
		sliderMin.setShowTickLabels(true);
		sliderMin.setShowTickMarks(false);
		sliderMin.setMajorTickUnit(20);
		sliderMin.setMinorTickCount(0);
		sliderMin.setSnapToTicks(true);
		
		TextField tfSliderMin = new TextField();
		
		sliderMin.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				if (newValue != null && (newValue.doubleValue()==0.0 || newValue.doubleValue()==20.0 || 
						newValue.doubleValue()==40.0 || newValue.doubleValue()==60.0 || newValue.doubleValue()==80.0 ||
						newValue.doubleValue()==100.0 || newValue.doubleValue() == 120.0 || 
						(tfSliderMin.getText() != null && Double.parseDouble(tfSliderMin.getText()) == newValue.doubleValue())))
				{
					tfSliderMin.setText(String.valueOf(newValue));
					ChartDataManager.createLineChartData(newValue.doubleValue());
				}
			}
		});
		
		//tfSliderMin.textProperty().bind(sliderMin.valueProperty().asString());		
		Button btnSliderMin = new Button("Set");
		btnSliderMin.setOnAction(event -> {
			try
			{
				double val = Double.parseDouble(tfSliderMin.getText());
				sliderMin.setValue(val);
			}
			catch (Exception e)
			{
				//TODO show error lbl
			}
		});
		tfSliderMin.setOnKeyPressed(event ->
		{
			if (event.getCode() == KeyCode.ENTER)
			{
				btnSliderMin.fire();
			}			
		});
		tfSliderMin.setMaxWidth(60);
		
		Label lblSliderMin = new Label("Min. charging duration (minutes):");
		lblSliderMin.setId("lblSlider");
		
		
		ToolBar toolbar = new ToolBar();
		toolbar.setId("content-pane-toolbar");
		HBox hboxWeek = new HBox(5, lblSliderWeek, sliderWeeks);
		hboxWeek.setAlignment(Pos.CENTER);
		hboxWeek.setId("content-pane-toolbar-hbox");		
		HBox hboxDuration = new HBox(5, lblSliderMin, sliderMin, tfSliderMin, btnSliderMin);
		hboxDuration.setAlignment(Pos.CENTER);
		hboxDuration.setId("content-pane-toolbar-hbox");
		hboxDuration.setPadding(new Insets(0, 0, 0, 20));
		toolbar.getItems().addAll(hboxWeek, hboxDuration);
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
	}
	
	private BorderPane createDashboard()
	{
		//stationLineChart = DataManager.stationLineChart;
		stationLineChart = ChartDataManager.stationLineChart;
		stationBarChart = DataManager.stationBarChart;
		stationTimeChartA = DataManager.stationTimeChartA;
		stationTimeChartB = DataManager.stationTimeChartB;
		stationLineChart.getContextMenu().getItems().addAll(createResetMenuItem(stationLineChart));
		stationTimeChartA.getContextMenu().getItems().addAll(createResetMenuItem(stationTimeChartA));
        stationTimeChartB.getContextMenu().getItems().addAll(createResetMenuItem(stationTimeChartB));		
		stationLineChart.getContextMenu().getItems().addAll(createFullscreenMenuItem(stationLineChart));
		stationBarChart.getContextMenu().getItems().addAll(createFullscreenMenuItem(stationBarChart));
		stationTimeChartA.getContextMenu().getItems().add(createFullscreenMenuItem(stationTimeChartA));
        stationTimeChartB.getContextMenu().getItems().add(createFullscreenMenuItem(stationTimeChartB));	
        
        //stationLineChartFullScreen = DataManager.stationLineChartFullScreen;
        stationLineChartFullScreen = ChartDataManager.stationLineChartFullScreen;
        stationBarChartFullScreen = DataManager.stationBarChartFullScreen;
        stationTimeChartAFullScreen = DataManager.stationTimeChartAFullScreen;
        stationTimeChartBFullScreen = DataManager.stationTimeChartBFullScreen;
        stationLineChartFullScreen.getContextMenu().getItems().addAll(createResetMenuItem(stationLineChartFullScreen));
		stationTimeChartAFullScreen.getContextMenu().getItems().addAll(createResetMenuItem(stationTimeChartAFullScreen));
		stationTimeChartBFullScreen.getContextMenu().getItems().addAll(createResetMenuItem(stationTimeChartBFullScreen));
     
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
	
	private MenuItem createResetMenuItem(XYChart<?,?> chart)
	{
		chart.setOnMouseReleased(event -> {
			if (event.getClickCount() > 1)
			{
				resetZoom(chart, (int) sliderWeeks.getValue());
			}
		});
		
		MenuItem mItem = new MenuItem("Reset View");
		mItem.setOnAction(event -> {
			resetZoom(chart, (int) sliderWeeks.getValue());			
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
			resetZoom(stationLineChartFullScreen, (int) sliderWeeks.getValue());
		}
		else if (title.equals("Daytime Usage"))
		{
			tab.setContent(new BorderPane(stationBarChartFullScreen));
			if ((int)sliderWeeks.getValue() == 7)
			{
				stationBarChartFullScreen.prepareData(0, true);
			}
			else
			{
				stationBarChartFullScreen.prepareData((int)sliderWeeks.getValue(), true);
			}
		}
		else if (title.equals("Station A - Daily Usage"))
		{
			tab.setContent(new BorderPane(stationTimeChartAFullScreen));
			resetZoom(stationTimeChartAFullScreen, (int) sliderWeeks.getValue());
		}
		else if (title.equals("Station B - Daily Usage"))
		{
			tab.setContent(new BorderPane(stationTimeChartBFullScreen));
			resetZoom(stationTimeChartBFullScreen, (int) sliderWeeks.getValue());
		}
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);
	}
	
	private void resetZoom(XYChart<?,?> chart, int value)
	{
		if (chart instanceof AdvancedScatterChart)
		{
			((NumberAxis)chart.getYAxis()).setLowerBound(0);
			((NumberAxis)chart.getYAxis()).setUpperBound(24);
			((NumberAxis)chart.getYAxis()).setTickUnit(1);
		}
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
		}
	}
}
