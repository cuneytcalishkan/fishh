/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author CUNEYT
 */
public class Configure {

    private Properties prop;

    /**
     * The configuration  class to read the configurations from the properties file.
     * @throws IOException
     */
    public Configure() throws IOException {
        prop = new Properties();
        prop.load(this.getClass().getClassLoader().getResourceAsStream("util/client.properties"));
    }

    /**
     * Gets the specified property value associated with the given key or null
     * if the key does not exist.
     * @param key the keyword of the property
     * @return the value of the specified key
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}
