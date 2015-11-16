This utility watches the Database and collects the Blocked queries and also the Blocking queries.

	Usage: startprofiler.bat profiler.sleep.interval=x
	- where x is the time between two subsequent calls to the Database.

Note: The script can also be started by executing the bat file only. In that case the time interval between two Database subsequent calls will be 2 mins.

In order to change the Database settings. Please do change the  resources/dbConnection.properties in the release folder.