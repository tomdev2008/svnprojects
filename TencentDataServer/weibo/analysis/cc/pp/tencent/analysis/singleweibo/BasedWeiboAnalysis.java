package cc.pp.tencent.analysis.singleweibo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.sf.json.JSONArray;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.algorithms.InsertSort;
import cc.pp.tencent.jdbc.WeiboJDBC;
import cc.pp.tencent.result.BasedSingleWeiboResult;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * Title: 基础版单条微博分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class BasedWeiboAnalysis implements Serializable {

	/**
	 * 默认序列号版本
	 */
	private static final long serialVersionUID = 1L;

	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public BasedWeiboAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		BasedWeiboAnalysis wa = new BasedWeiboAnalysis("192.168.1.151");
		String url = new String("http://t.qq.com/p/t/194864057052448");
		String[] result = wa.analysis(url);
		System.out.println(result[0]);

	}

	/**
	 * 分析函数
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String[] analysis(String url) throws Exception {
		/**************************************/
		JSONArray jsonresult = null;
		WeiboJDBC weibomysql = new WeiboJDBC(this.ip);
		String[] tworesult = new String[2];
		int apicount = 0;
		if (weibomysql.mysqlStatus()) {
			BasedSingleWeiboResult result = new BasedSingleWeiboResult();
			/**********提取accesstoken*************/
			/**********************用户授权****************************/
			//		String[] token = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
			String[] token = WeiboUtils.getAccessToken(this.ip);
			OAuthV1 oauth = new OAuthV1();
			BasedWeiboAnalysis.oauthInit(oauth, token[0], token[1]);
			String wid = WeiboUtils.getWid(url);
			/**********************转发数据****************************/
			HashMap<String,Integer> reposttimelinebyDay = new HashMap<String,Integer>();  //3、转发时间线--按天
			int[] reposttimelineby24H = new int[24];  //按24小时内
			int[] location = new int[101];  //6、区域分布
			int[] viptype = new int[14];; //7、VIP类型
			int[] isself = new int[2] ; //9、是否自己发的微博
			int[] addVRatio = new int[3];  //10、加V比例,0-老用户，1-已实名认证，2-未实名认证
			//			int[] reposterquality = new int[2]; //11、转发用户质量（水军比例）
			int[] weibotype = new int[8]; //12、微博类型,1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
			//			int[] etype = new int[2]; //13、心情类型
			int[] weibostatus = new int[5]; //14、微博状态,0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
			String[] keyusersbyrep = new String[50]; //15、关键账号，按照转发量
			for (int i = 0; i < 50; i++) {
				keyusersbyrep[i] = "0=0";
			}
			String[] keyusersbycom = new String[50]; //15、关键账号,按评论量
			for (int i = 0; i < 50; i++) {
				keyusersbycom[i] = "0=0";
			}

			int existwb = 0;
			int wbsum = 0;
			@SuppressWarnings("unused")
			int comsum = 0;
			String name;
			String head;
			int rcount;
			int ccount;
			String rewid; //转发微博wid
			int self; //是否自己发的微博
			int wbtype; //微博类型,1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
			String citys;
			int city;  //区域信息
			@SuppressWarnings("unused")
			int emotiontype; //心情类型
			int addv;  //加V信息
			int isvip;  //VIP类型
			int wbstatus; //微博状态,0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
			long reposttime;  //转发时间
			SimpleDateFormat fo1;
			String hour;
			SimpleDateFormat fo2;
			String date;
			JsonNode weibodata;
			JsonNode jsoninfo;
			/**********************转发数据****************************/
			TAPI weibo = new TAPI(oauth.getOauthVersion());
			String weiboinfo = weibo.reList(oauth, "json", "0", wid, "1", "0", "20", "0");
			apicount++;
			weibodata = JsonUtils.getJsonNode(weiboinfo);
			if (!weibodata.get("errcode").toString().equals("0") || (weibodata.get("data") == null)) {  //返回错误信息处理
				tworesult[0] = "20003";
				tworesult[0] = "0";
				return tworesult;
			}
			jsoninfo = weibodata.get("data").get("info");
			if (jsoninfo == null) {
				tworesult[0] = "20003";
				tworesult[0] = "0";
				return tworesult;
			}
			//1、转发量
			wbsum = Integer.parseInt(weibodata.get("data").get("totalnum").toString());
			result.setRepostCount(weibodata.get("data").get("totalnum").toString());

			int cursor = 0;
			String lastreposttime = null;
			String lastwid = null;
			int index = 99;
			while ((cursor*100 < wbsum) && (apicount < 100)) 
			{
				jsoninfo = weibodata.get("data").get("info");
				if (jsoninfo == null) {
					break;
				}
				for (int i = 0; i < 100; i++)
				{	
					if (jsoninfo.get(i) == null) {
						index = i - 1;
						break;
					}
					existwb++;
					name = jsoninfo.get(i).get("nick").toString();
					name = name.substring(1, name.length()-1).replaceAll("\"", "\\\\\"");
					head = jsoninfo.get(i).get("head").toString();
					head = head.substring(1, head.length()-1);
					rcount = Integer.parseInt(jsoninfo.get(i).get("count").toString());
					ccount = Integer.parseInt(jsoninfo.get(i).get("mcount").toString());
					rewid = jsoninfo.get(i).get("id").toString();
					rewid = rewid.substring(1, rewid.length()-1);
					self = Integer.parseInt(jsoninfo.get(i).get("self").toString());
					wbtype = Integer.parseInt(jsoninfo.get(i).get("type").toString());
					citys = jsoninfo.get(i).get("province_code").toString();
					if (citys.length() == 2) {
						city = 0;
					} else {
						city = Integer.parseInt(citys.substring(1,citys.length()-1));
					}
					isvip = Integer.parseInt(jsoninfo.get(i).get("isvip").toString());
					wbstatus = Integer.parseInt(jsoninfo.get(i).get("status").toString());
					addv = Integer.parseInt(jsoninfo.get(i).get("isrealname").toString());
					emotiontype = Integer.parseInt(jsoninfo.get(i).get("emotiontype").toString());
					reposttime = Integer.parseInt(jsoninfo.get(i).get("timestamp").toString());
					fo1 = new SimpleDateFormat("HH");
					hour = fo1.format(new Date(reposttime*1000l));
					if (hour.substring(0,1).equals("0")) {
						hour = hour.substring(1);
					} 
					fo2 = new SimpleDateFormat("yyyy-MM-dd");
					date = fo2.format(new Date(reposttime*1000l));
					/**************************15、关键账号*********************************/
					keyusersbyrep = InsertSort.toptable(keyusersbyrep, name + "," + head + "=" + rcount);
					keyusersbycom = InsertSort.toptable(keyusersbycom, name + "," + head + "=" + ccount);
					/**************************3、转发时间线--按天*********************************/
					reposttimelineby24H[Integer.parseInt(hour)]++;
					if (reposttimelinebyDay.get(date) == null) {
						reposttimelinebyDay.put(date, 1);
					} else {
						reposttimelinebyDay.put(date, reposttimelinebyDay.get(date) + 1);
					}
					/**************************6、区域分布*********************************/
					if (city == 400) {
						location[0]++;
					} else {
						location[city]++;
					}
					/**************************7、VIP类型*********************************/
					viptype[isvip]++;;
					/************************9、是否自己发的微博******************************/
					isself[self]++; 
					/**************************10、加V比例*********************************/
					addVRatio[addv]++;
					/********************11、转发用户质量（水军比例）****************************/
					/**************************12、微博类型*********************************/
					weibotype[wbtype]++; //1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
					/**************************13、心情类型*********************************/
					/**************************14、微博状态*********************************/
					weibostatus[wbstatus]++; //0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
				}
				lastreposttime = jsoninfo.get(index).get("timestamp").toString();
				lastwid = jsoninfo.get(index).get("id").toString();
				weiboinfo = weibo.reList(oauth, "json", "0", wid, "1", lastreposttime, "100", lastwid);
				cursor++;
				apicount++;
				weibodata = JsonUtils.getJsonNode(weiboinfo);
				if (weibodata.get("data") == null) {  //返回错误信息处理
					break;
				}
			}
			/**********************评论数据****************************/
			weiboinfo = weibo.reList(oauth, "json", "1", wid, "1", "0", "100", "0");
			apicount++;
			weibodata = JsonUtils.getJsonNode(weiboinfo);
			//1、评论量
			if (weibodata.get("data").toString().equals("null")) {
				result.setCommentCount("0");;
			} else {
				comsum = Integer.parseInt(weibodata.get("data").get("totalnum").toString());
				result.setCommentCount(weibodata.get("data").get("totalnum").toString());
			}
			/**************************15、关键账号*********************************/
			for (int n = 0; n < 50; n++) {
				if (keyusersbyrep[n].length() > 5) {
					result.getKeyUsersByRep().put(Integer.toString(n), keyusersbyrep[n]);
				} else {
					break;
				}
			}
			for (int n = 0; n < 50; n++) {
				if (keyusersbycom[n].length() > 5) {
					result.getKeyUserByCom().put(Integer.toString(n), keyusersbycom[n]);
				} else {
					break;
				}
			}
			/**************************3、转发时间线--按天*********************************/
			for (int i = 0; i < 24; i++) {
				result.getReposttimelineBy24H().put(Integer.toString(i), Float.toString((float)Math.round(((float)reposttimelineby24H[i]/existwb)*10000)/100) + "%");
			}
			Set<String> keys = reposttimelinebyDay.keySet();
			Iterator<String> iterator = keys.iterator();
			int sum = 0;
			while (iterator.hasNext()) {
				sum += reposttimelinebyDay.get(iterator.next());
			}
			iterator = keys.iterator();
			String nextkey = null;
			while (iterator.hasNext()) {
				nextkey = iterator.next().toString();
				result.getReposttimelineByDay().put(nextkey, Float.toString((float)Math.round(((float)reposttimelinebyDay.get(nextkey)/sum)*10000)/100) + "%");
			}
			/**************************6、区域分布*********************************/
			if (location[0] != 0) {
				result.getLocation().put("400", Float.toString((float)Math.round(((float)location[0]/existwb)*10000)/100) + "%");
			}
			for (int j = 1; j < location.length; j++) {
				if (location[j] != 0) {
					result.getLocation().put(Integer.toString(j), Float.toString((float)Math.round(((float)location[j]/existwb)*10000)/100) + "%");
				}
			}
			/**************************7、VIP类型*********************************/
			result.getVipType().put("no", Float.toString((float)Math.round(((float)viptype[0]/existwb)*10000)/100) + "%");
			result.getVipType().put("yes", Float.toString((float)Math.round(((float)viptype[1]/existwb)*10000)/100) + "%");
			/************************9、是否自己发的微博******************************/
			result.getIsSelf().put("no", Float.toString((float)Math.round(((float)isself[0]/existwb)*10000)/100) + "%");
			result.getIsSelf().put("yes", Float.toString((float)Math.round(((float)isself[1]/existwb)*10000)/100) + "%");
			/**************************10、加V比例*********************************/
			result.getAddVRatio().put("old", Float.toString((float)Math.round(((float)addVRatio[0]/existwb)*10000)/100) + "%");
			result.getAddVRatio().put("yes", Float.toString((float)Math.round(((float)addVRatio[1]/existwb)*10000)/100) + "%");
			result.getAddVRatio().put("no", Float.toString((float)Math.round(((float)addVRatio[2]/existwb)*10000)/100) + "%");
			/********************11、转发用户质量（水军比例）****************************/
			/**************************12、微博类型*********************************/
			//1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
			for (int i = 0; i < weibotype.length; i++) {
				if (weibotype[i] != 0) {
					result.getWeiboType().put(Integer.toString(i), Float.toString((float)Math.round(((float)weibotype[i]/existwb)*10000)/100) + "%");
				}
			}
			/**************************13、心情类型*********************************/
			/**************************14、微博状态*********************************/
			//0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
			result.getWeiboStatus().put("normal",
					Float.toString((float) Math.round(((float) weibostatus[0] / existwb) * 10000) / 100) + "%");
			result.getWeiboStatus().put("sysdel",
					Float.toString((float) Math.round(((float) weibostatus[1] / existwb) * 10000) / 100) + "%");
			result.getWeiboStatus().put("verfy",
					Float.toString((float) Math.round(((float) weibostatus[2] / existwb) * 10000) / 100) + "%");
			result.getWeiboStatus().put("userdel",
					Float.toString((float) Math.round(((float) weibostatus[3] / existwb) * 10000) / 100) + "%");
			result.getWeiboStatus().put("rootdel",
					Float.toString((float) Math.round(((float) weibostatus[4] / existwb) * 10000) / 100) + "%");

			/***************数据转换与存储**************/
			jsonresult = JSONArray.fromObject(result);

			weibomysql.sqlClose();
		}
		tworesult[0] = jsonresult.toString();
		tworesult[1] = Integer.toString(apicount);

		return tworesult;
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
