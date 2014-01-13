package cc.pp.sina.analysis.userweibo;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONArray;
import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.common.SourceType;
import cc.pp.sina.jdbc.UserWeiboJDBC;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.api.Users;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

/**
 * Title: 用户微博分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UWBAnalysis extends SourceType implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public UWBAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		UWBAnalysis uwa = new UWBAnalysis("192.168.1.151");
		long time = System.currentTimeMillis()/1000;
		uwa.analysis("3128226573");
		System.out.println(System.currentTimeMillis()/1000 - time);

	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	public void analysis(String uid) throws WeiboException, SQLException {

		String[] tworesult = new String[2];
		UserWeiboJDBC usermysql = new UserWeiboJDBC(ip);
//		int apicount = 0;
		if (usermysql.mysqlStatus()) 
		{
			/********获取accesstoken和用户username*********/
			String accesstoken = usermysql.getAccessToken();
			//String accesstoken = "2.00OKXevBdcZIJCee8ff5f68dtzKiEB";
			/*****************变量初始化********************/
			int weibosum = 0;   // 微博总数   // 平均每天、每周、每月的发博量
			float avewbbymonth = 0.0f;
			float avewbbyweek = 0.0f;
			float avewbbyday = 0.0f;
			int lastwbtimediff = 0;  // 最新的一条微博距当前的时间差（天）
			int favorited = 0;  // 被收藏数
			int original = 0;   // 原创微博数
			int piccount = 0;   // 含图片的微博数
			int aveorirepcom = 0;  // 原创中的转评数
			int allrepcount = 0;   // 总转发量
			int allcomcount = 0;   // 总评论量
//			float avereposted = 0.0f;  // 平均转发量
//			float avecommented = 0.0f;  // 平均评论量
			int[] wbgradebyrep = new int[5];  // 微博按转发量等级分布
			int[] wbgradebycom = new int[5];  // 微博按评论量等级分布
			String[] reptop10 = new String[10];  // 转发量前10的微博
			for (int i = 0; i < 10; i++) {
				reptop10[i] = "0=0";
			}
			String[] comtop10 = new String[10];  // 评论量前10的微博
			for (int i = 0; i < 10; i++) {
				comtop10[i] = "0=0";
			}
			int[] wbtimelinebyy = new int[5]; // 微博发布时间曲线（按年、,2009-2013）
			HashMap<String,Integer> wbtimelinebym = new HashMap<String,Integer>();  // 按月
			//HashMap<String,Integer> wbtimelinebyw = new HashMap<String,Integer>();  // 按周
			int[] wbfenbubyhour = new int[24]; // 24小时内的微博发布分布
			int[] wbsource = new int[6];  // 微博来源分布（PC、android、iphone、iPad、Symbian、others）			
			/*****************采集用户信息******************/
			Users um = new Users();
			um.client.setToken(accesstoken);
			User user = um.showUserById(uid);
//			apicount++;
			if (user.getStatusCode().equals("20003")) {
				tworesult[0] = "20003";
				tworesult[1] = "0";
				return;
			}
			weibosum = user.getStatusesCount();	
			String nickname = user.getScreenName();
			/*********************微博总数***********************/
			long usercreatedat = user.getCreatedAt().getTime()/1000;
			int diff = (int) ((System.currentTimeMillis()/1000 - usercreatedat)/86400);  // 用户存活时间（天）
			/****************采集用户微博信息****************/
			long createdat = 0;     // 微博创建时间
			String text = null;        // 微博内容
			String source = null;      // 微博来源
			int repcount = 0;   // 转发数
			int comcount = 0;  // 评论数
			SimpleDateFormat fo = null;
			String date = null;
			String year = null;
			String month = null;
			String hour = null;
			
			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			int page = 1;
			int flag = 0;
			while ((page*100 < weibosum + 100) && (page < 150)) 
			{
				StatusWapper status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
//				apicount++;
				page++;
				flag = 0;
				for (Status s : status.getStatuses())
				{
					flag++;
					text = s.getText();
					source = s.getSource().getName();
					repcount = s.getRepostsCount();
					comcount = s.getCommentsCount();					 
					createdat = s.getCreatedAt().getTime()/1000;
					fo = new SimpleDateFormat("yyyy-MM-dd,HH");
					date = fo.format(new Date(createdat*1000l));
					year = date.substring(0, 4);
					month = date.substring(5,7);
					hour = date.substring(11);
					if (hour.substring(0,1).equals("0")) {
						hour = hour.substring(1);
					} 
					/*************最新的一条微博距当前的时间差（天）*************/
					if ((page == 2) && (flag == 1)) {
						lastwbtimediff = (int) ((System.currentTimeMillis()/1000 - createdat)/86400);
					}
					/*********************被收藏数***********************/
					if (s.isFavorited()) {
						favorited++;
					}
					/********************原创微博数***********************/
					if (s.getRetweetedStatus() == null) {
						original++;
					}   
					/******************含图片的微博数**********************/
					/******************原创中的转评数**********************/
					if (s.getThumbnailPic().length() != 0) {
						piccount++;
						aveorirepcom += repcount + comcount;
					}
					/********************总转发量***********************/
					allrepcount += repcount; 
					/********************总评论量***********************/
					allcomcount += comcount;
					/*****************微博按转发量等级分布*******************/
					if (repcount < 10) {
						wbgradebyrep[0]++;   // X<=10
					} else if (repcount < 100) {
						wbgradebyrep[1]++;   // 10<X<=100
					} else if (repcount < 1000) {
						wbgradebyrep[2]++;   // 100<X<=1k
					} else if (repcount < 5000) {
						wbgradebyrep[3]++;   // 1k<X<=5k
					} else {
						wbgradebyrep[4]++;   // X>5k
					}
					/*****************微博按评论量等级分布*******************/
					if (comcount < 10) {
						wbgradebycom[0]++;   // X<=10
					} else if (comcount < 100) {
						wbgradebycom[1]++;   // 10<X<=100
					} else if (comcount < 500) {
						wbgradebycom[2]++;   // 100<X<=500
					} else if (comcount < 1000) {
						wbgradebycom[3]++;   // 500<X<=1k
					} else {
						wbgradebycom[4]++;   // X>1k
					}
					/*****************转发量前10的微博*********************/
					reptop10 = InsertSort.toptable(reptop10, text.replaceAll("=", "") + "=" + repcount); 
					/*****************评论量前10的微博*********************/
					comtop10 = InsertSort.toptable(comtop10, text.replaceAll("=", "") + "=" + comcount);
					/***************微博发布时间曲线(按年)*******************/
					wbtimelinebyy[Integer.parseInt(year) - 2009]++;
					/**********************按月*************************/
					if (wbtimelinebym.get(year+month) == null) {
						wbtimelinebym.put(year+month, 1);
					} else {
						wbtimelinebym.put(year+month, wbtimelinebym.get(year+month) + 1);
					}
					/**********************按周*************************/
					/***************24小时内的微博发布分布*******************/
					wbfenbubyhour[Integer.parseInt(hour)]++; 
					/********************微博来源分布**********************/
					wbsource[SourceType.getCategory(source)]++;
				} 
			}
			/************************数据整理**************************/
			// 微博总数weibosum
			/**************平均每天、每周、每月的发博量****************/
			avewbbymonth = weibosum / (diff / 30);
			avewbbyweek = weibosum / (diff / 7);
			avewbbyday = weibosum / diff;
			/*************最新的一条微博距当前的时间差（天）*************/
			// lastwbtimediff
			/*******************被收藏微博比例***********************/
			float favoritedratio = (float)favorited/weibosum;
			/********************原创微博比例***********************/
			float oriratio = (float)original/weibosum;
			/******************原创中的平均转评量*********************/ 
			float aveorirepcomratio = (float)aveorirepcom/original;
			/******************含图片的微博比例**********************/
			float picratio = piccount/weibosum;
			/********************平均转发量***********************/
			float avereposted = (float)allrepcount/weibosum;
			/********************平均评论量***********************/
			float avecommented = (float)allcomcount/weibosum;
			/*****************微博按转发量等级分布*******************/
			HashMap<String,String> wbgradebyrept = new HashMap<String,String>();
			wbgradebyrept.put("<10", Float.toString((float)Math.round(((float)wbgradebyrep[0]/weibosum)*10000)/100) + "%"); 
			wbgradebyrept.put("10~100", Float.toString((float)Math.round(((float)wbgradebyrep[1]/weibosum)*10000)/100) + "%"); 
			wbgradebyrept.put("100~1k", Float.toString((float)Math.round(((float)wbgradebyrep[2]/weibosum)*10000)/100) + "%"); 
			wbgradebyrept.put("1k~5k", Float.toString((float)Math.round(((float)wbgradebyrep[3]/weibosum)*10000)/100) + "%"); 
			wbgradebyrept.put(">5k", Float.toString((float)Math.round(((float)wbgradebyrep[4]/weibosum)*10000)/100) + "%"); 
			JSONArray wbgradebyrepr = JSONArray.fromObject(wbgradebyrept);
			/*****************微博按评论量等级分布*******************/
			HashMap<String,String> wbgradebycomt = new HashMap<String,String>();
			wbgradebycomt.put("<10", Float.toString((float)Math.round(((float)wbgradebycom[0]/weibosum)*10000)/100) + "%");
			wbgradebycomt.put("10~100", Float.toString((float)Math.round(((float)wbgradebycom[1]/weibosum)*10000)/100) + "%");
			wbgradebycomt.put("100~500", Float.toString((float)Math.round(((float)wbgradebycom[2]/weibosum)*10000)/100) + "%");
			wbgradebycomt.put("500~1k", Float.toString((float)Math.round(((float)wbgradebycom[3]/weibosum)*10000)/100) + "%");
			wbgradebycomt.put(">1k", Float.toString((float)Math.round(((float)wbgradebycom[4]/weibosum)*10000)/100) + "%");
			JSONArray wbgradebycomr = JSONArray.fromObject(wbgradebycomt);
			/*****************转发量前10的微博*********************/
			HashMap<String,String> reptop10t = new HashMap<String,String>();
			for (int k = 0; k < 10; k++) {
				if (reptop10[k].length() > 3) {
					reptop10t.put(Integer.toString(k), reptop10[k]);
				}
			}
			JSONArray reptop10r = JSONArray.fromObject(reptop10t);
			/*****************评论量前10的微博*********************/
			HashMap<String,String> comtop10t = new HashMap<String,String>();
			for (int k = 0; k < 10; k++) {
				if (comtop10[k].length() > 3) {
					comtop10t.put(Integer.toString(k), comtop10[k]);
				}
			}
			JSONArray comtop10r = JSONArray.fromObject(comtop10t);
			/***************微博发布时间曲线(按年)*******************/
			HashMap<String,String> wbtimelinebyyt = new HashMap<String,String>();
			for (int k = 0; k < 5; k++) {
				if (wbtimelinebyy[k] != 0) {
					wbtimelinebyyt.put(Integer.toString(k+2009), Float.toString((float)Math.round(((float)wbtimelinebyy[k]/weibosum)*10000)/100) + "%");
				}
			}
			JSONArray wbtimelinebyyr = JSONArray.fromObject(wbtimelinebyyt);
			/**********************按月*************************/
			JSONArray wbtimelinebymr = JSONArray.fromObject(wbtimelinebym);
			/**********************按周*************************/
			/***************24小时内的微博发布分布*******************/
			HashMap<String,String> wbfenbubyhourt = new HashMap<String,String>();
			for (int k = 0; k < 24; k++) {
				wbfenbubyhourt.put(Integer.toString(k), Float.toString((float)Math.round(((float)wbfenbubyhour[k]/weibosum)*10000)/100) + "%");
			}
			JSONArray wbfenbubyhourr = JSONArray.fromObject(wbfenbubyhourt);
			/********************微博来源分布**********************/
			HashMap<String,String> wbsourcet = new HashMap<String,String>(); 
			wbsourcet.put("Sina", Float.toString((float)Math.round(((float)wbsource[0]/weibosum)*10000)/100) + "%");
			wbsourcet.put("PC", Float.toString((float)Math.round(((float)wbsource[1]/weibosum)*10000)/100) + "%");
			wbsourcet.put("Android", Float.toString((float)Math.round(((float)wbsource[2]/weibosum)*10000)/100) + "%");
			wbsourcet.put("iPhone", Float.toString((float)Math.round(((float)wbsource[3]/weibosum)*10000)/100) + "%");
			wbsourcet.put("iPad", Float.toString((float)Math.round(((float)wbsource[4]/weibosum)*10000)/100) + "%");
			wbsourcet.put("Others", Float.toString((float)Math.round(((float)wbsource[5]/weibosum)*10000)/100) + "%");
			JSONArray wbsourcer = JSONArray.fromObject(wbsourcet);
			/*******************数据转换与存储*****************/
//			usermysql.insertUserWbResult(uid, nickname, weibosum, avewbbyday, avewbbyweek, avewbbymonth, 
//					lastwbtimediff, favoritedratio, picratio, oriratio, aveorirepcomratio, avereposted, avecommented, 
//					wbgradebyrepr.toString(), wbgradebycomr.toString(), reptop10r.toString(),
//					comtop10r.toString(), wbtimelinebyyr.toString(), wbtimelinebymr.toString(), 
//					wbfenbubyhourr.toString(), wbsourcer.toString());
			usermysql.insertUserWbResult(uid, nickname, weibosum, avewbbyday, avewbbyweek, avewbbymonth, 
					lastwbtimediff, favoritedratio, picratio, oriratio, aveorirepcomratio, avereposted, avecommented, 
					wbgradebyrepr.toString(), wbgradebycomr.toString(), "",
					"", wbtimelinebyyr.toString(), wbtimelinebymr.toString(), 
					wbfenbubyhourr.toString(), wbsourcer.toString());

			usermysql.sqlClose();
		}
	}

}

