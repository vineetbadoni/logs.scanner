package com.ca.render;

import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.ca.logs.profiler.util.PropertiesUtil;
import com.ca.scanner.PAMLogFileScanner.LogEvent;

public class ChartRenderer {

	private LinkedList<LogEvent> events = null;
	
	private String title ;
	
//	private static int NoOfMins = 10;
	
	//-1 means all entries
	private static int NoOfMins = -1;

	public ChartRenderer(LinkedList<LogEvent> events,String title) {
		this.events = events;
		this.title = title;
	}

	private XYDataset createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries(title);
		// XYSeries series2 = new XYSeries("Object 2");
		// XYSeries series3 = new XYSeries("Object 3");

		/*
		 * series1.add(1.0, 2.0); series1.add(2.0, 3.0); series1.add(3.0, 2.5);
		 * series1.add(3.5, 2.8); series1.add(4.2, 6.0);
		 */

		/*
		 * series2.add(2.0, 1.0); series2.add(2.5, 2.4); series2.add(3.2, 1.2);
		 * series2.add(3.9, 2.8); series2.add(4.6, 3.0);
		 * 
		 * series3.add(1.2, 4.0); series3.add(2.5, 4.4); series3.add(3.8, 4.2);
		 * series3.add(4.3, 3.8); series3.add(4.5, 4.0);
		 */

		/*
		 * dataset.addSeries(series2); dataset.addSeries(series3);
		 */
		
		LogEvent initialTime = events.get(0);

		for (LogEvent event : events) {
			long extractedInfo = Long.parseLong(event.getExtractedInfo());
			
			long timeDiff  = (event.getFullDate().getTime()-initialTime.getFullDate().getTime());
			
			if(NoOfMins > 0 && timeDiff > NoOfMins*60*1000){
				break;
			}
			
//			series.add(timeDiff, extractedInfo);
			
			series.add(event.getFullDate().getTime(), extractedInfo);
		}
		dataset.addSeries(series);

		return dataset;
	}
	
	private JPanel createChartPanel() {
	    String chartTitle = title;
	    String xAxisLabel = PropertiesUtil.getProperty("X_AXIS_TEXT");
	    String yAxisLabel = PropertiesUtil.getProperty("Y_AXIS_TEXT");
	 
	    XYDataset dataset = createDataset();
	    
	    JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, xAxisLabel, yAxisLabel, dataset,true,true,true);
	    
//	    JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset,PlotOrientation.VERTICAL,false,false,false);
	    
	    ((DateAxis)(chart.getXYPlot().getDomainAxis())).setDateFormatOverride(new SimpleDateFormat(PropertiesUtil.getProperty("TIME_FORMAT")));
	    
	    return new ChartPanel(chart);
	}
	
	public void launchGraph(){
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.add(createChartPanel());
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                frame.setVisible(true);
                
            }
        });
	}

}
