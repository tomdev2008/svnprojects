package cc.pp.price.cpm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import cc.pp.spider.utils.HttpUtils;

public class WeiboReadCount {

	private final static String BASE_URL = "http://t.qq.com/";
	private final static String ENCODING = "utf-8";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String username = "leng-xiao-hua";
		int aveReaderSum = WeiboReadCount.averagecNote(username);
		System.out.println(aveReaderSum);

	}

	/**
	 * 计算最近20条微博的平均阅读量
	 * @param username
	 * @return
	 */
	public static int averagecNote(String username) {

		String html = HttpUtils.doGet(BASE_URL + username, ENCODING);
		Elements divs = Jsoup.parse(html).select("div.pubInfo");
		int sum = 0;
		for (int i = 0; i < divs.size(); i++) {
			sum = sum + patternNumbers(divs.get(i).select("span.cNote").attr("title"));
		}
		if (divs.size() == 0) {
			return 0;
		} else {
			sum = sum / divs.size();
			return sum;
		}
	}

	/**
	 * 从一字符串中匹配数字
	 */
	public static int patternNumbers(String str) {

		String result = "";
		Pattern patt = Pattern.compile("[1-9]");
		Matcher matc = patt.matcher(str);
		while (matc.find()) {
			result = result + matc.group();
		}
		if (result == "") {
			return 0;
		} else {
			return Integer.parseInt(result);
		}
	}

}
