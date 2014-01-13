package cc.pp.sina.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demo {

	private static Logger logger = LoggerFactory.getLogger(Demo.class);

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		System.out.println(System.currentTimeMillis() / 1000);
		System.out.println(System.currentTimeMillis() / 1000 + 30 * 86400);
		logger.info("OK");
	}

}
