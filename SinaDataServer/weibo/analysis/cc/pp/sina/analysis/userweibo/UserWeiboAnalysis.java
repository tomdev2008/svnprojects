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
import cc.pp.sina.result.UserWeiboResult;

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
public class UserWeiboAnalysis extends SourceType implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private final UserWeiboJDBC usermysql;

	/**
	 * 构造函数
	 * @param ip
	 */
	public UserWeiboAnalysis(String ip) {
		usermysql = new UserWeiboJDBC(ip);
		usermysql.mysqlStatus();
	}

	public UserWeiboAnalysis(String ip, String user, String password) {
		usermysql = new UserWeiboJDBC(ip, user, password);
		usermysql.mysqlStatus();
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		//		UserWeiboAnalysis uwa = new UserWeiboAnalysis("192.168.1.151");
		UserWeiboAnalysis uwa = new UserWeiboAnalysis("127.0.0.1", "root", "root");
		String[] result = uwa.analysis("1747752902");
		System.out.println(result[0]);
		System.out.println(result[1]);

	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public String[] analysis(String uid) throws WeiboException, SQLException {

		String[] tworesult = new String[2];
		JSONArray jsonresult = null;
		int apicount = 0;

		UserWeiboResult result = new UserWeiboResult();
		/********获取accesstoken和用户username*********/
		String accesstoken = usermysql.getAccessToken();
		//String accesstoken = "2.00OKXevBdcZIJCee8ff5f68dtzKiEB";
		/*****************变量初始化********************/
		int weibosum = 0; // 微博总数   // 平均每天、每周、每月的发博量
		float avewbbymonth = 0.0f, avewbbyweek = 0.0f, avewbbyday = 0.0f;
		int lastwbtimediff = 0; // 最新的一条微博距当前的时间差（天）
		int favorited = 0; // 被收藏数
		int original = 0; // 原创微博数
		int piccount = 0; // 含图片的微博数
		int aveorirepcom = 0; // 原创中的转评数
		int allrepcount = 0; // 总转发量
		int allcomcount = 0; // 总评论量
		//			float avereposted = 0.0f;  // 平均转发量
		//			float avecommented = 0.0f;  // 平均评论量
		int[] wbgradebyrep = new int[5]; // 微博按转发量等级分布
		int[] wbgradebycom = new int[5]; // 微博按评论量等级分布
		String[] reptop10 = new String[10]; // 转发量前10的微博
		for (int i = 0; i < 10; i++) {
			reptop10[i] = "0=0";
		}
		String[] comtop10 = new String[10]; // 评论量前10的微博
		for (int i = 0; i < 10; i++) {
			comtop10[i] = "0=0";
		}
		int[] wbtimelinebyy = new int[5]; // 微博发布时间曲线（按年、,2009-2013）
		HashMap<String, Integer> wbtimelinebym = new HashMap<String, Integer>(); // 按月
		//HashMap<String,Integer> wbtimelinebyw = new HashMap<String,Integer>();  // 按周
		int[] wbfenbubyhour = new int[24]; // 24小时内的微博发布分布
		int[] wbsource = new int[6]; // 微博来源分布（PC、android、iphone、iPad、Symbian、others）
		/*****************采集用户信息******************/
		Users um = new Users();
		um.client.setToken(accesstoken);
		User user = um.showUserById(uid);
		apicount++;
		if (user.getStatusCode().equals("20003")) {
			tworesult[0] = "20003";
			tworesult[1] = "0";
			return tworesult;
		}
		weibosum = user.getStatusesCount();
		/*********************微博总数***********************/
		result.setWeiboCount(Long.toString(weibosum));
		long usercreatedat = user.getCreatedAt().getTime() / 1000;
		int diff = (int) ((System.currentTimeMillis() / 1000 - usercreatedat) / 86400); // 用户存活时间（天）
		/****************采集用户微博信息****************/
		long createdat = 0; // 微博创建时间
		String text = null; // 微博内容
		String source = null; // 微博来源
		int repcount = 0; // 转发数
		int comcount = 0; // 评论数
		SimpleDateFormat fo = null;
		String date = null;
		String year = null;
		String month = null;
		String hour = null;

		Timeline tm = new Timeline();
		tm.client.setToken(accesstoken);
		int page = 1;
		int flag = 0;
		while ((page * 100 < weibosum + 100) && (page < 21)) {

			System.out.println(page);

			StatusWapper status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
			apicount++;
			page++;
			flag = 0;
			for (Status s : status.getStatuses())
			{
				flag++;
				text = s.getText();
				source = s.getSource().getName();
				repcount = s.getRepostsCount();
				comcount = s.getCommentsCount();
				createdat = s.getCreatedAt().getTime() / 1000;
				fo = new SimpleDateFormat("yyyy-MM-dd,HH");
				date = fo.format(new Date(createdat * 1000l));
				year = date.substring(0, 4);
				month = date.substring(5, 7);
				hour = date.substring(11);
				if (hour.substring(0, 1).equals("0")) {
					hour = hour.substring(1);
				}
				/*************最新的一条微博距当前的时间差（天）*************/
				if ((page == 2) && (flag == 1)) {
					lastwbtimediff = (int) ((System.currentTimeMillis() / 1000 - createdat) / 86400);
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
					wbgradebyrep[0]++; // X<=10
				} else if (repcount < 100) {
					wbgradebyrep[1]++; // 10<X<=100
				} else if (repcount < 1000) {
					wbgradebyrep[2]++; // 100<X<=1k
				} else if (repcount < 5000) {
					wbgradebyrep[3]++; // 1k<X<=5k
				} else {
					wbgradebyrep[4]++; // X>5k
				}
				/*****************微博按评论量等级分布*******************/
				if (comcount < 10) {
					wbgradebycom[0]++; // X<=10
				} else if (comcount < 100) {
					wbgradebycom[1]++; // 10<X<=100
				} else if (comcount < 500) {
					wbgradebycom[2]++; // 100<X<=500
				} else if (comcount < 1000) {
					wbgradebycom[3]++; // 500<X<=1k
				} else {
					wbgradebycom[4]++; // X>1k
				}
				/*****************转发量前10的微博*********************/
				reptop10 = InsertSort.toptable(reptop10, text.replaceAll("=", "") + "=" + repcount);
				/*****************评论量前10的微博*********************/
				comtop10 = InsertSort.toptable(comtop10, text.replaceAll("=", "") + "=" + comcount);
				/***************微博发布时间曲线(按年)*******************/
				wbtimelinebyy[Integer.parseInt(year) - 2009]++;
				/**********************按月*************************/
				if (wbtimelinebym.get(year + month) == null) {
					wbtimelinebym.put(year + month, 1);
				} else {
					wbtimelinebym.put(year + month, wbtimelinebym.get(year + month) + 1);
				}
				/**********************按周*************************/
				/***************24小时内的微博发布分布*******************/
				wbfenbubyhour[Integer.parseInt(hour)]++;
				/********************微博来源分布**********************/
				wbsource[SourceType.getCategory(source)]++;
			}
		}
		/************************数据整理**************************/
		/**************平均每天、每周、每月的发博量****************/
		avewbbymonth = weibosum / (diff / 30);
		avewbbyweek = weibosum / (diff / 7);
		avewbbyday = weibosum / diff;
		result.getAverageWeibo().put("avewbbyday", Float.toString((float) Math.round(avewbbyday * 10000) / 100));
		result.getAverageWeibo().put("avewbbyweek", Float.toString(Math.round(avewbbyweek)));
		result.getAverageWeibo().put("avewbbymonth", Float.toString(Math.round(avewbbymonth)));
		/*************最新的一条微博距当前的时间差（天）*************/
		result.setLastWbTimeDiff(Integer.toString(lastwbtimediff));
		/*******************被收藏微博比例***********************/
		result.setFavoritedRatio(Float.toString((float) Math.round(((float) favorited / weibosum) * 10000) / 100) + "%");
		/********************原创微博比例***********************/
		result.setOriginalRatio(Float.toString((float) Math.round(((float) original / weibosum) * 10000) / 100) + "%");
		/******************原创中的平均转评量*********************/
		result.setAveOriginalRepCom(Float.toString((float) Math.round(((float) aveorirepcom / original) * 1000) / 100));
		/******************含图片的微博比例**********************/
		result.setPicRatio(Float.toString((float) Math.round(((float) piccount / weibosum) * 10000) / 100) + "%");
		/********************平均转发量***********************/
		result.setAveRepostedCount(Float.toString((float) Math.round(((float) allrepcount / weibosum) * 10000) / 100));
		/********************平均评论量***********************/
		result.setAveCommentedCount(Float.toString((float) Math.round(((float) allcomcount / weibosum) * 10000) / 100));
		/*****************微博按转发量等级分布*******************/
		result.getWbGradeByRep().put("<10",
				Float.toString((float) Math.round(((float) wbgradebyrep[0] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByRep().put("10~100",
				Float.toString((float) Math.round(((float) wbgradebyrep[1] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByRep().put("100~1k",
				Float.toString((float) Math.round(((float) wbgradebyrep[2] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByRep().put("1k~5k",
				Float.toString((float) Math.round(((float) wbgradebyrep[3] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByRep().put(">5k",
				Float.toString((float) Math.round(((float) wbgradebyrep[4] / weibosum) * 10000) / 100) + "%");
		/*****************微博按评论量等级分布*******************/
		result.getWbGradeByCom().put("<10",
				Float.toString((float) Math.round(((float) wbgradebycom[0] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByCom().put("10~100",
				Float.toString((float) Math.round(((float) wbgradebycom[1] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByCom().put("100~500",
				Float.toString((float) Math.round(((float) wbgradebycom[2] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByCom().put("500~1k",
				Float.toString((float) Math.round(((float) wbgradebycom[3] / weibosum) * 10000) / 100) + "%");
		result.getWbGradeByCom().put(">1k",
				Float.toString((float) Math.round(((float) wbgradebycom[4] / weibosum) * 10000) / 100) + "%");
		/*****************转发量前10的微博*********************/
		for (int k = 0; k < 10; k++) {
			if (reptop10[k].length() > 3) {
				result.getRepWbTop10().put(Integer.toString(k), reptop10[k]);
			}
		}
		/*****************评论量前10的微博*********************/
		for (int k = 0; k < 10; k++) {
			if (comtop10[k].length() > 3) {
				result.getComWbTop10().put(Integer.toString(k), comtop10[k]);
			}
		}
		/***************微博发布时间曲线(按年)*******************/
		for (int k = 0; k < 5; k++) {
			if (wbtimelinebyy[k] != 0) {
				result.getWbTimelineByYear().put(Integer.toString(k + 2009),
						Float.toString((float) Math.round(((float) wbtimelinebyy[k] / weibosum) * 10000) / 100) + "%");
			}
		}
		/**********************按月*************************/
		result.setWbTimelineByMonth(wbtimelinebym);
		/**********************按周*************************/
		/***************24小时内的微博发布分布*******************/
		for (int k = 0; k < 24; k++) {
			result.getWbFenbuByHour().put(Integer.toString(k),
					Float.toString((float) Math.round(((float) wbfenbubyhour[k] / weibosum) * 10000) / 100) + "%");
		}
		/********************微博来源分布**********************/
		result.getWbSource().put("Sina",
				Float.toString((float) Math.round(((float) wbsource[0] / weibosum) * 10000) / 100) + "%");
		result.getWbSource().put("PC",
				Float.toString((float) Math.round(((float) wbsource[1] / weibosum) * 10000) / 100) + "%");
		result.getWbSource().put("Android",
				Float.toString((float) Math.round(((float) wbsource[2] / weibosum) * 10000) / 100) + "%");
		result.getWbSource().put("iPhone",
				Float.toString((float) Math.round(((float) wbsource[3] / weibosum) * 10000) / 100) + "%");
		result.getWbSource().put("iPad",
				Float.toString((float) Math.round(((float) wbsource[4] / weibosum) * 10000) / 100) + "%");
		result.getWbSource().put("Others",
				Float.toString((float) Math.round(((float) wbsource[5] / weibosum) * 10000) / 100) + "%");
		/*******************数据转换与存储*****************/
		jsonresult = JSONArray.fromObject(result);
		//			usermysql.insertResult(uid, user.getFollowersCount(), jsonresult.toString());

		tworesult[0] = jsonresult.toString();
		tworesult[1] = Integer.toString(apicount);

		return tworesult;
	}

	public void closeMysql() throws SQLException {
		usermysql.sqlClose();
	}

}
