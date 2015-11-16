package com.ca.logger;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.xml.DOMConfigurator;

public final class BaseLogger implements RepositorySelector {
	
	static{
		DOMConfigurator.configure("resources/log4j.xml");
	}

    /** Logger for logging purpose. */
    private Logger baseLogger;


	/** 
	 * Repository Selector that is used for Application specific
	 * LoggerRepository.
	 */
	 private static LoggerRepository defaultRepository;

	 private static Map<ClassLoader,Hierarchy> repositories = new HashMap<ClassLoader,Hierarchy>();
	 private static ClassLoader loader = null;
	  
	 /**
     * This is to read properties from the property file and to instantiate
     * Logger.
     */
    private BaseLogger() {
    	loader = getClass().getClassLoader();
    }

    /**
     * This method returns instance of BaseLogger class. It creates new instance
     * if no instance is instantiate.
     * @param className - The class name for which logger is to be configured.
     * @return instance
     */
    public static BaseLogger getInstance(Class className) {
		
		/*try {
			
			if (instance == null) {
			    instance = new BaseLogger();
				defaultRepository = LogManager.getLoggerRepository();
			    //RepositorySelector theSelector = instance;
			    LogManager.setRepositorySelector(instance, guard);
				
				Hierarchy hierarchy = new Hierarchy(new RootCategory(Level.DEBUG));
				//Now configure the damm logger
				Properties log4jProps = new Properties();
				log4jProps.load(loader.getResourceAsStream(PropertyReader.getInstance().getProperty("logger")));
				//LogManager.resetConfiguration();
				new PropertyConfigurator().doConfigure(log4jProps,hierarchy);
				
				//BasicConfigurator.configure();
				// Put this thing for everybody to use.
				repositories.put(loader, hierarchy);  
			}
			
		} catch (IllegalArgumentException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}*/
    	BaseLogger tempLogger = new BaseLogger();
    	tempLogger.baseLogger = Logger.getLogger(className);
		return  tempLogger;
    }
    
    /**
     * This method returns instance of BaseLogger class. It creates new instance
     * if no instance is instantiate. This is configured with BaseLogger class.
     * 
     * @return instance
     */
    public static BaseLogger getInstance() {
    	return getInstance(BaseLogger.class);
    }

    /**
     * Log a message with the DEBUG level.
     * 
     * @param message the message to log.
     */
    public void debug(String message) {
    	if(baseLogger.isDebugEnabled())
    		baseLogger.debug(message);
    }

    /**
     * Log a message for performance information.
     * The custom log level is very slow :(
     * Reverting to standard log4j log level.
     * 
     * @param message the message to log.
     */
    public void itpamperf(String message) {
    	debug("[ITPAM Core Performance] " + message);
    }

    /**
     * Log a message for connector performance information.
     * The custom log level is very slow :(
     * Reverting to standard log4j log level.
     * 
     * @param message the message to log.
     */
    public void connperf(String message) {
    	debug("[ITPAM Connector Performance] " + message);
    }

    /**
     * Log a message for function trace information.
     * The custom log level is very slow :(
     * Reverting to standard log4j log level.
     * 
     * @param message the message to log.
     */
    public void ftrace(String message) {
    	if(baseLogger.isDebugEnabled())
    		debug("[ITPAM Function Trace] " + message);
    }

    /**
     * Log a message with the DEBUG level including the stack trace of the
     * Throwablet passed as parameter.
     * 
     * @param message the message to log.
     * @param t the exception to log, including its stack trace.
     */
    public void debug(String message, Throwable t) {
    	if(baseLogger.isDebugEnabled())
    		baseLogger.debug(message, t);
    }

    /**
     * Log a message with the ERROR level.
     * 
     * @param message the message to log.
     */
    public void error(String message) {
        baseLogger.error(message);
    }

    /**
     * Log a message with the EROOR level including the stack trace of the
     * Throwablet passed as parameter.
     * 
     * @param message the message to log.
     * @param t the exception to log, including its stack trace.
     */
    public void error(String message, Throwable t) {
        baseLogger.error(message, t);
    }

    /**
     * Log a message with the FATAL level.
     * 
     * @param message the message to log.
     */
    public void fatal(String message) {
        baseLogger.fatal(message);
    }

    /**
     * Log a message with the FATAL level including the stack trace of the
     * Throwablet passed as parameter.
     * 
     * @param message the message to log.
     * @param t the exception to log, including its stack trace.
     */
    public void fatal(String message, Throwable t) {
        baseLogger.fatal(message, t);
    }

    /**
     * Log a message with the WARN level.
     * 
     * @param message the message to log.
     */
    public void warn(String message) {
        baseLogger.warn(message);
    }

    /**
     * Log a message with the WARN level including the stack trace of the
     * Throwablet passed as parameter.
     * 
     * @param message the message to log.
     * @param t the exception to log, including its stack trace.
     */
    public void warn(String message, Throwable t) {
        baseLogger.warn(message, t);
    }

    /**
     * Log a message with the INFO level.
     * 
     * @param message the message to log.
     */
    public void info(String message) {
        if(baseLogger.isInfoEnabled())
        	baseLogger.info(message);
    }

    /**
     * Log a message with the INFO level including the stack trace of the
     * Throwablet passed as parameter.
     * 
     * @param message the message to log.
     * @param t the exception to log, including its stack trace.
     */
    public void info(String message, Throwable t) {
    	if(baseLogger.isInfoEnabled())
    		baseLogger.info(message, t);
    }

    /**
     * Log a message with the TRACE level.
     * 
     * @param message the message to log.
     */
    public void trace(String message) {
    	if(baseLogger.isDebugEnabled())
    		baseLogger.debug(message);
    }

    /**
     * Log a message with the TRACE level including the stack trace of the
     * Throwablet passed as parameter.
     * 
     * @param message the message to log.
     * @param t the exception to log, including its stack trace.
     */
    public void trace(String message, Throwable t) {
    	if(baseLogger.isDebugEnabled())
    		baseLogger.debug(message, t);
    }

    public boolean isDebugEnabled() {
    	return baseLogger.isDebugEnabled();
    }
    
    public boolean isInfoEnabled() {
    	return baseLogger.isInfoEnabled();
    }
    
    public boolean isTraceEnabled() {
    	return baseLogger.isDebugEnabled();
    }
    
    public LoggerRepository getLoggerRepository() {
		ClassLoader loader = getClass().getClassLoader();
	      LoggerRepository repository = (LoggerRepository)repositories.get(loader);
	      
	      if (repository == null) {
	          return defaultRepository;
	      } else {
	          return repository;
	      }
	}
}
