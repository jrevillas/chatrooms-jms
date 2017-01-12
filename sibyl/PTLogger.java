package sibyl;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class PTLogger extends Filter<ILoggingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PTLogger.class);
    private static final Marker MARKER = MarkerFactory.getMarker("jms-logger");

    private static final String DEBUG = "\u001B[36m[DEBUG] \u001B[0m%s \u001B[36m%s\u001B[0m";
    private static final String ERROR = "\u001B[31m[ERROR]  \u001B[0m%s \u001B[31m%s\u001B[0m";
    private static final String INFO = "\u001B[32m[INFO]  \u001B[0m%s \u001B[32m%s\u001B[0m";
    private static final String JMS = "\u001B[35m[JMS]   \u001B[0m%s \u001B[35m%s\u001B[0m";
    private static final String TEST = "\u001B[32m[TEST]   \u001B[0m%s \u001B[32m%s\u001B[0m";
    private static final String WARN = "\u001B[33m[WARN]   \u001B[0m%s \u001B[33m%s\u001B[0m";
    private static final String SLASH = "\u001B[34m[SLASH] \u001B[0m%s \u001B[34m%s\u001B[0m";

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMarker().equals(MARKER)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }

    public static void debug(String source, String logLine) {
        LOGGER.info(MARKER, String.format(DEBUG, source, logLine));
    }

    public static void error(String source, String logLine) {
        LOGGER.info(MARKER, String.format(ERROR, source, logLine));
    }

    public static void info(String source, String logLine) {
        LOGGER.info(MARKER, String.format(INFO, source, logLine));
    }

    public static void jms(String source, String logLine) {
        LOGGER.info(MARKER, String.format(JMS, source, logLine));
    }

    public static void test(String source, String logLine) {
        LOGGER.info(MARKER, String.format(TEST, source, logLine));
    }

    public static void warn(String source, String logLine) {
        LOGGER.info(MARKER, String.format(WARN, source, logLine));
    }

    public static void slash (String source, String logLine) {
        LOGGER.info(MARKER, String.format(SLASH, source, logLine));
    }

}