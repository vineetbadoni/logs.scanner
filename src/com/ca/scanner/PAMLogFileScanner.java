package com.ca.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ca.logger.BaseLogger;
import com.ca.logs.profiler.util.PropertiesUtil;
import com.ca.render.ChartRenderer;

public class PAMLogFileScanner {
	
//	PAM log4j pattern : 2015-11-06 13:08:28,093 INFO  [com.optinuity.c2o.workflowengine.WorkflowManager] [ Session Task-1] Manager for flow: 31 unlocked - java.util.concurrent.locks.ReentrantLock@7c6718a[Unlocked]
//	private static final String PAM_LOG4J_REGEX = "(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2},\\d{3}) (.*) \\[(.*)\\] \\[(.*)\\] (.*)$";
	
//	private static final String PAM_LOG_REGEX = "handleResponse: Time taken in processing = (\\d+)ms.";
	
//	private static final String PAM_LOG_REGEX = "Transaction committed...Time taken: (\\d+)";
	
//	private static final String PAM_LOG_REGEX = "(.*)Time taken in processing = (\\d+)ms.";
	
	private static final String PAM_LOG4J_REGEX = PropertiesUtil.getProperty("REGEX_LOG4J_FORMAT");
	
	private static final String PAM_LOG_REGEX = PropertiesUtil.getProperty("REGEX_LOG_ENTRY_FORMAT");
	
	private static BaseLogger logger = BaseLogger.getInstance(PAMLogFileScanner.class);
	
	private LinkedList<LogEvent> events = new LinkedList<LogEvent>();
 
	public static void main(String[] args) throws IOException {
		for(int i=1;;i++){
			try {
				String dirPath = PropertiesUtil.getProperty("NODE_BASE_PATH."+ i);
				graphifyLogs(dirPath, "PAM Performance Node" + i);
			} catch (Exception ignore) {
				break;
			}
		}
	}

	private static void graphifyLogs(final String basePath,final String title) throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				File inputFile = new File(basePath);
				String[] files ;
				if(inputFile.isDirectory()){
					files = inputFile.list(new FilenameFilter() {
						
						@Override
						public boolean accept(File dir, String name) {
							return name.contains("c2o.log") ;
						}
					});
				}else{
					files = new String[]{basePath};
				}

				PAMLogFileScanner scanner = new PAMLogFileScanner();
				
				for(String fileName:files){
//					Path path = Paths.get(basePath+File.separator+fileName);
					logger.info("Processing ("+basePath+File.separator+fileName+").....");
					boolean success = scanner.scanWithPattern(basePath+File.separator+fileName);
					if(!success){
						logger.error("Exiting. Scanner failed to lookup for the pattern, check previous errors");
						return;
					}
				}
				
				logger.info("Log files processing is complete.");
				
				logger.info("Starting post processing");
				scanner.doPostProcessing();
				logger.info("post processing complete");
				
				scanner.launchGraph(title);
				
				scanner.showAverage(title);
			}
		},title).start();
		
	}

	protected void showAverage(String title) {
		long total = 0;
		for (LogEvent event:events){
			total+=Long.parseLong(event.getExtractedInfo());
		}
		logger.info("Average ("+title+")"+total/events.size());
	}

	private void launchGraph(String title) {
		ChartRenderer render = new ChartRenderer(events,title);
		render.launchGraph();
	}

	private void doPostProcessing() {

		logger.info("Sorting the events based on the time these occurred");
		// Process the event sets for something useful like sort it by time etc.
		Collections.sort(events, new EventsComparator());

		logger.info("Total number of events " + events.size());

		for (LogEvent event : events) {
			printEvent(event);
		}

	}

	private boolean scanWithPattern(String path) {
		BufferedReader br = null;
		// Open the file
		try {
			FileInputStream fstream = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(fstream));

			String lineText;
			Pattern p = Pattern.compile(PAM_LOG4J_REGEX);

			// Read File Line By Line
			while ((lineText = br.readLine()) != null) {
				
				lineText = lineText.replaceAll(" \\[ITPAM Core Performance\\] ", " ");

				Matcher m = p.matcher(lineText);

				if (m.matches()) {

					String date = m.group(1);
					String time = m.group(2);
					String logLevel = m.group(3);
					String className = m.group(4);
					String threadName = m.group(5);
					String logText = m.group(6);
					
					LogEvent event = new LogEvent(date, time, threadName,className, logLevel, logText);

					event.extractInfo(PAM_LOG_REGEX);

					if (event.getExtractedInfo() != null) {
						/*logger.info("=================================================================");
						logger.info(event.toString());
						logger.info(event.getExtractedInfo());
						logger.info("=================================================================");*/
						
						//add it to the eventSet
						events.add(event);
					}
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("Error occured while scanning the pattern in the logs",e);
			return false;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ignore) {}
			}
		}
	}
	
	private void printEvent(LogEvent event){
		logger.debug("=================================================================");
		logger.debug(event.toString());
		logger.debug(event.getExtractedInfo());
		logger.debug("=================================================================");
	}
	
	private void AverageEvent(LogEvent event){
		logger.info("=================================================================");
		logger.info(event.toString());
		logger.info(event.getExtractedInfo());
		logger.info("=================================================================");
	}
	
	class EventsComparator implements Comparator<LogEvent>{

		@Override
		public int compare(LogEvent event1, LogEvent event2) {
			return event1.getFullDate().compareTo(event2.getFullDate());
		}
		
	}
	
	public class LogEvent{
		private String date ;
		private String time ;
		private String threadName ;
		private String className;
		private String category ;
		private String message ;
		
		private Date fullDate = null;
		
		private String extractedInfo ;

		public LogEvent(String date,String time,String threadName,String className,String category,String message){
			this.date = date;
			this.time = time;
			this.threadName = threadName;
			this.className=className;
			this.category=category;
			this.message=message;
		}
		
		public String getExtractedInfo() {
			return extractedInfo;
		}
		
		public void setExtractedInfo(String extractedInfo) {
			this.extractedInfo = extractedInfo;
		}
		
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getThreadName() {
			return threadName;
		}
		public void setThreadName(String threadName) {
			this.threadName = threadName;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
		public Date getFullDate() {
			return fullDate;
		}

		public void setFullDate(Date fullDate) {
			this.fullDate = fullDate;
		}
		
		@Override
		public String toString() {
			return "date: "+date + "\ntime: " + time + "\nthreadName: " + threadName + "\nclassName: " + className +"\ncategory: " + category +"\nmessage: " + message;
		}
		
		public void extractInfo(String pattern){
			
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(message);
			
			if(m.matches()){
				setExtractedInfo(m.group(Integer.parseInt(PropertiesUtil.getProperty("PAM_WATCH_ENTRY_IDX","1"))));
				//set the time stamp from the date and time field.
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
				try {
					setFullDate(formatter.parse(date+" "+time));
				} catch (ParseException e) {
					logger.error("failed to set the time stamp",e);
				}
			}
			
		}

	}

}