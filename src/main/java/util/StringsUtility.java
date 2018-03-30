package util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringsUtility {

	private static final Logger logger = LoggerFactory.getLogger(StringsUtility.class);

	public static String deencode(String string) {
		//Escape special encoding (I.E. ; è being shown as &egrave;)
		return StringEscapeUtils.unescapeHtml4(string);
	}

	public static String inbetween(String inputLine, String start, int removeTrailingChars) {
		if (!inputLine.contains(start)) {
			logger.error("Error : line '" + inputLine + "' doesn't contain string : " + start);
			return "null";
		}

		return deencode(inputLine.substring(inputLine.indexOf(start) + start.length()));
	}

	public static String inbetween(String inputLine, String start, String end) {

		if (!inputLine.contains(start)) {
			logger.error("Error : line '" + inputLine + "' doesn't contain string : " + start);
			return "null";
		} else if (!inputLine.contains(end)) {
			logger.error("Error : line '" + inputLine + "' doesn't contain string : " + end);
			return "null";
		} else {
			return deencode(inputLine.substring(inputLine.indexOf(start) + start.length(), inputLine.indexOf(end)));
		}
	}
}
