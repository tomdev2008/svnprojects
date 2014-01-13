package cc.pp.tencent.analysis.userfans.interaction;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.HttpException;
import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.common.Emotion;
import cc.pp.tencent.jdbc.UfiJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * Title: 用户与粉丝交互数据分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class InteractionsAnalysis implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public InteractionsAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 * @throws WeiboException 
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws Exception {

		InteractionsAnalysis ia = new InteractionsAnalysis("192.168.1.27");
		ia.analysis();

	}
	
	/**
	 * 分析函数
	 * @throws Exception
	 */
	public void analysis() throws Exception {
		
		UfiJDBC myjdbc = new UfiJDBC(this.ip);
		if (myjdbc.mysqlStatus()) 
		{
		    /***************获取所有用户数据****************/
			HashMap<Integer,String> tencentusers = myjdbc.getTencentUsers();
			HashMap<String,String> t2users = myjdbc.getT2Users();
			GetAtInfo gai = new GetAtInfo();
			String[] tokens = new String[2];
			for (int i = 0; i < tencentusers.size(); i++) 
			{
				String username = tencentusers.get(i);
				/***************定义需要获取的变量****************/
			    int allcount = 0;
			    int[] emotions = new int[3];
			    HashMap<String,String> emotionratio = new HashMap<String,String>();
			    /***************获取用户的@数据值****************/
				int[] result = new int[4];
				if (t2users.get(username) != null) {
					tokens[0] = t2users.get(username).substring(0, t2users.get(username).indexOf(","));
					tokens[1] = t2users.get(username).substring(t2users.get(username).indexOf(",") + 1);
					result = gai.atInfoAnalysis(tokens);
					allcount += result[3];
					emotions[0] += result[0];
					emotions[1] += result[1];
					emotions[2] += result[2];
				}
				/********获取accesstoken和用户username*********/
			    UfiUtils ufiUtils = new UfiUtils();
			    String[] token = ufiUtils.getAccessToken(this.ip);
				OAuthV1 oauth = new OAuthV1();
				InteractionsAnalysis.oauthInit(oauth, token[0], token[1]);
				/*****************采集用户信息******************/
				UserAPI user = new UserAPI(oauth.getOauthVersion());
				String userinfo = user.otherInfo(oauth, "json", username, "");
				JsonNode userdata = JsonUtils.getJsonNode(userinfo);
				if (!userdata.get("errcode").toString().equals("0")) {
					emotionratio.put("negative", "0.0%");
					emotionratio.put("positive", "0.0%");
					emotionratio.put("neutral", "0.0%");
					JSONArray json = JSONArray.fromObject(emotionratio);
					myjdbc.insertUserIteractions1(username + ufiUtils.getTodayDate(), username, ufiUtils.getTodayDate(), 0, json.toString());
					continue;
				}
				
				StatusesAPI userwb = new StatusesAPI(oauth.getOauthVersion());
				String userwbinfo = userwb.userTimeline(oauth, "json", "0", "0", "5", "0", username, "", "0", "0");
				JsonNode userwbdata = JsonUtils.getJsonNode(userwbinfo);
				JsonNode info = userwbdata.get("data").get("info");
				
//				System.out.println(userwbdata);
				long t = 0;
				int count = 0;
				
				try {
					for (int j = 0; j < info.size(); j++) 
					{
						if (info.get(j) == null) {
							continue;
						}
						t = Integer.parseInt(info.get(j).get("timestamp").toString());
						if (t < (System.currentTimeMillis()/1000 - 86400)) {
							break;
						}
			
						count = Integer.parseInt(info.get(j).get("mcount").toString())
								+ Integer.parseInt(info.get(j).get("count").toString());
						if (count == 0) {
							continue;
						}
						
						allcount += count;
						String wid = info.get(j).get("id").toString();
						wid = wid.substring(1, wid.length()-1);
						TAPI weibo = new TAPI(oauth.getOauthVersion());
						String weiboinfo = weibo.reList(oauth, "json", "0", wid, "1", "0", "100", "0");
						JsonNode weibodata = JsonUtils.getJsonNode(weiboinfo);
						if (!weibodata.get("errcode").toString().equals("0") || (weibodata.get("data") == null)) {  //返回错误信息处理
							continue;
						}
						JsonNode jsoninfo = weibodata.get("data").get("info");
						if (jsoninfo == null) {
							continue;
						}
						
						Emotion emotion = new Emotion();
						JsonNode jsondata;
						String data;
						double label = 0.0;
						double score = 0.0;
						String text = new String();
						
						for (int k = 0; k < 100; k++)
						{
							if (jsoninfo.get(k) == null) {
								break;
							}
							
							text = jsoninfo.get(k).get("text").toString();
							data = emotion.getEmotion(text);
							jsondata = JsonUtils.getJsonNode(data);
							
							label = jsondata.get("label").getDoubleValue();
							score = jsondata.get("score").getDoubleValue();
							if (label < 0.5) {
								emotions[2]++;   // 0---负面
							} else if (label > 1.5) {
								emotions[1]++;   // 1---正面
							} else {
								if (score > 0.6) {
									emotions[1]++;  // 1---正面
								} else {
									emotions[2]++;  // 中性
								}
							}
						}
					}
				} catch (RuntimeException e) {
					
				}
				
				int[] remotions = ufiUtils.transData(emotions);
				int sum = remotions[0] + remotions[1] + remotions[2];
				if ((allcount != 0) && (sum == 0)) {
					remotions[1] = 1;
				}
				if (sum == 0) {
					sum = 1;
				}
				
				emotionratio.put("negative", Float.toString((float)Math.round(((float)remotions[0]/sum)*10000)/100) + "%");
				emotionratio.put("positive", Float.toString((float)Math.round(((float)remotions[1]/sum)*10000)/100) + "%");
				emotionratio.put("neutral", Float.toString((float)Math.round(((float)remotions[2]/sum)*10000)/100) + "%");
				
				JSONArray json = JSONArray.fromObject(emotionratio);
				myjdbc.insertUserIteractions1(username + ufiUtils.getTodayDate(), username, ufiUtils.getTodayDate(), allcount, json.toString());
			}
			
			myjdbc.sqlClose();
		}
		
	}

	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public static void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) 
	{
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}

}
