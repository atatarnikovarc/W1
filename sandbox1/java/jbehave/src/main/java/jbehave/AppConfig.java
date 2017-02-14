package jbehave;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 10/19/14
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppConfig {
    private static AppConfig _instance = new AppConfig();
    public static AppConfig getInstance() { return _instance; }
    private Properties props;

    private AppConfig() {
        this.props = new Properties();
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("framework.properties");
            this.props.load(inputStream);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public String getProp1() {
        return this.props.getProperty("prop1");
    }
}
