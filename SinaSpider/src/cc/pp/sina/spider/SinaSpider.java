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
import cc.pp.sina.utils.BaseInfoUrl;
import cc.pp.sina.utils.HttpUtils;

public class SinaSpider {

	private final BaseInfoUrl baseInfoUrl = new BaseInfoUrl();
	private final HttpUtils httpUtils = new HttpUtils();

	/**
	 * 主函数
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws HttpException, IOException, SQLException {

		SinaSpider sinaSpider = new SinaSpider();

		/***********本地环境************/
		//		SpiderJDBC spiderJDBC = new SpiderJDBC("127.0.0.1", "root", "");
		/***********线上环境************/
		SpiderJDBC spiderJDBC = new SpiderJDBC("192.168.25.44");

		if (spiderJDBC.mysqlStatus()) {
			sinaSpider.importWeiboToDb(spiderJDBC, "1893752581", 7122);
			spiderJDBC.sqlClose();
		}

	}

	/**
	 * 获取微博数据
	 * @param username
	 * @param count
	 * @throws HttpException
	 * @throws IOException
	 * @throws SQLException 
	 */
	public void importWeiboToDb(SpiderJDBC myJdbc, String username, int count) throws HttpException, IOException,
			SQLException {

		int pageCount = count / 10 + 1;
		for (int page = 1; page <= pageCount; page++) {
			String url = baseInfoUrl.getUrl(username, "profile", page);
			String html = httpUtils.getHtml(url);
			Document doc = Jsoup.parse(html);
			Elements table = doc.select("div.c");
			HashMap<String, String> result = new HashMap<String, String>();
			for (Element data : table) {
				if (data.select("div").size() > 1) {
					result = parseWeibo(data);
					myJdbc.insertWeiboInfo("spiderweibo", username, result.get("oriNickname"), //
							result.get("oriUsername"), result.get("oriWeiboText"), result.get("oriZanCount"), //
							result.get("oriRepCount"), result.get("oriComcount"), result.get("weiboText"), //
							result.get("zanCount"), result.get("repcount"), result.get("comCount"), //
							result.get("createAt"), result.get("source"));
				}
			}
		}
	}

	/**
	 * 提取微博信息
	 * @param data
	 * @return
	 */
	public HashMap<String, String> parseWeibo(Element data) {

		String oriNickname = data.select("div").get(1).select("span").get(0).select("a").text();
		String oriUsername = data.select("div").get(1).select("span").get(0).select("a").attr("href");
		String oriWeiboText = data.select("div").get(1).select("span").get(1).text();
		String oriZanCount, oriRepCount, oriComcount, weiboText;
		String creatatAndSource, zanCount, repcount, comCount;

		if (data.select("div").size() == 4) {
			oriZanCount = data.select("div").get(2).select("span").get(0).text();
			oriRepCount = data.select("div").get(2).select("span").get(1).text();
			oriComcount = data.select("div").get(2).select("a").get(2).text();
			weiboText = data.select("div").get(3).text();
			creatatAndSource = data.select("div").get(3).select("span").get(1).text();
			zanCount = data.select("div").get(3).select("a").get(0).text();
			repcount = data.select("div").get(3).select("a").get(1).text();
			comCount = data.select("div").get(3).select("a").get(2).text();
		} else {
			oriZanCount = data.select("div").get(1).select("span").get(2).text();
			oriRepCount = data.select("div").get(1).select("span").get(3).text();
			oriComcount = data.select("div").get(1).select("a").get(1).text();
			weiboText = data.select("div").get(2).text();
			creatatAndSource = data.select("div").get(2).select("span").get(1).text();
			zanCount = data.select("div").get(2).select("a").get(0).text();
			repcount = data.select("div").get(2).select("a").get(1).text();
			comCount = data.select("div").get(2).select("a").get(2).text();
		}

		oriUsername = oriUsername.substring(18, oriUsername.indexOf("?"));
		if (oriWeiboText.indexOf("@") > 0) {
			oriWeiboText = oriWeiboText.substring(0, oriWeiboText.indexOf("@"));
		}
		oriZanCount = oriZanCount.substring(2, oriZanCount.length() - 1);
		oriRepCount = oriRepCount.substring(5, oriRepCount.length() - 1);
		oriComcount = oriComcount.substring(5, oriComcount.length() - 1);
		weiboText = weiboText.substring(weiboText.indexOf(":") + 1, weiboText.indexOf("赞")).trim();
		String createAt = System.currentTimeMillis() / 1000 + ","
				+ creatatAndSource.substring(0, creatatAndSource.indexOf("来自")).trim();
		String source = creatatAndSource.substring(creatatAndSource.indexOf("来自") + 2).trim();
		zanCount = zanCount.substring(2, zanCount.length() - 1);
		repcount = repcount.substring(3, repcount.length() - 1);
		comCount = comCount.substring(3, comCount.length() - 1);

		HashMap<String, String> result = new HashMap<String, String>();
		result.put("oriNickname", oriNickname);
		result.put("oriUsername", oriUsername);
		result.put("oriWeiboText", oriWeiboText);
		result.put("oriZanCount", oriZanCount);
		result.put("oriRepCount", oriRepCount);
		result.put("oriComcount", oriComcount);
		result.put("weiboText", weiboText);
		result.put("zanCount", zanCount);
		result.put("repcount", repcount);
		result.put("comCount", comCount);
		result.put("createAt", createAt);
		result.put("source", source);

		return result;
	}

	/**
	 * 获取   微博数、关注数、粉丝数
	 * @param username
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public int[] getThreeCounts(String username) throws HttpException, IOException {

		int[] threecounts = new int[3];
		String fanscount, friendscount, weiboscount;
		String url = baseInfoUrl.getUrl(username, "profile", 1);
		String html = httpUtils.getHtml(url);
		Document doc = Jsoup.parse(html);
		Elements table = doc.select("div.tip2");
		weiboscount = table.select("span").text();
		friendscount = table.select("a").get(0).text();
		fanscount = table.select("a").get(1).text();
		weiboscount = weiboscount.substring(3, weiboscount.length() - 1);
		friendscount = friendscount.substring(3, friendscount.length() - 1);
		fanscount = fanscount.substring(3, fanscount.length() - 1);
		threecounts[0] = Integer.parseInt(weiboscount);
		threecounts[1] = Integer.parseInt(friendscount);
		threecounts[2] = Integer.parseInt(fanscount);

		return threecounts;
	}

}
