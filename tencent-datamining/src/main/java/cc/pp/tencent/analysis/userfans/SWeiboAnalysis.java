package cc.pp.tencent.analysis.userfans;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.UserWeiboJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

public class SWeiboAnalysis implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public SWeiboAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws Exception {

		SWeiboAnalysis swa = new SWeiboAnalysis("192.168.1.27");
		String[] uids = { "wghwd_9059" };
		int[] result = swa.analysis(uids);
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}

	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws Exception 
	 * @throws WeiboException
	 */
	public int[] analysis(String[] uids) throws Exception {

		UserWeiboJDBC usermysql = new UserWeiboJDBC(this.ip);
		if (usermysql.mysqlStatus()) {
			/***************获取accesstoken*************/
			String[] token = usermysql.getAccessTokenone();
			//			String[] token = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
			OAuthV1 oauth = new OAuthV1();
			SWeiboAnalysis.oauthInit(oauth, token[0], token[1]);
			StatusesAPI userweibos = new StatusesAPI(oauth.getOauthVersion());
			/*****************变量初始化********************/
			int[] wbfenbubyhour = new int[24]; // 24小时内的微博发布分布		
			/****************采集用户微博信息****************/
			long createdat = 0;     // 微博创建时间
			SimpleDateFormat fo = null;
			String hour;
			JsonNode jsondata;
			try {
				for (int i = 0; i < uids.length; i++) {
					if (uids[i] == null) {
						break;
					}
					String status = userweibos.userTimeline(oauth, "json", "0", "0", "70", "0", uids[i], "", "3", "0");
					jsondata = JsonUtils.getJsonNode(status);

					for (int j = 0; j < jsondata.get("data").get("info").size(); j++) {

						createdat = Long.parseLong(jsondata.get("data").get("info").get(j).get("timestamp").toString());
						fo = new SimpleDateFormat("HH");
						hour = fo.format(new Date(createdat * 1000l));
						if (hour.substring(0, 1).equals("0")) {
							hour = hour.substring(1);
						}
						/***************24小时内的微博发布分布*******************/
						wbfenbubyhour[Integer.parseInt(hour)]++;
					}
				}
			} catch (RuntimeException e) {
				//
			}

			usermysql.sqlClose();
			return wbfenbubyhour;
		} else {
			return null;
		}
	}

	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public static void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) {
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}
}
