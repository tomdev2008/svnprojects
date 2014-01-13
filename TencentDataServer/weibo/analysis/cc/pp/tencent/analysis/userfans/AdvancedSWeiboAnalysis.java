package cc.pp.tencent.analysis.userfans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.oauthv1.OAuthV1;

public class AdvancedSWeiboAnalysis implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private final String[] token;

	/**
	 * 构造函数
	 * @param ip
	 */
	public AdvancedSWeiboAnalysis(String[] token) {
		this.token = token;
	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws Exception 
	 * @throws WeiboException
	 */
	public int[] analysis(String[] uids) throws Exception {

		/***************获取accesstoken*************/
		//			String[] token = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
		OAuthV1 oauth = new OAuthV1();
		OauthInit.oauthInit(oauth, token[0], token[1]);
		StatusesAPI userweibos = new StatusesAPI(oauth.getOauthVersion());
		/*****************变量初始化********************/
		int[] wbfenbubyhour = new int[24]; // 24小时内的微博发布分布		
		/****************采集用户微博信息****************/
		long createdat = 0; // 微博创建时间
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

		return wbfenbubyhour;
	}

}
