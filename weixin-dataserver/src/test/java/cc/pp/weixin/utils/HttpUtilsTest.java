package cc.pp.weixin.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class HttpUtilsTest {

	@Ignore
	public void doGetForErrUrlTest() {
		HttpUtils httputils = new HttpUtils();
		String result = httputils.doGet("http://www.baidu.c", "utf-8");
		assertEquals("err", result);
	}

	@Ignore
	public void doGetForTrueUrlTest() {
		HttpUtils httputils = new HttpUtils();
		String result = httputils.doGet("http://www.baidu.com/", "utf-8");
		assertTrue(!result.equalsIgnoreCase("err"));
	}

	@Test
	public void doGetTest() {
		HttpUtils httputils = new HttpUtils();
		String html = httputils.doGet("http://www.anyv.net/index.php/category-28-page-2", "gbk");
		System.out.println(html);
		//		Document doc = Jsoup.parse(html);
		//		System.out.println(doc.toString().contains("下一页"));
		//		assertTrue(!html.equalsIgnoreCase("err"));
	}

}
