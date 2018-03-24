package utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.io.File;


public class Utils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class.getName());
	
	public static Properties fetchProperties(){
		Properties properties = new Properties();
		try {
			File file = ResourceUtils.getFile("classpath:application.properties");
            InputStream in = new FileInputStream(file);
			properties.load(in);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return properties;
	}
}
