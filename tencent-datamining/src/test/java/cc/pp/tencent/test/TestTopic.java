package cc.pp.tencent.test;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.analysis.topic.TopicAnalysis;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

public class TestTopic {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// 
		//		HttpUtils httpUtils = new HttpUtils();
		//		String url = "http://ti.3g.qq.com/g/s?sid=AQw6lQ3IZHjLZmI37Wtovs5e&aid=sm&keyword=%E5%8F%AA%E5%9B%A0%E6%A8%A1%E7%89%B9%E4%BC%BC%E5%88%9D%E6%81%8B&rw=0&start=&end=20130723&hw=0&sss=32&eid=431&cate=0&psize=10&pid=1";
		//		String html = httpUtils.getTopicHtml(url);
		//		System.out.println(html);
		//		TopicUtils tpoicUtils = new TopicUtils();
		//		String[] wids = tpoicUtils.getWids("只因模特似初恋", 2);
		//		System.out.println(wids.length);
		OAuthV1 oauth = new OAuthV1();
		TopicAnalysis.oauthInit(oauth, "256551ad0bdb46ae8cb9bbdc9a7d2d69", "2315934f376d61804d9056e53584e374");
		TAPI weibo = new TAPI(oauth.getOauthVersion());
		//		String weiboinfo = weibo.reList(oauth, "json", "0", "291582086103964", "1", "0", "100", "0");
		String singlewbinfo = weibo.show(oauth, "json", "278015122501598");
		JsonNode swbdata = JsonUtils.getJsonNode(singlewbinfo);
		System.out.println(singlewbinfo);
		if (swbdata.get("data").toString().equals("null")) {
			System.out.println("ok");
		}

	}

}
