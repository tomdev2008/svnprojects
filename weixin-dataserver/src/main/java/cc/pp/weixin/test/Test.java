package cc.pp.weixin.test;

import java.io.IOException;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.pp.weixin.spider.Spider;
import cc.pp.weixin.sql.LocalJDBC;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {

		Spider spider = new Spider();
		String url = "http://www.anyv.net/";
		Document doc = Jsoup.connect(url).get();
		LocalJDBC myjdbc = new LocalJDBC("weixin");
		if (myjdbc.mysqlStatus()) {
			Elements listsli = doc.select("li");
			for (Element li : listsli) {
				if (li.select("a").attr("href").contains("viewnews")) {
					String text = li.select("span").text();
					if ((text.matches("^[0-9]+$")) || (text.contains(")"))) {
						if (!li.select("a").attr("title").matches("^[0-9]+$")) { // 用户名全为数字的踢掉
							System.out.println(li.select("a").attr("title") + "," + text);
							String fanscount = text;
							if (text.contains(")")) {
								fanscount = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
							}
							String name = li.select("a").attr("title");
							String[] userinfo = spider.retriveWidAndDescript(li.select("a").attr("href"));
							myjdbc.insertWeixinUserInfo("weixinusers", name, "0", userinfo[0], userinfo[1], fanscount);
						}
					}
				}
			}
			myjdbc.sqlClose();
		}
	}

}
