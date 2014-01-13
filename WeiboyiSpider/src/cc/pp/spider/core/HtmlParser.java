package cc.pp.spider.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import cc.pp.picture.download.PictureGet;
import cc.pp.spider.constant.ParamsConstant;
import cc.pp.spider.domain.UserInfo;
import cc.pp.spider.sql.LocalJDBC;

@SuppressWarnings("unused")
public class HtmlParser {

	private final WeiboyiUtils weiboyiUtils = new WeiboyiUtils();
	private String type;
	private String cate;

	private final static HashMap<String, String> cates = ParamsConstant.getEngCates();

	public HtmlParser() {
		if (!weiboyiUtils.login()) {
			System.out.println("Login error!");
		}
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		HtmlParser htmpParser = new HtmlParser();
		String html = htmpParser.getHtmlData("新浪微博", "IT", 1);
		//		int pageCount = htmpParser.parserPageCount(html);
		//		System.out.println(pageCount);
		List<UserInfo> userinfos = htmpParser.parserHtml(html);
		System.out.println(JSONArray.fromObject(userinfos));

	}

	/**
	 * 获取总页数
	 * @param html
	 * @return
	 */
	public int parserPageCount(String html) {

		Elements divs = Jsoup.parse(html).select("span.altogether");
		if (divs.toString().length() > 0) {
			String str = divs.text();
			str = str.substring(1, str.length() - 1);
			return Integer.parseInt(str);
		} else {
			return 1;
		}
	}

	/**
	 * 数据插入数据库
	 * @throws Exception 
	 */
	public void dumpToDb(LocalJDBC myjdbc, String tablename, List<UserInfo> userInfos) //
			throws Exception {

		//		myjdbc.createUserinfoTable(tablename);
		for (UserInfo userinfo : userInfos) {
			// 下载图片
			PictureGet.createImage(userinfo.getPriceurl(),
					cates.get(userinfo.getCate()) + "_" + userinfo.getDomainname() + ".png", "pricepic/");
			// 存储数据
			myjdbc.insertUserInfo(tablename, userinfo.getType(), userinfo.getCate(), //
					userinfo.getNickname(), userinfo.getPriceurl(), userinfo.getDomainname());
		}
	}

	/**
	 * 解析页面数据
	 * @param html
	 * @return
	 */
	public List<UserInfo> parserHtml(String html) {

		List<UserInfo> result = new ArrayList<UserInfo>();
		String priceurl, nickname, domainname;
		Elements tables = Jsoup.parse(html).select("table");
		Elements rows = tables.get(1).select("tr");
		for (int i = 1; i < rows.size(); i++) {
			priceurl = rows.get(i).select("td").get(1).select("span").attr("style");
			nickname = rows.get(i).select("td").get(0).select("span").get(0).select("span").attr("title");
			domainname = rows.get(i).select("td").get(0).select("span").get(0).select("a").attr("href");
			if ((priceurl.indexOf("(") > 0) && (priceurl.indexOf(")") > 0)) {
				priceurl = priceurl.substring(priceurl.indexOf("(") + 1, priceurl.indexOf(")"));
			}
			if (nickname.indexOf(" ") > 0) {
				nickname = nickname.substring(0, nickname.indexOf(" "));
			}
			if (domainname.lastIndexOf("/") > 0) {
				domainname = domainname.substring(domainname.lastIndexOf("/") + 1);
			}
			UserInfo userinfo = new UserInfo();
			userinfo.setNickname(nickname);
			userinfo.setDomainname(domainname);
			userinfo.setPriceurl(priceurl);
			userinfo.setType(type);
			userinfo.setCate(cate);
			result.add(userinfo);
		}

		return result;
	}

	/**
	 * 根据平台和页码抓取信息
	 * @param weibo_type_filter：平台信息
	 * @param page：页码
	 * @return
	 * @throws Exception 
	 */
	private String getHtmlData(String weibo_type_filter, int page) throws Exception {
		return getHtmlData(weibo_type_filter, "", "", 0, 0, 0, 0, "", page);
	}

	/**
	 * 根据平台、行业类别、页码抓取信息
	 * @param weibo_type_filter：平台信息
	 * @param content_categories：行业类别
	 * @param page：页码
	 * @return
	 * @throws Exception
	 */
	public String getHtmlData(String weibo_type_filter, String content_categories, int page) throws Exception {
		return getHtmlData(weibo_type_filter, content_categories, "", 0, 0, 0, 0, "", page);
	}

	/**
	 * 获取页面数据
	 */
	private String getHtmlData(String weibo_type_filter, String content_categories, String switch_price,
			int price_from, int price_to, int followers_count_min, int followers_count_max,
			String audience_gender_filter, int page) throws Exception {
		this.type = weibo_type_filter;
		this.cate = content_categories;
		return weiboyiUtils.getWeiboyiHtml(weibo_type_filter, content_categories, switch_price, price_from, price_to,
				followers_count_min, followers_count_max, audience_gender_filter, page);
	}

}
