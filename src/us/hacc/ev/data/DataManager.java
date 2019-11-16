package us.hacc.ev.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;
import us.hacc.ev.application.ChargeCycle;
import us.hacc.ev.application.ElectricVehicle;
import us.hacc.ev.application.TimeDiff;
import us.hacc.ev.application.charts.AdvancedScatterChart;
import us.hacc.ev.application.charts.AdvancedStackedBarChart;
import us.hacc.ev.application.charts.StationLineChart;
import us.hacc.ev.util.Tools;

public class DataManager extends HashMap<String, ArrayList<ChargeCycle>>
{
	private static final long serialVersionUID = 1L;
	private String file;
	public static StationLineChart stationLineChart, stationLineChartFullScreen;
	public static AdvancedScatterChart stationTimeChartA, stationTimeChartAFullScreen, stationTimeChartB, stationTimeChartBFullScreen;
	public static AdvancedStackedBarChart stationBarChart, stationBarChartFullScreen;
	private Date today, weeks2;
	private Calendar cal;
	
	public DataManager(String file)
	{
		this.file = file;
		importData();
		sortData();
		//checkGaps();
		
		cal = Calendar.getInstance();
		cal.set(2019, 8, 1, 0, 0, 0);
		today = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, -14);
		weeks2 = cal.getTime();
		cal.set(2019, 8, 1, 0, 0, 0);
	}

	public void createCharts()
	{
		ChartDataManager cdm = new ChartDataManager();
		//stationLineChart = new StationLineChart(createXAxis(true), new NumberAxis(0, 50, 5), true);
		ElectricVehicle.progress.set(2);
		stationBarChart = new AdvancedStackedBarChart(new CategoryAxis(), new NumberAxis(), false);
		ElectricVehicle.progress.set(3);
		stationTimeChartA = new AdvancedScatterChart(createXAxis(true), new NumberAxis(0, 24, 1), "Station A - Daily Usage", "A");
		ElectricVehicle.progress.set(4);
		stationTimeChartB = new AdvancedScatterChart(createXAxis(true), new NumberAxis(0, 24, 1), "Station B - Daily Usage", "B");
		ElectricVehicle.progress.set(5);
		
		//stationLineChartFullScreen = new StationLineChart(createXAxis(false), new NumberAxis(0, 50, 5), true);	//TODO maybe in new thread?
		ElectricVehicle.progress.set(6);
		stationBarChartFullScreen = new AdvancedStackedBarChart(new CategoryAxis(), new NumberAxis(), true); //TODO change back to true
		ElectricVehicle.progress.set(7);
		stationTimeChartAFullScreen = new AdvancedScatterChart(createXAxis(false), new NumberAxis(0, 24, 1), "Station A - Daily Usage", "A");
		ElectricVehicle.progress.set(8);
		stationTimeChartBFullScreen = new AdvancedScatterChart(createXAxis(false), new NumberAxis(0, 24, 1), "Station B - Daily Usage", "B");
		ElectricVehicle.progress.set(9);
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
	
	private void importData()
	{
		//int counter = 0;
		
		try
		{
			InputStream in = ElectricVehicle.class.getResourceAsStream(file); //TODO deleted 3837872 & 3837874
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			String row;
			String dataItems[];
			ChargeCycle cc;
			while( (row = reader.readLine()) != null )
			{
				dataItems = row.split(",");
				if (dataItems[0].trim().equals("Charge Station Name"))
				{
					System.out.println("header line");
					continue; //Header line
				}
				
				cc = new ChargeCycle(
						dataItems[0].trim(),								// charge station
						dataItems[1].trim(),								// session initiated by
						dataItems[2].trim(),								// start time
						dataItems[3].trim(),								// end time
						dataItems[5].trim(),								// energy
						dataItems[6].trim().replace('$', ' ').trim(),		// session cost
						dataItems[7].trim(),								// session id
						dataItems[8].trim(),								// port type
						dataItems[9].trim()									// payment method
					);
				
				if (!containsKey(cc.getStation()))			// check if station already in list
				{
					put(cc.getStation(), new ArrayList<ChargeCycle>()); // if not, create new arraylist
				}
				
				//System.out.println("import datarow " + ++counter);
				
				boolean duplicate = false;				
				for (ChargeCycle elem : get(cc.getStation()))
				{
					if (elem.getSessionId() == cc.getSessionId())
					{
						System.out.println("duplicate: " + elem.getSessionId());
						duplicate = true;
						break;
					}
				}				
				
				if (!duplicate)
				{
					get(cc.getStation()).add(cc);
				}
			}
			reader.close();			
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}				
	}

	private void sortData()
	{
		for (Entry<String, ArrayList<ChargeCycle>> eSet : entrySet())
		{
			eSet.getValue().sort(new Comparator<ChargeCycle>()
			{
				@Override
				public int compare(ChargeCycle o1, ChargeCycle o2) // o2 is first, o1 is second
				{
					if (o2.getStartTime().getTime() - o1.getStartTime().getTime() == 0)
					{
						System.out.println("same start time: " + o2.getSessionId() + " & " + o1.getSessionId());
					}
					else if (o2.getStartTime().getTime() - o1.getStartTime().getTime() > 0) // time not right, switch
					{
						return -1;
					}
					return 1;
				}
			});			
		}	
	}
	
	private void checkGaps()
	{
		TimeDiff gap;
		
		for (Entry<String, ArrayList<ChargeCycle>> eSet : entrySet())
		{
			ChargeCycle ccPrev = null;
			for (ChargeCycle cc : eSet.getValue())
			{
				if (ccPrev != null)
				{
					gap = new TimeDiff(cc.getStartTime().getTime() - ccPrev.getEndTime().getTime());
					if (gap.getMin() < 0)
					{
						System.out.println("negative gap " + ccPrev.getSessionId() + " & " + cc.getSessionId());
					}
					//System.out.println("Station: " + cc.getStation() + ", time not used: " + gap.toString());				
				}			
				ccPrev = cc;			
			}		
		}
	}
}
