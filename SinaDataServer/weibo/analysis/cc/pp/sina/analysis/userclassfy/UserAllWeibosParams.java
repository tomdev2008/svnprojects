package cc.pp.sina.analysis.userclassfy;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import cc.pp.sina.algorithms.PPSort;
import cc.pp.sina.analysis.userclassfy.dao.UserAllWeibosData;
import cc.pp.sina.common.SourceType;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

/**
 * 用户微博分析
 * 接口调用次数：10
 * @author Administrator
 *
 */
@SuppressWarnings("static-access")
public class UserAllWeibosParams extends SourceType implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		UserAllWeibosParams uwa = new UserAllWeibosParams();
		String accesstoken = "2.00vyKmPBdcZIJCab7e4cdb505dUSLC";
		UserAllWeibosData result = uwa.analysis("3279409634", accesstoken);
		System.out.println(result.getWeibosum());
		System.out.println(result.getOriratio());
		System.out.println(result.getAveorirepcom());
		System.out.println(result.getAverepcom());
		System.out.println(result.getWbsource());
		System.out.println(result.getAverepcombyweek());
		System.out.println(result.getAverepcombymonth());
		System.out.println(result.getLastweiboids());
	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public UserAllWeibosData analysis(String uid, String accesstoken) throws WeiboException, SQLException {

		/*****************变量初始化********************/
		long weibosum = 0; // 1、微博总数  
		int original = 0; // 2、原创微博数
		int allorirepcomcount = 0; // 3、原创总转评数
		int allrepcomcount = 0; // 4、总转评量
		HashMap<String, Integer> wbsources = new HashMap<String, Integer>(); // 5、微博来源分布（PC、android、iphone、iPad、Symbian、others）
		int[] lastweek = new int[2]; // 6、最近一周内的平均转评量：0---微博条数，1---总转评量
		int[] lastmonth = new int[2]; // 7、最近一月内的平均转评量：0---微博条数，1---总转评量
		HashMap<Integer, String> lastweiboids = new HashMap<Integer, String>(); // 8、最近100条微博id
		/****************采集用户微博信息****************/
		long nowtime = System.currentTimeMillis() / 1000;
		long createdat = 0; // 微博创建时间
		String source;
		int repcount, comcount, page = 1, index = 0;

		Timeline tm = new Timeline();
		tm.client.setToken(accesstoken);
		StatusWapper status = null;
		try {
			status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
		} catch (RuntimeException e) {
			return null;
		}
		weibosum = status.getTotalNumber();
		if (weibosum == 0) {
			return null;
		}
		while (page * 100 < weibosum + 100) {
			for (Status s : status.getStatuses())
			{
				source = s.getSource().getName();
				repcount = s.getRepostsCount();
				comcount = s.getCommentsCount();
				createdat = s.getCreatedAt().getTime() / 1000;
				/********************原创微博数***********************/
				if (s.getRetweetedStatus() == null) {
					original++;
				}
				/******************原创中的转评数**********************/
				if (s.getThumbnailPic().length() != 0) {
					allorirepcomcount += repcount + comcount;
				}
				/********************总转评量***********************/
				allrepcomcount += repcount + comcount;
				/********************微博来源分布********************/
				if (wbsources.get(source) == null) {
					wbsources.put(source, 1);
				} else {
					wbsources.put(source, wbsources.get(source) + 1);
				}
				/***************最近一周内的平均转评量*****************/
				if (createdat + 7 * 86400 > nowtime) {
					lastweek[0]++;
					lastweek[1] += repcount + comcount;
				}
				/***************最近一月内的平均转评量******************/
				if (createdat + 30 * 86400 > nowtime) {
					lastmonth[0]++;
					lastmonth[1] += repcount + comcount;
				}
				/*****************最近100条微博ids******************/
				if (index < 100)
					lastweiboids.put(index++, s.getId());
			}
			if (page > 9) { // 限制10次
				break;
			}
			page++;
			try {
				status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
			} catch (RuntimeException e) {
				break; // 出现错误时终止
			}
		}
		/************************数据整理**************************/
		// 微博总数 weibosum
		/********************原创微博比例***********************/
		float oriratio = (float) original / weibosum;
		/******************原创中的平均转评量*********************/
		float aveorirepcom = (float) allorirepcomcount / original;
		/********************平均转评量***********************/
		float averepcom = (float) allrepcomcount / weibosum;
		/********************微博来源分布**********************/
		String[] sortedwbsources = PPSort.sortedToStrings(wbsources);
		HashMap<String, Integer> wbsource = new HashMap<String, Integer>();
		int max = (wbsources.size() > 5) ? 5 : wbsources.size();
		for (int i = 0; i < max; i++) {
			wbsource.put(sortedwbsources[i].substring(0, sortedwbsources[i].indexOf("=")),
					Integer.parseInt(sortedwbsources[i].substring(sortedwbsources[i].indexOf("=") + 1)));
		}
		/***************最近一周内的平均转评量*****************/
		float averepcombyweek = 0.0f;
		if (lastweek[0] != 0) {
			averepcombyweek = (float) lastweek[1] / lastweek[0];
		}
		/***************最近一月内的平均转评量*****************/
		float averepcombymonth = 0.0f;
		if (lastmonth[0] != 0) {
			averepcombymonth = (float) lastmonth[1] / lastmonth[0];
		}
		/*******************数据转换与存储******************/
		UserAllWeibosData result = new UserAllWeibosData();
		result.setWeibosum(weibosum);
		result.setOriratio(oriratio);
		result.setAveorirepcom(aveorirepcom);
		result.setAverepcom(averepcom);
		result.setWbsource(wbsource);
		result.setAverepcombyweek(averepcombyweek);
		result.setAverepcombymonth(averepcombymonth);
		result.setLastweiboids(lastweiboids);

		return result;
	}

}

