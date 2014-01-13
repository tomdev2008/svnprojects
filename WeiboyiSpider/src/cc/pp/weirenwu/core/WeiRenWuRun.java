package cc.pp.weirenwu.core;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cc.pp.spider.sql.LocalJDBC;

public class WeiRenWuRun implements Runnable {

	private final LocalJDBC localJDBC;
	private final WeiRenWuUtil weiRenWuUtil;
	private final int page;

	private static AtomicInteger count = new AtomicInteger();

	public WeiRenWuRun(LocalJDBC localJDBC, WeiRenWuUtil weiRenWuUtil, int page) {
		this.localJDBC = localJDBC;
		this.weiRenWuUtil = weiRenWuUtil;
		this.page = page;
	}

	@Override
	public void run() {

		System.out.println(count.addAndGet(1));

		try {
			List<BozhuInfo> bozhuInfos = weiRenWuUtil.parserBozhuInfo(page);
			if (bozhuInfos != null) {
				for (BozhuInfo bozhuInfo : bozhuInfos) {
					localJDBC.inserWeiRenWu("weirenwu", bozhuInfo);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
