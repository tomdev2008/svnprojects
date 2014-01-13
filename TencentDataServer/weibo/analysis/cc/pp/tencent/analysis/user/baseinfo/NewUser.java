package cc.pp.tencent.analysis.user.baseinfo;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.UbiaJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * Title: 对于新用户或者没有结果的用户处理类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class NewUser {
	
	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public NewUser(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 * @throws JSONException
	 * @throws WeiboException
	 */
	public static void main(String[] args) throws Exception {

		NewUser nu = new NewUser("192.168.1.27");
		String username = new String("1862087393");
		String[] result = nu.getInfo(username);
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}

	}
	
	/**
	 * 获取用户信息
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String[] getInfo(String username) throws Exception {
		
		String[] info = new String[4];
		UbiaJDBC usermysql = new UbiaJDBC(this.ip);
		if (usermysql.mysqlStatus()) 
		{
			/********获取accesstoken和用户username*********/
			String[] accesstoken = usermysql.getAccessToken();
			/*****************采集用户信息******************/
			OAuthV1 oauth = new OAuthV1();
			NewUser.oauthInit(oauth, accesstoken[0], accesstoken[1]);
			UserAPI user = new UserAPI(oauth.getOauthVersion());
			String userinfo = user.otherInfo(oauth, "json", username, "");
			JsonNode userdata = JsonUtils.getJsonNode(userinfo);
			if (!userdata.get("errcode").toString().equals("0")) {  //返回错误信息处理
				return null;
			}
			int followerscount = Integer.parseInt(userdata.get("data").get("fansnum").toString());
			int friendscount = Integer.parseInt(userdata.get("data").get("idolnum").toString());
//			int statusescount = Integer.parseInt(userdata.get("data").get("tweetnum").toString());
			/*****************插入到总用户列表******************/
			usermysql.insertTencentUsers(username);
			/***************插入到今日数据结果列表****************/
			UbiaUtils ubiaUtils = new UbiaUtils();
			usermysql.insertTodayResult("tencentuserinforesult", username + ubiaUtils.getTodayDate(), 
					ubiaUtils.getTodayDate(), followerscount, friendscount, 0, 0, "0.0%", "0.0%");
			
			usermysql.sqlClose();
		}
		
		return info;
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
