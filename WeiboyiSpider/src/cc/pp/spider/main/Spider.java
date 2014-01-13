package cc.pp.spider.main;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cc.pp.spider.core.HtmlParser;
import cc.pp.spider.domain.UserInfo;
import cc.pp.spider.sql.LocalJDBC;

public class Spider implements Runnable {

	private final HtmlParser htmpParser = new HtmlParser();
	private final LocalJDBC myjdbc;
	private final String type;
	private final String cate;
	private final String tablename;

	private static AtomicInteger count = new AtomicInteger(0);

	public Spider(LocalJDBC myjdbc, String type, String cate, String tablename) {
		this.myjdbc = myjdbc;
		this.type = type;
		this.cate = cate;
		this.tablename = tablename;
	}

	@Override
	public void run() {
		try {
			System.out.println(count.addAndGet(1));
			/*************获取每个平台每个类别的页数*************/
			String html = htmpParser.getHtmlData(type, cate, 1);
			int pageCount = htmpParser.parserPageCount(html);
			/******************循环获取数据*****************/
			pageCount = (pageCount > 5) ? 5 : pageCount;
			for (int i = 1; i <= pageCount; i++) {
				html = htmpParser.getHtmlData(type, cate, i);
				List<UserInfo> userInfos = htmpParser.parserHtml(html);
				// 存储数据
				htmpParser.dumpToDb(myjdbc, tablename, userInfos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
