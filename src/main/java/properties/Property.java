package properties;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Property {

    private static final Logger logger = LoggerFactory.getLogger(Property.class);

    private PropertyFileManager e; //property file manager used to save and get values on updates

    private String key; //name in Properties
    private String value; //current value
    private String defaultValue;

    /**
     * @param keyName      Key to store value to
     * @param defaultValue Default value returned if no property of this key exists
     * @param e            PropertyFileManager used to store and retrieve keys
     */
    public Property(String keyName, String defaultValue, PropertyFileManager e) {
        this.key = keyName;
        this.defaultValue = defaultValue;
        this.value = e.getPropertyValue(keyName, defaultValue);
        this.e = e;
    }

    private String getValue() {
        return value;
    }

    public String setNewValue(String value) {
        e.savePropertyValue(key, value);
        this.value = value;

        return value;
    }

    public int setNewValue(int i) {
        e.savePropertyValue(key, i + "");
        this.value = i + "";

        return i;
    }

    public boolean setNewValue(Boolean value) {
        String booleanStringValue = Boolean.toString(value).toUpperCase();
        e.savePropertyValue(key, booleanStringValue);
        this.value = booleanStringValue;

        return value;
    }

    public boolean getValueAsBoolean() {
        switch (value) {
            case Properties.TRUE:
                return true;
            case Properties.FALSE:
                return false;
            default:
                logger.error("isTrue() on " + key + " for value " + value + " is impossible. (Boolean string values are case sensitive!)");
                return false;
        }
    }

    public int getValueAsInt() {

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            logger.error("Could not get value as int (value : " + value + ", key : " + key + "), attempting with default value", e);
        }

        try {
            return Integer.parseInt(defaultValue);
        } catch (Exception e) {
            //@formatter:off
            logger.error("Could not get default value as int (value : " + defaultValue + ", key : " + key + ")", e);
            //@formatter:on
        }

        return 0;
    }

    public boolean isDefaultValue() {
        return value.equals(defaultValue);
    }

    @Override
    public String toString() {
        return getValue();
    }
}
