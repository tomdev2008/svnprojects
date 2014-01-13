package cc.pp.weirenwu.core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import cc.pp.spider.sql.LocalJDBC;

/**
 * 链接：http://weirenwu.weibo.com
 * 帐号密码：nickyx@126.com、ppcc2011
 * @author
 */

public class WeiRenWuUtil {

	/**
	 * 主函数
	 */
	public static void main(String[] args) throws HttpException, IOException, SQLException {

		WeiRenWuUtil weiRenWuUtil = new WeiRenWuUtil();
		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "root");
		myjdbc.mysqlStatus();

		try {
			for (int page = 1; page <= 1003; page++) {
				System.out.println("Page: " + page);
				List<BozhuInfo> bozhuInfos = weiRenWuUtil.parserBozhuInfo(page);
				if (bozhuInfos != null) {
					for (BozhuInfo bozhuInfo : bozhuInfos) {
						myjdbc.inserWeiRenWu("weirenwu_zhifa", bozhuInfo);
						//						myjdbc.inserWeiRenWu("weirenwu_zhuanfa", bozhuInfo);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		myjdbc.sqlClose();

	}

	public List<BozhuInfo> parserBozhuInfo(int page) {

		List<BozhuInfo> result = new ArrayList<BozhuInfo>();
		//		String html = getZhuanfaHtml(page);
		String html = getZhifaHtml(page);
		if ("error".equals(html)) {
			System.out.println("Get html error!");
			return null;
		}
		Elements trs = Jsoup.parse(html).select("table.dsAttr_tbl").select("tbody").select("tr");
		for (int i = 1; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			BozhuInfo temp = new BozhuInfo();
			String username = tds.get(1).select("a").attr("href");
			temp.setUsername(username.substring(username.lastIndexOf("/") + 1));
			temp.setNickname(tds.get(1).text());
			temp.setInfluence("0");
			temp.setCate(tds.get(3).text());
			temp.setCanliprice(tds.get(4).select("span").text());
			temp.setFanscount(tds.get(5).text());
			temp.setJiedanratio(tds.get(6).text());
			result.add(temp);
		}

		return result;
	}

	/**
	 * 转发
	 */
	public String getZhuanfaHtml(int page) {

		/**
		 * 转发全部价格：http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser&p=
		 * 可选参数： http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser&ut=&pricetype=2
		 *         &minPrice=&maxPrice=&minFans=&maxFans=&isVerified=2&weiboclass=&showstars=0
		 *         &nick=%E8%BE%93%E5%85%A5%E5%BE%AE%E5%8D%9A%E6%98%B5%E7%A7%B0&sort=stars-
		 */

		try {
			HttpClient client = new HttpClient();

			String zhuanfa_url = "http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser&p=" + page;
			HttpMethod method = new GetMethod(zhuanfa_url);

			method.setRequestHeader("Host", "weirenwu.weibo.com");
			method.setRequestHeader("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			method.setRequestHeader("Content-Encoding", "gzip,deflate,sdch");
			method.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
			method.setRequestHeader(
					"Cookie",
					"SINAGLOBAL=6615351682994.515.1383097688740; Hm_lvt_3d143f0a07b6487f65609d8411e5464f=1383097689,1383115077,1383119735; "
							+ "myuid=3457840342; SSOLoginState=1384847494; wvr=5; _s_tentry=login.sina.com.cn; Apache=3097587162628.77.1384847492317; "
							+ "ULV=1384847492330:4:3:1:3097587162628.77.1384847492317:1384143109195; Hm_lvt_3d143f0a07b6487f65609d8411e5464f=1384143635,"
							+ "1384144976,1384399509,1384847491; Hm_lpvt_3d143f0a07b6487f65609d8411e5464f=1384947361; UOR=,,developer.51cto.com; "
							+ "USRHAWB=usrmdins21149; PHPSESSID=bn7e928qtnaiiv1namgun8jm47; SUE=es%3D92040d47d55d4e052e41da3f5cdc4bbd%26ev%3Dv1%26"
							+ "es2%3De584bad162d845ff886bc08a2b54c941%26rs0%3Df%252BaOlpYHP1sTlSI0HRm4XZL5vuPBA7gOMzjTa2kHKn89QV5wqlPTcj6i2Aor39L4"
							+ "FAIAShC%252F4dxPRLeqCtlxiEnCuwx4KjsRFfMS66hT%252BYnVNzEZJs0O1zKG5MqMkrqbkEiYQnClxAKbi134aIRkpc2EX4AGpWVND6V7t%252Fw"
							+ "tYDg%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1385023362%26et%3D1385109762%26d%3Dc909%26i%3D139c%26us%3D1%26vf%3D0%26vt%3D0"
							+ "%26ac%3D%26st%3D0%26uid%3D2807724022%26name%3Dnickyx%2540126.com%26nick%3D%25E7%259A%25AE%25E7%259A%25AE%25E5%258A%2"
							+ "5A8%25E5%258A%259B%26fmp%3D%26lcp%3D2012-05-19%252013%253A46%253A19; SUS=SID-2807724022-1385023362-XD-uh47x-845e6c0c"
							+ "83c822b4922d89a1c5e0a9b0; ALF=1387615311; WBStore=05e39925d85ca3ea|undefined");

			method.setRequestHeader("Connection", "keep-alive");
			int status = client.executeMethod(method);
			if (status == 200) {
				return new String(method.getResponseBody(), "utf-8");
			} else {
				return "error";
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "error";
	}

	/**
	 * 直发
	 */
	public String getZhifaHtml(int page) {

		/**
		 * 直发全部价格：http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser&p=
		 * 可选参数： http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser&ut=
		 *         &pricetype=1&minPrice=&maxPrice=&minFans=&maxFans=&isVerified=1&weiboclass=
		 *         &showstars=0&nick=%E8%BE%93%E5%85%A5%E5%BE%AE%E5%8D%9A%E6%98%B5%E7%A7%B0&sort=stars-
		 */

		try {
			HttpClient client = new HttpClient();

			String zhifa_url = "http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser&p=" + page;
			HttpMethod method = new GetMethod(zhifa_url);

			method.setRequestHeader("Host", "weirenwu.weibo.com");
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Cache-Control", "max-age=0");
			method.setRequestHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			method.setRequestHeader("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
			method.setRequestHeader("Content-Encoding", "gzip,deflate,sdch");
			method.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
			method.setRequestHeader("Referer", "http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser&p=2");
			method.setRequestHeader(
					"Cookie",
					"SINAGLOBAL=6615351682994.515.1383097688740; Hm_lvt_3d143f0a07b6487f65609d8411e5464f=1383097689,1383115077,1383119735; "
							+ "myuid=3457840342; SSOLoginState=1384847494; wvr=5; _s_tentry=login.sina.com.cn; Apache=3097587162628.77.1384847492317;"
							+ " ULV=1384847492330:4:3:1:3097587162628.77.1384847492317:1384143109195; Hm_lvt_3d143f0a07b6487f65609d8411e5464f=138414"
							+ "3635,1384144976,1384399509,1384847491; Hm_lpvt_3d143f0a07b6487f65609d8411e5464f=1384947361; UOR=,,developer.51cto.com;"
							+ " USRHAWB=usrmdins21149; PHPSESSID=bn7e928qtnaiiv1namgun8jm47; SUE=es%3D92040d47d55d4e052e41da3f5cdc4bbd%26ev%3Dv1%26es"
							+ "2%3De584bad162d845ff886bc08a2b54c941%26rs0%3Df%252BaOlpYHP1sTlSI0HRm4XZL5vuPBA7gOMzjTa2kHKn89QV5wqlPTcj6i2Aor39L4FAIA"
							+ "ShC%252F4dxPRLeqCtlxiEnCuwx4KjsRFfMS66hT%252BYnVNzEZJs0O1zKG5MqMkrqbkEiYQnClxAKbi134aIRkpc2EX4AGpWVND6V7t%252FwtYDg%25"
							+ "3D%26rv%3D0; SUP=cv%3D1%26bt%3D1385023362%26et%3D1385109762%26d%3Dc909%26i%3D139c%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D%"
							+ "26st%3D0%26uid%3D2807724022%26name%3Dnickyx%2540126.com%26nick%3D%25E7%259A%25AE%25E7%259A%25AE%25E5%258A%25A8%25E5%2"
							+ "58A%259B%26fmp%3D%26lcp%3D2012-05-19%252013%253A46%253A19; SUS=SID-2807724022-1385023362-XD-uh47x-845e6c0c83c822b4922"
							+ "d89a1c5e0a9b0; ALF=1387615311; WBStore=4c140d5dbadaf188|undefined");

			int status = client.executeMethod(method);
			if (status == 200) {
				return new String(method.getResponseBody(), "utf-8");
			} else {
				return "error";
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "error";
	}

}
