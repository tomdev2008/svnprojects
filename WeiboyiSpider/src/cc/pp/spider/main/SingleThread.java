package cc.pp.spider.main;

import java.util.List;

import cc.pp.spider.constant.ParamsConstant;
import cc.pp.spider.core.HtmlParser;
import cc.pp.spider.domain.UserInfo;
import cc.pp.spider.sql.LocalJDBC;

public class SingleThread {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			String[] types = ParamsConstant.WEIBO_TYPE_FILTER;
			String[] cates = ParamsConstant.CONTENT_CATEGORIES;
			for (String type : types) {
				for (String cate : cates) {
					HtmlParser htmpParser = new HtmlParser();
					System.out.println(type + ": " + cate);
					/*************获取每个平台每个类别的页数*************/
					String html = htmpParser.getHtmlData(type, cate, 1);
					int pageCount = htmpParser.parserPageCount(html);
					//					System.out.println(type + "-----------" + cate + "----------" + pageCount);
					/******************循环获取数据*****************/
					for (int i = 1; i <= pageCount; i++) {
						System.out.println(type + ": " + cate + ": page=" + i);
						html = htmpParser.getHtmlData(type, cate, i);
						List<UserInfo> userInfos = htmpParser.parserHtml(html);
						htmpParser.dumpToDb(myjdbc, "weiboyiinfo", userInfos);
					}
				}
			}

			myjdbc.sqlClose();
		}


	}

}
