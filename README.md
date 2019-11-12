# dataUp - EV-Visualizer

EV-Visualizer is a Java desktop application that supports users to see the collected data from charging-stations in a visual way, so that they are able to detect errors and also to see how different stations relate to each other.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for testing purposes.

### Prerequisites

Please ensure that you have at least a [Java Runtime Environment (JRE)](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) in version 1.8 installed.<br>

To check what Java version you have installed, run the following command:
```
java -version
```


## Running the program

### Note

Because data is fetched from a *.csv* file and not from a database, the whole data needs to be loaded at program start to avoid delays during runtime. In addition, the program has been created on a 6-year old laptop without GPU. It takes about 1min at startup.<br>
Depending on the hardware, startup time can decrease significant.

### Run

By double-clicking the *ev-visualizer.jar* file the program automatically loads the provided data-sheet and starts with preparing the charts. After the preloader is finished, the Dashboard pane is visible.<br>
The whole program is designed in a tab-layout, similar to a browser.


## Program description

#### Dashboard

The Dashboard is the main entry point of the application. It behaves like a cockpit to see relevant and actual data trends immediately.<br>
Default date range is 2 weeks - this means, that the data from the last 2 weeks is shown in the charts. (*actual date is faked as 9/1/2019*)<br>
This is also indicated through the slider in the upper left corner. It's possible to view the data up to the last 6 weeks. The slider is only enabled in the Dashboard-tab.<br>
On every chart it's possible to make a right-click to open a context-menu. Depending on the chart, the possible functions differ.<br>
![Dashboard](/img/dashboard.png "Dashboard")

#### Full-Screen

Each chart can be viewed in Full-Screen. Watching a chart in Full-Screen also means, that the whole data set is visible. There is no date-range applied.<br>
By making a right-click on a chart and a click on *Show in Full-Screen*, another tab opens where the chart is displayed. Now it's possible to switch between tabs and compare the values.<br>
![Full-Screen](/img/full_screen.png "Full-Screen")

#### Zoom

Sometimes it's needed to get a more detailed view of the data, for example to look at the data of a single day.<br>
Therefor a zooming function is implemented on the *Station Overview* and *Station X - Daily Usage* charts. It works by pressing the left button and drag the mouse to another position. After the mouse-button is released, the axis are changing and the selected data is centered in the chart. To reset the zoom view, either make a double-click on the chart or via the context-menu (right-click).<br>
Zooming is possible in Dashboard and Full-Screen mode.<br>
![Zoom](/img/zoom.png "Zoom")

#### Additional info
Datapoints contain a Tooltip when the mouser moves over them. This is implemented to get some basic information about the charge cycles.<br>
![Tooltip](/img/zoom.png "Tooltip")


## Built With

* JavaFX

## Author

* [**Christian Mooslechner**](mailto:christian.mooslechner@outlook.com)