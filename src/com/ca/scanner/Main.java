package com.ca.scanner;

import java.util.ArrayList;

import com.ca.logger.BaseLogger;
import com.ca.logs.profiler.util.PropertiesUtil;
import com.ca.render.ChartRenderer;

public class Main {

	private static BaseLogger logger = null;

	private static ChartRenderer chartRender = null;

	private static ArrayList<PAMLogFileScanner> scanners = new ArrayList<PAMLogFileScanner>();

	public static void main(String[] args) throws Exception {
		logger = BaseLogger.getInstance(Main.class);

		ArrayList<Thread> threads = new ArrayList<Thread>();

		for (int i = 1;; i++) {
			try {
				String dirPath = PropertiesUtil.getProperty("NODE_BASE_PATH."+ i);

				scanners.add(new PAMLogFileScanner(dirPath,"PAM Performance Node" + i));

			} catch (Exception ignore) {
				break;
			}
		}

		// Start the Scanners;

		for (PAMLogFileScanner scanner : scanners) {
			Thread t = new Thread(scanner);
			threads.add(t);
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}

		logger.info("Logs scanning is complete, launching the graph.");

		// Launch the GUI.
		launchGraph();
	}

	private static void launchGraph() {
		
		chartRender = new ChartRenderer("Process Automation");

		for (PAMLogFileScanner scanner : scanners) {
			chartRender.addEvents(scanner.getID(), scanner.getEvents());
		}
		
		chartRender.launchGraph();
	}

}
