package cc.pp.sql;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParamsDB {

	private final InputStream inputStream = getClass().getClassLoader()
			.getResourceAsStream("config.properties");
	Properties p = new Properties();

	public String getProps(String prop) {
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p.getProperty(prop);
	}

}
