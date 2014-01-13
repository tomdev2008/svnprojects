package cc.pp.weixin.spider;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.weixin.sql.SpiderJDBC;

public class Spider {

	private static Logger logger = LoggerFactory.getLogger(Spider.class);

	private static enum CATEGORY {
		cates, url
	};

	private static enum USERINFO {
		name, wid, description
	};

	public static void main(String[] args) throws IOException, SQLException, InterruptedException {

		Spider spider = new Spider();
		String url = "http://www.anyv.net/";
		spider.retriveHomePage(url, 151, "weixinusers");
		spider.importDataToDB(url, 151, "weixinusers");

	}

	/**
	 * 获取首页信息
	 * @param url
	 * @throws SQLException
	 * @throws IOException
	 */
	public void retriveHomePage(String url, int ip, String tablename) throws SQLException, IOException {

		logger.info("HomePage is：" + url);
		Document doc = Jsoup.connect(url).get();
		SpiderJDBC myjdbc = new SpiderJDBC(ip);
		if (myjdbc.mysqlStatus()) {
			Elements listsli = doc.select("li");
			for (Element li : listsli) {
				if (li.select("a").attr("href").contains("viewnews")) {
					String text = li.select("span").text();
					if ((text.matches("^[0-9]+$")) || (text.contains(")"))) {
						if (!li.select("a").attr("title").matches("^[0-9]+$")) { // 用户名全为数字的踢掉
							//							System.out.println(li.select("a").attr("title") + "," + text);
							String fanscount = text;
							if (text.contains(")")) {
								fanscount = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
							}
							String name = li.select("a").attr("title");
							String[] userinfo = this.retriveWidAndDescript(li.select("a").attr("href"));
							myjdbc.insertWeixinUserInfo(tablename, name, "0", userinfo[0], userinfo[1], fanscount);
						}
					}
				}
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * 获取微信主数据，并存入数据库
	 * @param url
	 * @param ip
	 * @param tablename
	 * @throws IOException
	 * @throws SQLException
	 * @throws InterruptedException 
	 */
	public void importDataToDB(String url, int ip, String tablename) throws IOException, SQLException,
			InterruptedException {

		List<HashMap<String, String>> category = this.retriveCategory(url);
		SpiderJDBC myjdbc = new SpiderJDBC(151);
		if (myjdbc.mysqlStatus()) {
			for (HashMap<String, String> temp : category) {
				logger.info("Url is：" + temp.get("url"));
				List<HashMap<String, String>> users = this.retriveUsersFromCate(temp.get("url"));
				for (HashMap<String, String> user : users) {
					myjdbc.insertWeixinUserInfo(tablename, user.get("name"), temp.get("cates"), //
							user.get("wid"), user.get("description"));
				}
				Thread.sleep(60000);
			}
			myjdbc.sqlClose();
		}
	}

	public void importDataToDB(String cates, String url, int ip, String tablename) throws IOException, SQLException {

		SpiderJDBC myjdbc = new SpiderJDBC(151);
		if (myjdbc.mysqlStatus()) {
			List<HashMap<String, String>> users = this.retriveUsersFromCate(url);
			for (HashMap<String, String> user : users) {
				myjdbc.insertWeixinUserInfo(tablename, user.get("name"), cates, user.get("wid"),
						user.get("description"));
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * 从分类链接中提取每个微信主的信息
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public List<HashMap<String, String>> retriveUsersFromCate(String url) throws IOException {

		List<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
		int page = 1;
		String aurl = url + "-page-" + page;
		Document doc = Jsoup.connect(aurl).get();
		while (doc.toString().contains("下一页")) {
			Elements navdiv = doc.select("div.column").select("li");
			for (Element usernode : navdiv) {
				if (usernode.select("a").attr("href").length() > 10) {
					//					System.out.println(page + ": " + usernode.select("a").attr("href") + ","
					//							+ usernode.select("a").attr("title"));
					HashMap<String, String> temp = new HashMap<String, String>();
					String[] userinfo = this.retriveWidAndDescript(usernode.select("a").attr("href"));
					temp.put(USERINFO.name.toString(), usernode.select("a").attr("title"));
					temp.put(USERINFO.wid.toString(), userinfo[0]);
					temp.put(USERINFO.description.toString(), userinfo[1]);
					users.add(temp);
				}
			}
			page++;
			aurl = url + "-page-" + page;
			doc = Jsoup.connect(aurl).get();
		}
		Elements navdiv = doc.select("div.column").select("li");
		for (Element usernode : navdiv) {
			if (usernode.select("a").attr("href").length() > 10) {
				HashMap<String, String> temp = new HashMap<String, String>();
				String[] userinfo = this.retriveWidAndDescript(usernode.select("a").attr("href"));
				temp.put(USERINFO.name.toString(), usernode.select("a").attr("title"));
				temp.put(USERINFO.wid.toString(), userinfo[0]);
				temp.put(USERINFO.description.toString(), userinfo[1]);
				users.add(temp);
			}
		}

		return users;
	}

	/**
	 * 提取微信号和介绍信息
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String[] retriveWidAndDescript(String url) throws IOException {

		Document doc = Jsoup.connect(url).get();
		Elements navdiv = doc.select("h5");
		String info = navdiv.text();
		String[] userinfo = new String[2];
		if (info.indexOf("介绍") > 0) {
			userinfo[0] = info.substring(4, info.indexOf("介绍"));
			userinfo[1] = info.substring(info.indexOf("介绍") + 3);
		} else if (info.contains("微信号")) {
			userinfo[0] = info.substring(4);
		}

		return userinfo;
	}

	/**
	 * 获取分类信息和链接
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public List<HashMap<String, String>> retriveCategory(String url) throws IOException {

		Document doc = Jsoup.connect(url).get();
		Elements navdiv = doc.select("div.solidblockmenu").select("li");
		List<HashMap<String, String>> category = new ArrayList<HashMap<String, String>>();
		for (Element divnode : navdiv) {
			//			System.out.println(divnode.select("a").attr("href") + "," + divnode.select("a").text());
			if (divnode.select("a").attr("href").contains("category")) {
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put(CATEGORY.cates.toString(), divnode.select("a").text());
				temp.put(CATEGORY.url.toString(), divnode.select("a").attr("href"));
				category.add(temp);
			}
		}

		return category;
	}

}
