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
			for (int page = 1; page <= 1057; page++) {
				System.out.println("Page: " + page);
				List<BozhuInfo> bozhuInfos = weiRenWuUtil.parserBozhuInfo(page);
				if (bozhuInfos != null) {
					for (BozhuInfo bozhuInfo : bozhuInfos) {
						//						myjdbc.inserWeiRenWu("weirenwu_zhifa", bozhuInfo);
						myjdbc.inserWeiRenWu("weirenwu_zhuanfa", bozhuInfo);
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
		String html = getZhuanfaHtml(page);
		//		String html = getZhifaHtml(page);
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
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
			method.setRequestHeader("Connection", "keep-alive");
			method.setRequestHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			method.setRequestHeader("Content-Encoding", "gzip,deflate,sdch");
			method.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			method.setRequestHeader("Referer", "http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser");
			method.setRequestHeader(
					"Cookie",
					"SINAGLOBAL=6615351682994.515.1383097688740; Hm_lvt_3d143f0a07b6487f65609d8411e5464f=1383097689,1383115077,"
							+ "1383119735; Hm_lvt_3d143f0a07b6487f65609d8411e5464f=1384143635,1384144976,1384399509,1384847491; _s_tentry"
							+ "=www.techsiteanalytics.com; Apache=5281812825705.856.1389607307706; ULV=1389607308262:11:2:1:5281812825705"
							+ ".856.1389607307706:1388654932404; login_sid_t=3f5c50e0e4323bbd28ff050b7092b88e; PHPSESSID=atd1cvo2nhmnpt1"
							+ "fp6vk7dn815; USRHAWB=usrmdins21250; myuid=1862087393; SUB=AemZEg0d7%2BVzVTis1FH2Or4a6KacLaq4KcBS2Y89qVHv8N"
							+ "I6QVZv%2FXvsCPBCJfYID66Do7uic9GChcDRAwlei4i2NJdr2WgpPvPJ44TGRv7T; UOR=,,login.sina.com.cn; SUE=es%3D1c371b"
							+ "145cac9bfa61529578e3d35af0%26ev%3Dv1%26es2%3Df57f57e39422e09548eaf0fea3afb1a5%26rs0%3Dq6%252FzKjDljqpjpwar"
							+ "GNrMl2XdNt4J3hO2ZzHV0QUY4k5ArcS6uJLiiAcaZbzFuee5ASlo0DGmfSiSDaRdUlsF7ZuxFhKoiPZKTz9h%252BiWGWOWlVk3NoS2eSVW"
							+ "7IYSvd9SucpPV2dHef7z8M6xPnCTuQStw%252BXBn8dHN%252Bs%252FqsA0aBDA%253D%26rv%3D0; SUP=cv%3D1%26bt%3D138995878"
							+ "7%26et%3D1390045187%26d%3Dc909%26i%3D3df6%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D27%26st%3D0%26uid%3D2807724022%"
							+ "26name%3Dnickyx%2540126.com%26nick%3D%25E7%259A%25AE%25E7%259A%25AE%25E5%258A%25A8%25E5%258A%259B%26fmp%3D%2"
							+ "6lcp%3D2012-05-19%252013%253A46%253A19; SUS=SID-2807724022-1389958787-XD-hrq6p-9bd75b707963127eae3670a3eea0a"
							+ "9b0; ALF=1392550787; SSOLoginState=1389958787; un=nickyx@126.com; wvr=5; WBStore=8d05b2d45893c0dd|undefined");

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
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
			method.setRequestHeader("Content-Encoding", "gzip,deflate,sdch");
			method.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			method.setRequestHeader("Referer", "http://weirenwu.weibo.com/taskv2/index.php?c=task.addseluser");
			method.setRequestHeader(
					"Cookie",
					"SINAGLOBAL=6615351682994.515.1383097688740; Hm_lvt_3d143f0a07b6487f65609d8411e5464f=1383097689,1383115077,1383119735;"
							+ " Hm_lvt_3d143f0a07b6487f65609d8411e5464f=1384143635,1384144976,1384399509,1384847491; _s_tentry=www.techsiteana"
							+ "lytics.com; Apache=5281812825705.856.1389607307706; ULV=1389607308262:11:2:1:5281812825705.856.1389607307706:"
							+ "1388654932404; login_sid_t=3f5c50e0e4323bbd28ff050b7092b88e; PHPSESSID=atd1cvo2nhmnpt1fp6vk7dn815; USRHAWB=usr"
							+ "mdins21250; myuid=1862087393; SUB=AemZEg0d7%2BVzVTis1FH2Or4a6KacLaq4KcBS2Y89qVHv8NI6QVZv%2FXvsCPBCJfYID66Do7u"
							+ "ic9GChcDRAwlei4i2NJdr2WgpPvPJ44TGRv7T; UOR=,,login.sina.com.cn; SUE=es%3D1c371b145cac9bfa61529578e3d35af0%26ev"
							+ "%3Dv1%26es2%3Df57f57e39422e09548eaf0fea3afb1a5%26rs0%3Dq6%252FzKjDljqpjpwarGNrMl2XdNt4J3hO2ZzHV0QUY4k5ArcS6uJL"
							+ "iiAcaZbzFuee5ASlo0DGmfSiSDaRdUlsF7ZuxFhKoiPZKTz9h%252BiWGWOWlVk3NoS2eSVW7IYSvd9SucpPV2dHef7z8M6xPnCTuQStw%252B"
							+ "XBn8dHN%252Bs%252FqsA0aBDA%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1389958787%26et%3D1390045187%26d%3Dc909%26i%3D3df6%"
							+ "26us%3D1%26vf%3D0%26vt%3D0%26ac%3D27%26st%3D0%26uid%3D2807724022%26name%3Dnickyx%2540126.com%26nick%3D%25E7%25"
							+ "9A%25AE%25E7%259A%25AE%25E5%258A%25A8%25E5%258A%259B%26fmp%3D%26lcp%3D2012-05-19%252013%253A46%253A19; SUS=SID-"
							+ "2807724022-1389958787-XD-hrq6p-9bd75b707963127eae3670a3eea0a9b0; ALF=1392550787; SSOLoginState=1389958787; un="
							+ "nickyx@126.com; WBStore=8d05b2d45893c0dd|undefined");

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
