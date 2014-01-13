package cc.pp.spider.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import cc.pp.spider.constant.ParamsConstant;

/**
 * 微博易模拟登陆，网页抓取
 * 主页：http://chuanbo.weiboyi.com/
 * 账号：blackcat，密码：blackcat1
 */
public class WeiboyiUtils {

	// 用户名和密码
	private String username = "";
	private String password = "";

	private DefaultHttpClient httpClient;

	// 登陆页面
	private static final String LOGIN_URL = "http://chuanbo.weiboyi.com/";
	// 微博主信息页面，目前只针对“查看微博主”这个页面
	private static final String OVER_TWEET = "http://chuanbo.weiboyi.com/hworder/overtweet/index?";

	public WeiboyiUtils() {
		try {
			this.username = ParamsConstant.getUserAndPw("username");
			this.password = ParamsConstant.getUserAndPw("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 主函数
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		WeiboyiUtils weiboyiUtils = new WeiboyiUtils();
		if (weiboyiUtils.login()) {
			String html = weiboyiUtils.getWeiboyiHtml("新浪微博", "", "", 0, 0, 0, 0, "", 1);
			System.out.println(html);
		} else {
			System.out.println("Login error!");
		}

	}

	/**
	 * 获取页面数据
	 */
	public String getWeiboyiHtml(String weibo_type_filter, String content_categories, String switch_price,
			int price_from, int price_to, int followers_count_min, int followers_count_max,
			String audience_gender_filter, int page) throws Exception {

		String url = getUrl(weibo_type_filter, content_categories, switch_price, price_from, //
				price_to, followers_count_min, followers_count_max, audience_gender_filter, page);
		HttpGet httpGet = new HttpGet(url);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String html;
		try {
			html = httpClient.execute(httpGet, responseHandler);
		} catch (Exception e) {
			html = "error";
		} finally {
			httpGet.abort();
		}

		return html;
	}

	/**
	 * 模拟登陆
	 * @return
	 */
	public boolean login() {

		httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(LOGIN_URL);
		httpPost.getParams().setParameter("http.protocol.handle-redirects", true);
		// 添加参数
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair("username", username));
		formParams.add(new BasicNameValuePair("password", password));
		//		formParams.add(new BasicNameValuePair("piccode", "2a5u"));
		formParams.add(new BasicNameValuePair("mode", "1"));
		formParams.add(new BasicNameValuePair("typelogin", "1"));

		HttpResponse httpResponse = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
			httpResponse = httpClient.execute(httpPost);
		} catch (Exception e) {
			//			e.printStackTrace();
		} finally {
			httpPost.abort();
		}

		if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
			return true;
		}

		return false;
	}

	/**
	 * 根据参数拼接URL
	 * @param weibo_type_filter：平台
	 * @param content_categories：常见分类 
	 * @param switch_price：价格类型
	 * @param price_from：价格下限
	 * @param price_to：价格上限
	 * @param followers_count_min：粉丝量下限
	 * @param followers_count_max：粉丝量上限
	 * @param audience_gender_filter：粉丝性别
	 * @param page：页码
	 * @return
	 * @throws Exception 
	 */
	private String getUrl(String weibo_type_filter, String content_categories, String switch_price, int price_from,
			int price_to, int followers_count_min, int followers_count_max, String audience_gender_filter, int page)
			throws Exception {

		String url = OVER_TWEET + "weiboname=" + ParamsConstant.INPUT_TAGS;
		if (weibo_type_filter != "") {
			url = url + "&weibo_type_filter=" + weibo_type_filter;
		}
		if (content_categories != "") {
			url = url + "&content_categories=" + ParamsConstant.getCate(content_categories);
		}
		if (switch_price != "") {
			url = url + "&switch_price=" + switch_price;
		}
		url = url + "&price_from=" + price_from + "&price_to=" + price_to + "&followers_count_min=" //
				+ followers_count_min + "&followers_count_max=" + followers_count_max;
		if (audience_gender_filter != "") {
			url = url + "&audience_gender_filter=" + audience_gender_filter;
		}
		if (page < 1) {
			url = url + "&page=1";
		} else {
			url = url + "&page=" + page;
		}

		return URIUtil.encodeQuery(url);
	}

}
