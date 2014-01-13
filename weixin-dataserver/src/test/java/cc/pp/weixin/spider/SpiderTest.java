package cc.pp.weixin.spider;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Ignore;
import org.junit.Test;

public class SpiderTest {

	@Ignore
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
	public void retriveCategoryTest() throws IOException {
		Spider spider = new Spider();
		String url = "http://www.anyv.net/index.php/category-1";
		List<HashMap<String, String>> cates = spider.retriveCategory(url);
		assertEquals(18, cates.size());
	}

	@Ignore
	public void retriveWidAndDescriptTest() throws IOException {
		Spider spider = new Spider();
		String url = "http://www.anyv.net/index.php/viewnews-8889";
		String[] cates = spider.retriveWidAndDescript(url);
		assertEquals("hefeishuo", cates[0].trim());
		assertEquals("说说合肥那点事儿。", cates[1].trim());
	}

	@Ignore
	public void retriveUsersFromCateTest() throws IOException {
		Spider spider = new Spider();
		String url = "http://www.anyv.net/index.php/category-1";
		List<HashMap<String, String>> users = spider.retriveUsersFromCate(url);
		assertEquals(95, users.size());
	}

	@Test
	public void retriveUsersInfoFromCateTest() throws IOException {
		Spider spider = new Spider();
		String url = "http://www.anyv.net/index.php/category-51";
		List<HashMap<String, String>> users = spider.retriveUsersFromCate(url);
		String cates = "明星";
		for (HashMap<String, String> user : users) {
			System.out.println(user.get("name") + "," + cates + "," + user.get("wid") + "," + user.get("description"));
		}
	}
}
