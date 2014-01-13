package cc.pp.spider.common;

import java.io.FileInputStream;
import java.util.Properties;

public class Params {

	public String getCatesProps(String key) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("params/cates.properties"));
		return props.getProperty(key);
	}

	public String getDbProps(String key) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("params/db.properties"));
		return props.getProperty(key);
	}

	public String getUserAndPwProps(String key) throws Exception {
		Properties props = new Properties();
		props.load(new FileInputStream("params/password.properties"));
		return props.getProperty(key);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Params catesParams = new Params();
		//		System.out.println(catesParams.getDbProps("token_ip"));
		System.out.println(catesParams.getCatesProps("文学"));
		//		System.out.println(catesParams.getUserAndPwProps("password"));

	}

}
