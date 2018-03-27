package properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Properties {

    private static PropertyFileManager e = new PropertyFileManager("tvcrawler.properties");

    public static final String TRUE = "TRUE";
    public static final String FALSE = "FALSE";

    //public static Property BUILD_VERSION = new Property("BUILD_VERSION", "X22.1.1", e);

    public static Property generateBasicBooleanProperty(String key) {
        return new Property("\""+key+"\"", "FALSE", e);
    }
}


class PropertyFileManager {

    PropertiesConfiguration config;

    private static final Logger logger = LoggerFactory.getLogger(PropertyFileManager.class);

    public PropertyFileManager(String fileName) {

        try {
            config = new PropertiesConfiguration(fileName);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        logger.info("Restoring properties from " + fileName + "...");

        try {

            if (!new File(fileName).exists()) {
                logger.info("File : " + fileName + " doesn't exist!");
            }

            new File(fileName).createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void savePropertyValue(String key, String value) {

        logger.info("Saving property '" + key + "' with value " + value + ".");

        try {
            config.setProperty(key, value);
            config.save();
        } catch (ConfigurationException e) {
            logger.error("Error while setting property " + key + ".", e);
            e.printStackTrace();
        }
    }

    public String getPropertyValue(String key, String defaultValue) {

        logger.info("Getting property " + key + ".");

        String value = (String) config.getProperty(key);

        if (value == null) { //file might be empty

            logger.info("Saving default value '"+defaultValue+"' for key '" + key + "' due to null value.");

            savePropertyValue(key, defaultValue);
            return defaultValue;
        } else {
            return value;
        }
    }
}
