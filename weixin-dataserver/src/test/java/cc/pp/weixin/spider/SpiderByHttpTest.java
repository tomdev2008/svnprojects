package cc.pp.weixin.spider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

import cc.pp.weixin.utils.HttpUtils;

public class SpiderByHttpTest {

	public HttpUtils httpUtils = new HttpUtils();

	public void spiderTest() throws IOException {
		String url = "http://www.anyv.net/";
		Document doc = Jsoup.connect(url).get();
		Elements navdiv = doc.select("div.solidblockmenu").select("li");
		for (Element divnode : navdiv) {
			if (divnode.select("a").attr("href").contains("category")) {
				System.out.println(divnode.select("a").text() + " " + divnode.select("a").attr("href"));
			}
		}
	}

	@Ignore
	public void importDataToDBTest() throws IOException, SQLException, InterruptedException {
		SpiderByHttp spider = new SpiderByHttp();
		String url = "http://www.anyv.net/";
		boolean flag = spider.importDataToLocalDB(url, "weixin", "weixinusers");
		assertTrue(flag);
	}

	@Ignore
	public void importDataToDBDisTest() throws IOException, SQLException, InterruptedException {
		SpiderByHttp spider = new SpiderByHttp();
		boolean flag = spider.importDataToLocalDB("科技", "http://www.anyv.net/index.php/category-19", //
				"weixin", "weixinusers");
		assertTrue(flag);
	}

	@Ignore
	public void retriveCategoryTest() throws IOException {
		SpiderByHttp spider = new SpiderByHttp();
		String url = "http://www.anyv.net/";
		String html = httpUtils.retriveHtml(url, "gbk");
		List<HashMap<String, String>> cates = spider.retriveCategory(html);
		for (HashMap<String, String> cate : cates) {
			System.out.println(cate.get("cates") + "," + cate.get("url"));
		}
		assertEquals(18, cates.size());
	}

	@Ignore
	public void retriveWidAndDescriptTest() throws IOException {
		SpiderByHttp spider = new SpiderByHttp();
		//		String url = "http://www.anyv.net/index.php/viewnews-8889";
		String url = "http://www.anyv.net/index.php/viewnews-314";
		String html = httpUtils.retriveHtml(url, "gbk");
		String[] cates = spider.retriveWidAndDescript(html);
		System.out.println(cates[0]);
		System.out.println(cates[1]);
		//		assertEquals("hefeishuo", cates[0].trim());
		//		assertEquals("说说合肥那点事儿。", cates[1].trim());
	}

	@Ignore
	public void retriveUsersFromCateTest() throws IOException, InterruptedException {
		SpiderByHttp spider = new SpiderByHttp();
		String url = "http://www.anyv.net/index.php/category-1-page-1";
		List<HashMap<String, String>> users = spider.retriveUsersFromCate(url);
		System.out.println(users.size());
		assertEquals(95, users.size());
	}

	@Ignore
	public void retriveUsersInfoFromCateTest() throws IOException, InterruptedException {
		SpiderByHttp spider = new SpiderByHttp();
		String url = "http://www.anyv.net/index.php/category-28";
		List<HashMap<String, String>> users = spider.retriveUsersFromCate(url);
		String cates = "明星";
		for (HashMap<String, String> user : users) {
			System.out.println(user.get("name") + "," + cates + "," + user.get("wid") + "," + user.get("description"));
		}
	}

	@Test
	public void LocalTest() throws IOException, SQLException, InterruptedException {
		SpiderByHttp spider = new SpiderByHttp();
		String url = "http://www.anyv.net/";
		//		spider.retriveHomePageLocal(url, "weixin", "weixinusers");
		spider.importDataToLocalDB(url, "weixin", "weixinusers");
	}

}
