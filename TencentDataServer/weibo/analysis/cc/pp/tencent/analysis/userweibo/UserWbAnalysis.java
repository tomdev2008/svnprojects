package cc.pp.tencent.analysis.userweibo;

import java.io.Serializable;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * Title: 用户微博分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UserWbAnalysis implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private int ip = 0;

	public UserWbAnalysis(int ip) {
		// TODO Auto-generated constructor stub
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws Exception 
	 * @throws WeiboException 
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		UserWbAnalysis uwa = new UserWbAnalysis(151);
		String[] token = {"2c656d405a1d4587b7b99d143102d002","b36e96d43ed75485258f0fdf0cefe143"};
		OAuthV1 oauth = new OAuthV1();
		UserWbAnalysis.oauthInit(oauth, token[0], token[1]);
		/**********************用户信息****************************/
		//获取该用户资料
		StatusesAPI userweibos = new StatusesAPI(oauth.getOauthVersion());
		String userwbs = userweibos.userTimeline(oauth, "json", "0", "0", "70", "0", "wghwd_9059", "", "3", "0");
		JsonNode jsondata = JsonUtils.getJsonNode(userwbs);
		//		System.out.println(userwbs);
//		System.out.println(jsondata);
		//		System.out.println(jsondata.get("data").get("totalnum"));
		System.out.println(jsondata.get("data").get("info").get(0).get("timestamp"));
		String lastwbtime = new String();
		String lastuid = new String();
		
	}

//	public String[] analysis(String uid) throws WeiboException, SQLException {
//
//		String[] tworesult = new String[2];
//		JSONArray jsonresult = null;
//		UserWbJDBC usermysql = new UserWbJDBC(ip);
//		int apicount = 0;
//		if (usermysql.mysqlStatus()) 
//		{
//			AnalysisResult result = new AnalysisResult();
//			/********获取accesstoken和用户username*********/
//			String accesstoken = usermysql.getAccessToken();
//			//String accesstoken = "2.00OKXevBdcZIJCee8ff5f68dtzKiEB";
//			/*****************变量初始化********************/
//			int weibosum = 0;   // 微博总数   // 平均每天、每周、每月的发博量
//			float avewbbymonth = 0.0f;
//			float avewbbyweek = 0.0f;
//			float avewbbyday = 0.0f;
//			int lastwbtimediff = 0;  // 最新的一条微博距当前的时间差（天）
//			int favorited = 0;  // 被收藏数
//			int original = 0;   // 原创微博数
//			int piccount = 0;   // 含图片的微博数
//			int aveorirepcom = 0;  // 原创中的转评数
//			int allrepcount = 0;   // 总转发量
//			int allcomcount = 0;   // 总评论量
//			float avereposted = 0.0f;  // 平均转发量
//			float avecommented = 0.0f;  // 平均评论量
//			int[] wbgradebyrep = new int[5];  // 微博按转发量等级分布
//			int[] wbgradebycom = new int[5];  // 微博按评论量等级分布
//			String[] reptop10 = new String[10];  // 转发量前10的微博
//			for (int i = 0; i < 10; i++) {
//				reptop10[i] = "0=0";
//			}
//			String[] comtop10 = new String[10];  // 评论量前10的微博
//			for (int i = 0; i < 10; i++) {
//				comtop10[i] = "0=0";
//			}
//			int[] wbtimelinebyy = new int[5]; // 微博发布时间曲线（按年、,2009-2013）
//			HashMap<String,Integer> wbtimelinebym = new HashMap<String,Integer>();  // 按月
//			//HashMap<String,Integer> wbtimelinebyw = new HashMap<String,Integer>();  // 按周
//			int[] wbfenbubyhour = new int[24]; // 24小时内的微博发布分布
//			int[] wbsource = new int[6];  // 微博来源分布（PC、android、iphone、iPad、Symbian、others）			
//			/*****************采集用户信息******************/
//			Users um = new Users();
//			um.client.setToken(accesstoken);
//			User user = um.showUserById(uid);
//			apicount++;
//			if (user.getStatusCode().equals("20003")) {
//				tworesult[0] = "20003";
//				tworesult[1] = "0";
//				return tworesult;
//			}
//			weibosum = user.getStatusesCount();	
//			/*********************微博总数***********************/
//			result.setWeiboCount(Long.toString(weibosum));
//			long usercreatedat = user.getCreatedAt().getTime()/1000;
//			int diff = (int) ((System.currentTimeMillis()/1000 - usercreatedat)/86400);  // 用户存活时间（天）
//			/****************采集用户微博信息****************/
//			long createdat = 0;     // 微博创建时间
//			String text = null;        // 微博内容
//			String source = null;      // 微博来源
//			int repcount = 0;   // 转发数
//			int comcount = 0;  // 评论数
//			SimpleDateFormat fo = null;
//			String date = null;
//			String year = null;
//			String month = null;
//			String hour = null;
//			
//			Timeline tm = new Timeline();
//			tm.client.setToken(accesstoken);
//			int page = 1;
//			int flag = 0;
//			while ((page*100 < weibosum + 100) && (page < 150)) 
//			{
//				StatusWapper status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
//				apicount++;
//				page++;
//				flag = 0;
//				for (Status s : status.getStatuses())
//				{
//					flag++;
//					text = s.getText();
//					source = s.getSource().getName();
//					repcount = s.getRepostsCount();
//					comcount = s.getCommentsCount();					 
//					createdat = s.getCreatedAt().getTime()/1000;
//					fo = new SimpleDateFormat("yyyy-MM-dd,HH");
//					date = fo.format(new Date(createdat*1000l));
//					year = date.substring(0, 4);
//					month = date.substring(5,7);
//					hour = date.substring(11);
//					if (hour.substring(0,1).equals("0")) {
//						hour = hour.substring(1);
//					} 
//					/*************最新的一条微博距当前的时间差（天）*************/
//					if ((page == 2) && (flag == 1)) {
//						lastwbtimediff = (int) ((System.currentTimeMillis()/1000 - createdat)/86400);
//					}
//					/*********************被收藏数***********************/
//					if (s.isFavorited()) {
//						favorited++;
//					}
//					/********************原创微博数***********************/
//					if (s.getRetweetedStatus() == null) {
//						original++;
//					}   
//					/******************含图片的微博数**********************/
//					/******************原创中的转评数**********************/
//					if (s.getThumbnailPic().length() != 0) {
//						piccount++;
//						aveorirepcom += repcount + comcount;
//					}
//					/********************总转发量***********************/
//					allrepcount += repcount; 
//					/********************总评论量***********************/
//					allcomcount += comcount;
//					/*****************微博按转发量等级分布*******************/
//					if (repcount < 10) {
//						wbgradebyrep[0]++;   // X<=10
//					} else if (repcount < 100) {
//						wbgradebyrep[1]++;   // 10<X<=100
//					} else if (repcount < 1000) {
//						wbgradebyrep[2]++;   // 100<X<=1k
//					} else if (repcount < 5000) {
//						wbgradebyrep[3]++;   // 1k<X<=5k
//					} else {
//						wbgradebyrep[4]++;   // X>5k
//					}
//					/*****************微博按评论量等级分布*******************/
//					if (comcount < 10) {
//						wbgradebycom[0]++;   // X<=10
//					} else if (comcount < 100) {
//						wbgradebycom[1]++;   // 10<X<=100
//					} else if (comcount < 500) {
//						wbgradebycom[2]++;   // 100<X<=500
//					} else if (comcount < 1000) {
//						wbgradebycom[3]++;   // 500<X<=1k
//					} else {
//						wbgradebycom[4]++;   // X>1k
//					}
//					/*****************转发量前10的微博*********************/
//					reptop10 = UserWbAnalysis.toptable(reptop10, text.replaceAll("=", "") + "=" + repcount); 
//					/*****************评论量前10的微博*********************/
//					comtop10 = UserWbAnalysis.toptable(comtop10, text.replaceAll("=", "") + "=" + comcount);
//					/***************微博发布时间曲线(按年)*******************/
//					wbtimelinebyy[Integer.parseInt(year) - 2009]++;
//					/**********************按月*************************/
//					if (wbtimelinebym.get(year+month) == null) {
//						wbtimelinebym.put(year+month, 1);
//					} else {
//						wbtimelinebym.put(year+month, wbtimelinebym.get(year+month) + 1);
//					}
//					/**********************按周*************************/
//					/***************24小时内的微博发布分布*******************/
//					wbfenbubyhour[Integer.parseInt(hour)]++; 
//					/********************微博来源分布**********************/
//					wbsource[SourceType.getCategory(source)]++;
//				} 
//			}
//			/************************数据整理**************************/
//			/**************平均每天、每周、每月的发博量****************/
//			avewbbymonth = weibosum / (diff / 30);
//			avewbbyweek = weibosum / (diff / 7);
//			avewbbyday = weibosum / diff;
//			result.getAverageWeibo().put("avewbbyday", Float.toString((float)Math.round(avewbbyday*10000)/100));
//			result.getAverageWeibo().put("avewbbyweek", Float.toString(Math.round(avewbbyweek)));
//			result.getAverageWeibo().put("avewbbymonth", Float.toString(Math.round(avewbbymonth)));
//			/*************最新的一条微博距当前的时间差（天）*************/
//			result.setLastWbTimeDiff(Integer.toString(lastwbtimediff));
//			/*******************被收藏微博比例***********************/
//			result.setFavoritedRatio(Float.toString((float)Math.round(((float)favorited/weibosum)*10000)/100) + "%");
//			/********************原创微博比例***********************/
//			result.setOriginalRatio(Float.toString((float)Math.round(((float)original/weibosum)*10000)/100) + "%");
//			/******************原创中的平均转评量*********************/ 
//			result.setAveOriginalRepCom(Float.toString((float)Math.round(((float)aveorirepcom/original)*1000)/100));
//			/******************含图片的微博比例**********************/
//			result.setPicRatio(Float.toString((float)Math.round(((float)piccount/weibosum)*10000)/100) + "%");
//			/********************平均转发量***********************/
//			result.setAveRepostedCount(Float.toString((float)Math.round(((float)allrepcount/weibosum)*10000)/100));
//			/********************平均评论量***********************/
//			result.setAveCommentedCount(Float.toString((float)Math.round(((float)allcomcount/weibosum)*10000)/100));
//			/*****************微博按转发量等级分布*******************/
//			result.getWbGradeByRep().put("<10", Float.toString((float)Math.round(((float)wbgradebyrep[0]/weibosum)*10000)/100) + "%"); 
//			result.getWbGradeByRep().put("10~100", Float.toString((float)Math.round(((float)wbgradebyrep[1]/weibosum)*10000)/100) + "%"); 
//			result.getWbGradeByRep().put("100~1k", Float.toString((float)Math.round(((float)wbgradebyrep[2]/weibosum)*10000)/100) + "%"); 
//			result.getWbGradeByRep().put("1k~5k", Float.toString((float)Math.round(((float)wbgradebyrep[3]/weibosum)*10000)/100) + "%"); 
//			result.getWbGradeByRep().put(">5k", Float.toString((float)Math.round(((float)wbgradebyrep[4]/weibosum)*10000)/100) + "%"); 
//			/*****************微博按评论量等级分布*******************/
//			result.getWbGradeByCom().put("<10", Float.toString((float)Math.round(((float)wbgradebycom[0]/weibosum)*10000)/100) + "%");
//			result.getWbGradeByCom().put("10~100", Float.toString((float)Math.round(((float)wbgradebycom[1]/weibosum)*10000)/100) + "%");
//			result.getWbGradeByCom().put("100~500", Float.toString((float)Math.round(((float)wbgradebycom[2]/weibosum)*10000)/100) + "%");
//			result.getWbGradeByCom().put("500~1k", Float.toString((float)Math.round(((float)wbgradebycom[3]/weibosum)*10000)/100) + "%");
//			result.getWbGradeByCom().put(">1k", Float.toString((float)Math.round(((float)wbgradebycom[4]/weibosum)*10000)/100) + "%");
//			/*****************转发量前10的微博*********************/
//			for (int k = 0; k < 10; k++) {
//				if (reptop10[k].length() > 3) {
//					result.getRepWbTop10().put(Integer.toString(k), reptop10[k]);
//				}
//			}
//			/*****************评论量前10的微博*********************/
//			for (int k = 0; k < 10; k++) {
//				if (comtop10[k].length() > 3) {
//					result.getComWbTop10().put(Integer.toString(k), comtop10[k]);
//				}
//			}
//			/***************微博发布时间曲线(按年)*******************/
//			for (int k = 0; k < 5; k++) {
//				if (wbtimelinebyy[k] != 0) {
//					result.getWbTimelineByYear().put(Integer.toString(k+2009), Float.toString((float)Math.round(((float)wbtimelinebyy[k]/weibosum)*10000)/100) + "%");
//				}
//			}
//			/**********************按月*************************/
//			result.setWbTimelineByMonth(wbtimelinebym);
//			/**********************按周*************************/
//			/***************24小时内的微博发布分布*******************/
//			for (int k = 0; k < 24; k++) {
//				result.getWbFenbuByHour().put(Integer.toString(k), Float.toString((float)Math.round(((float)wbfenbubyhour[k]/weibosum)*10000)/100) + "%");
//			}
//			/********************微博来源分布**********************/
//			result.getWbSource().put("Sina", Float.toString((float)Math.round(((float)wbsource[0]/weibosum)*10000)/100) + "%");
//			result.getWbSource().put("PC", Float.toString((float)Math.round(((float)wbsource[1]/weibosum)*10000)/100) + "%");
//			result.getWbSource().put("Android", Float.toString((float)Math.round(((float)wbsource[2]/weibosum)*10000)/100) + "%");
//			result.getWbSource().put("iPhone", Float.toString((float)Math.round(((float)wbsource[3]/weibosum)*10000)/100) + "%");
//			result.getWbSource().put("iPad", Float.toString((float)Math.round(((float)wbsource[4]/weibosum)*10000)/100) + "%");
//			result.getWbSource().put("Others", Float.toString((float)Math.round(((float)wbsource[5]/weibosum)*10000)/100) + "%");
//			/*******************数据转换与存储*****************/
//			jsonresult = JSONArray.fromObject(result);
////			usermysql.insertResult(uid, user.getFollowersCount(), jsonresult.toString());
//
//			usermysql.sqlClose();
//		}
//		tworesult[0] = jsonresult.toString();
//		tworesult[1] = Integer.toString(apicount);
//
//		return tworesult;
//	}
	
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
