package cc.pp.price.cpm;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.spider.domain.UserInfo;
import cc.pp.spider.sql.LocalJDBC;

public class WeiboSpider implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(WeiboSpider.class);

	private final LocalJDBC myJdbc;
	private final UserInfo userInfo;
	private final String tablename;

	private static AtomicInteger count = new AtomicInteger(0);

	public WeiboSpider(LocalJDBC myJdbc, UserInfo userInfo, String tablename) {
		super();
		this.myJdbc = myJdbc;
		this.userInfo = userInfo;
		this.tablename = tablename;
	}

	@Override
	public void run() {

		System.out.println(count.addAndGet(1));

		try {
			int avereadcount = WeiboReadCount.averagecNote(userInfo.getDomainname());
			myJdbc.insertReadInfo(tablename, userInfo.getType(), userInfo.getCate(), //
					userInfo.getNickname(), userInfo.getDomainname(), //
					avereadcount, userInfo.getPriceurl());
		} catch (SQLException e) {
			logger.info("Error uid is: " + userInfo.getDomainname());
			e.printStackTrace();
		} catch (RuntimeException e) {
			logger.info("Error uid is: " + userInfo.getDomainname());
			e.printStackTrace();
		}
	}

}
