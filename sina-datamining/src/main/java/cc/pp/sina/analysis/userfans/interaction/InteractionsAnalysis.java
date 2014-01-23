package cc.pp.sina.analysis.userfans.interaction;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.HttpException;
import org.codehaus.jackson.JsonNode;

import cc.pp.sina.common.Emotion;
import cc.pp.sina.jdbc.UfiJDBC;
import cc.pp.sina.utils.JsonUtil;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.api.Users;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

/**
 * Title: 用户与粉丝的交互数据分析
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

	public InteractionsAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException, HttpException, IOException {
		// 
		InteractionsAnalysis ia = new InteractionsAnalysis("192.168.1.27");
		ia.analysis();
		ia.moveResult();
	}

	/**
	 * 分析函数
	 * @throws SQLException
	 * @throws WeiboException
	 * @throws HttpException
	 * @throws IOException
	 */
	public void analysis() throws SQLException, WeiboException, HttpException, IOException {

		UfiJDBC myjdbc = new UfiJDBC(this.ip);
		if (myjdbc.mysqlStatus()) 
		{
			/***************获取所有用户数据****************/
			HashMap<Integer,String> sinausers = myjdbc.getSinaUsers();
			GetAtInfo gai = new GetAtInfo(this.ip);
			HashMap<String,String> t2users = gai.getT2Tokens();
			for (int i = 0; i < sinausers.size(); i++) 
			{
				String username = sinausers.get(i);
				/***************定义需要获取的变量****************/
				int allcount = 0;
				int[] emotions = new int[3];
				HashMap<String,String> emotionratio = new HashMap<String,String>();
				/***************获取用户的@数据值****************/
				int[] result = new int[4];
				if ((t2users.get(username) != null) && (!"no".equals(t2users.get(username)))) {
					result = gai.atInfoAnalysis(t2users.get(username));
					allcount += result[3];
					emotions[0] += result[0];
					emotions[1] += result[1];
					emotions[2] += result[2];
				}
				/********获取accesstoken和用户username*********/
				String accesstoken = myjdbc.getAccessToken();
				/*****************采集用户信息******************/
				Users um = new Users();
				um.client.setToken(accesstoken);
				User user = um.showUserById(username);
				UfiUtils ufiUtils = new UfiUtils();
				if (user.getStatusCode().equals("20003")) {
					emotionratio.put("negative", "0.0%");
					emotionratio.put("positive", "0.0%");
					emotionratio.put("neutral", "0.0%");
					JSONArray json = JSONArray.fromObject(emotionratio);
					myjdbc.insertUserIteractions1(username + ufiUtils.getTodayDate(), username, ufiUtils.getTodayDate(), 0, json.toString());
					i++;
					continue;
				}
				if (user.getStatus() == null) {
					user = um.showUserById(username);
				}
				if (user.getStatus() == null) {
					emotionratio.put("negative", "0.0%");
					emotionratio.put("positive", "0.0%");
					emotionratio.put("neutral", "0.0%");
					JSONArray json = JSONArray.fromObject(emotionratio);
					myjdbc.insertUserIteractions1(username + ufiUtils.getTodayDate(), username, ufiUtils.getTodayDate(), 0, json.toString());
					i++;
					continue;
				}

				Timeline usertm = new Timeline();
				usertm.client.setToken(accesstoken);
				StatusWapper userstatus = null;
				try {
					userstatus = usertm.getUserTimelineByUid(username, new Paging(1, 5), 0, 0);
				} catch (RuntimeException e) {}
				if (userstatus == null) {
					emotionratio.put("negative", "0.0%");
					emotionratio.put("positive", "0.0%");
					emotionratio.put("neutral", "0.0%");
					JSONArray json = JSONArray.fromObject(emotionratio);
					myjdbc.insertUserIteractions1(username + ufiUtils.getTodayDate(), username, ufiUtils.getTodayDate(), 0, json.toString());
					i++;
					continue;
				}
				long t = System.currentTimeMillis()/1000;
				for (Status userwb : userstatus.getStatuses()) 
				{
					if (userwb.getCreatedAt().getTime()/1000 < t - 86400) {
						break;
					}
					int num = userwb.getCommentsCount() + userwb.getRepostsCount();
					if (num == 0) {
						continue;
					} 
					allcount += num;
					String wid = userwb.getId();
					Timeline tm = new Timeline();
					tm.client.setToken(accesstoken);
					/*************变量初始化****************/
					int cursor = 1;
					Paging page = new Paging(cursor, 100);
					StatusWapper status = tm.getRepostTimeline(wid, page);
					Emotion emotion = new Emotion();
					JsonNode jsondata;
					String data;
					double label = 0.0;
					double score = 0.0;
					String text = new String();
					for (Status reposter : status.getStatuses())
					{
						text = reposter.getText();
						if (text.indexOf("//") < 2) {
							text = "OK";
						} else {
							text = text.substring(0, text.indexOf("//"));
						}
						data = emotion.getEmotion(text);
						jsondata = JsonUtil.getJsonNode(data);
						label = jsondata.get("label").getDoubleValue();
						score = jsondata.get("score").getDoubleValue();
						if (label < 0.5) {
							emotions[0]++;   // 0---负面
						} else if (label > 1.5) {
							emotions[1]++;   // 1---正面
						} else {
							if (score > 0.63) {
								emotions[1]++;  // 1---正面
							} else {
								emotions[2]++;  // 2---中性
							}
						}
					}
				}

				int[] remotions = ufiUtils.transData(emotions);
				int sum = remotions[0] + remotions[1] + remotions[2];
				if ((allcount !=  0) && (sum == 0)) {
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
	 * 转移结果数据
	 * @throws SQLException
	 */
	public void moveResult() throws SQLException {

		UfiJDBC myjdbc = new UfiJDBC(this.ip);
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		UfiUtils ufiUtils = new UfiUtils();
		if (myjdbc.mysqlStatus()) {
			result = myjdbc.getIteractions(ufiUtils.getTodayDate());
			myjdbc.sqlClose();
		}
		/******************插入数据***********************/
		String info = new String();
		String usernameday = new String();
		String allcount = new String();
		String emotionratio = new String();

		UfiJDBC jdbc1 = new UfiJDBC("192.168.1.154"); // 转移服务器
		if (jdbc1.mysqlStatus()) {
			for (int i = 0; i < result.size(); i++) {				
				info = result.get(i);
				emotionratio = info.substring(info.lastIndexOf("=") + 1);
				info = info.substring(0, info.lastIndexOf("="));
				allcount = info.substring(info.lastIndexOf("=") + 1);
				usernameday = info.substring(0, info.lastIndexOf("="));
				
				jdbc1.insertUserIteractions1(usernameday, usernameday.substring(0,usernameday.length()-8), 
						usernameday.substring(usernameday.length()-8), Integer.parseInt(allcount), emotionratio);
			
			}
			jdbc1.sqlClose();
		}
	}
	
	/**
	 * 转移结果数据
	 * @throws SQLException
	 */
	public void moveResultAll() throws SQLException {

		UfiJDBC myjdbc = new UfiJDBC(this.ip);
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		if (myjdbc.mysqlStatus()) {
			result = myjdbc.getAllIteractions();
			myjdbc.sqlClose();
		}
		/******************插入数据***********************/
		String info = new String();
		String usernameday = new String();
		String allcount = new String();
		String emotionratio = new String();

		UfiJDBC jdbc1 = new UfiJDBC("192.168.1.154"); // 转移服务器
		if (jdbc1.mysqlStatus()) {
			for (int i = 0; i < result.size(); i++) {				
				info = result.get(i);
				emotionratio = info.substring(info.lastIndexOf("=") + 1);
				info = info.substring(0, info.lastIndexOf("="));
				allcount = info.substring(info.lastIndexOf("=") + 1);
				usernameday = info.substring(0, info.lastIndexOf("="));
				jdbc1.insertUserIteractions1(usernameday, usernameday.substring(0,usernameday.length()-8), 
						usernameday.substring(usernameday.length()-8), Integer.parseInt(allcount), emotionratio);
			}
			jdbc1.sqlClose();
		}
	}


}
