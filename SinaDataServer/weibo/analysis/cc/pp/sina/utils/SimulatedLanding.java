package cc.pp.sina.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;

/**
 * 抓取图书馆数据
 * @author Zhang Jinzheng
 *
 */
public class SimulatedLanding {

	private static String host = "183.60.92.251"; // Host
	private static String loginUrl = "/"; // 登陆页面
	private static String homePage = "/u/"; //首页信息


	private static HttpHost targetHost = new HttpHost(host, 8080, "http");
	
	private String number = null;
	private String passwd = null;
	private HttpResponse response = null;
	private DefaultHttpClient httpclient = null;

	/**
	 * 重载构造方法
	 * @param number
	 * @param passwd
	 */
	public SimulatedLanding(String number, String passwd) {
		this.number = number;
		this.passwd = passwd;
		try {
			this.login();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Get 获取Cookie
	 * @return
	 * @throws ClientProtocolException, IOException
	 */
	private String getCookie() {
		String cookie = null;
		HttpGet httpGet = new HttpGet(loginUrl);
		httpGet.getParams().setParameter("http.protocol.handle-redirects",false);	
		try {
			response = httpclient.execute(targetHost, httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.abort();
		}
		cookie = response.getFirstHeader("Set-Cookie").getValue();
		cookie = cookie.substring(0, cookie.indexOf(";"));
		
		return cookie;
	}

	/**
	 * Post 登陆
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public void login() throws ClientProtocolException, IOException {

		httpclient = new DefaultHttpClient();
		httpclient.setReuseStrategy((ConnectionReuseStrategy) new DefaultRedirectStrategy());

		HttpPost httpPost = new HttpPost(loginUrl);
		
		// 请求头需要Cookie
		httpPost.setHeader("Cookie", getCookie());	

		// All the parameters post to the web site
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("number", number));
		formparams.add(new BasicNameValuePair("passwd", passwd));
		formparams.add(new BasicNameValuePair("select", "cert_no"));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
			response = httpclient.execute(targetHost,httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpPost.abort();
		}
	}


	/**
	 * 首页信息
	 * @return
	 * @throws IOException
	 */
	String getHomePage(String uid) throws IOException {
		return getHtml(this.getHomeUrl(uid));
	}

	/**
	 * 获取网页 HTML
	 * @param redirectLocation
	 * @return
	 * @throws IOException
	 */
	private String getHtml(String url) throws IOException {

		String Html = "";
		HttpGet httpget = new HttpGet(url);
		HttpEntity entity = null;
		String rd = null;

		response = httpclient.execute(targetHost, httpget);// 建立连接，不能abort，abort有讲究
		entity = response.getEntity();

		if (entity != null) {
			InputStream instream = null;
			try {
				instream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader((instream), "UTF-8"));
				while ((rd = reader.readLine()) != null) {
					Html += rd + "\n";
				}
			} finally {
				instream.close();
				httpget.abort();
	            // 销毁响应实体
				//EntityUtils.consume(entity);
			}
		}
		return Html;
	}
	
	/**
	 * 首页的url
	 * @param uid
	 * @return
	 */
	public String getHomeUrl(String uid) {
		return homePage + 1862087393;
	}
	
	/**
	 * 当不再需要HttpClient实例时,关闭连接管理器以确保释放所有占用的系统资源
	 */
	public void shutdown() {
		 httpclient.getConnectionManager().shutdown();
	}

	/**
	 * 主函数 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		SimulatedLanding manager = new SimulatedLanding("wgxzy_1015@163.com", "wanggang19890821");
		String html = manager.getHomePage("1862087393");
		System.out.println(html);
	}
}
