package cc.pp.sina.spider;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cc.pp.sina.sql.SpiderJDBC;
import cc.pp.sina.utils.HttpUtils;
import cc.pp.sina.utils.SingleWeiboUrl;

public class SingleWeiboSpider {

	SingleWeiboUrl singleWeiboUrl = new SingleWeiboUrl();
	private final HttpUtils httpUtils = new HttpUtils();

	/**
	 * 主函数
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws HttpException, IOException, SQLException {

		SingleWeiboSpider singleWeiboSpider = new SingleWeiboSpider();
		String wburl = "http://weibo.com/3345820740/A5igBaQUX";

		int startpage = 500;
		int endpage = 500;

		/***********本地环境************/
		SpiderJDBC spiderJDBC = new SpiderJDBC("127.0.0.1", "root", "");
		/***********线上环境************/
		// ip地址参数
		//		int ip = 45;
		//				if (ip == 50) {
		//			startpage = 1 + 500 * (33 - 26);
		//			endpage = 500 + 500 * (33 - 26);
		//		} else if (ip == 51) {
		//			startpage = 1 + 500 * (35 - 26);
		//			endpage = 500 + 500 * (35 - 26);
		//		} else {
		//			startpage = 1 + 500 * (ip - 26);
		//			endpage = 500 + 500 * (ip - 26);
		//		}
		//		SpiderJDBC spiderJDBC = new SpiderJDBC("192.168.1." + ip);

		if (spiderJDBC.mysqlStatus()) {
			singleWeiboSpider.importWeiboToDb(spiderJDBC, wburl, startpage, endpage);
			spiderJDBC.sqlClose();
		}

	}

	/**
	 * 爬取数据，并将爬取的微博数据入库
	 * @param wburl
	 * @param repcount
	 * @throws HttpException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void importWeiboToDb(SpiderJDBC myJdbc, String wburl, int startpage, int endpage) throws HttpException,
			IOException, SQLException {

		// 提取PC端微博链接中的username、id
		singleWeiboUrl.tackleWeiboUrl(wburl);
		// 翻页数
		String url = "", html = "";
		HashMap<String, String> result = new HashMap<String, String>();
		for (int page = startpage; page <= 1; page++) {

			System.out.println(page);

			// 根据页码获取手机端微博链接
			url = singleWeiboUrl.getUrl(page);

			System.out.println(url);

			html = httpUtils.getHtml(url);
			Document doc = Jsoup.parse(html);
			Elements divs = doc.select("div.c");

			for (int i = 3; i < divs.size() - 1; i++) {
				//					System.out.println(divs.get(i));
				result = weiboParser(divs.get(i));
				myJdbc.insertRepWeiboInfo("singleweiboinfo", result.get("domainname"), result.get("nickname"), //
						result.get("weibotext"), result.get("createat"), result.get("source"));
			}
		}

	}

	/**
	 * 解析微博信息
	 * @param data
	 * @return
	 */
	public HashMap<String, String> weiboParser(Element data) {

		HashMap<String, String> result = new HashMap<String, String>();

		String nickname = data.select("a").get(0).text();
		// 这里提取的是个性化域名，或者用户名，并非都是用户名
		String domainname = data.select("a").get(0).attr("href");
		String weibotext = data.text();
		String createatAndsource = data.select("span").text();
		domainname = domainname.substring(3, domainname.indexOf("?"));
		weibotext = weibotext.substring(weibotext.indexOf(":") + 1);
		String createat = System.currentTimeMillis() / 1000 + ","
				+ createatAndsource.substring(0, createatAndsource.indexOf("来自")).trim().replace("?", "");
		String source = createatAndsource.substring(createatAndsource.indexOf("来自") + 2).trim();

		result.put("nickname", nickname);
		result.put("domainname", domainname);
		result.put("weibotext", weibotext);
		result.put("createat", createat);
		result.put("source", source);

		return result;
	}

}
