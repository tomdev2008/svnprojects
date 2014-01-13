package cc.pp.spider.constant;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class WeirenwuConstant {

	private final Properties props;

	public WeirenwuConstant() throws FileNotFoundException, IOException {
		props = new Properties();
		props.load(new FileInputStream("params/weirenwu.properties"));
	}

	public String getUsername() {
		return props.getProperty("username");
	}

	public String getPassword() {
		return props.getProperty("password");
	}

}
