package cc.pp.sina.analysis.single.weibo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cc.pp.sina.common.MidToId;
import cc.pp.sina.jdbc.WeiboJDBC;
import cc.pp.sina.result.BasedSingleWeiboResult;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

/**
 * 单条微博分析工具类
 * @author Administrator
 *
 */
public class WeiboUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		WeiboUtils weiboUtils = new WeiboUtils();
		String[] array = new String[3];
		weiboUtils.initStrArray(array);
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}

	/**
	 * 字符串数组初始化
	 */
	public void initStrArray(String[] array) {

		for (int i = 0; i < array.length; i++) {
			array[i] = "0=0";
		}
	}

	/**
	 * 性别判断
	 */
	public int checkSex(String sex) {

		if (sex.equals("m")) {
			return 0; //0----m
		} else {
			return 1; //1----f
		}
	}

	/**
	 * 城市编码判断
	 */
	public int checkCity(int city) {

		if (city == 400) {
			return 0;
		} else {
			return city;
		}
	}

	/**
	 * 认证编码判断
	 */
	public int checkVerifyType(int verifytype) {

		if ((verifytype < 9)&&(verifytype >= 0)) {
			return verifytype;
		} else if (verifytype == 200) {
			return 9;
		} else if (verifytype == 220) {
			return 10;
		} else if (verifytype == 400) {
			return 11;
		} else if (verifytype == -1) {
			return 12;
		} else {
			return 13;
		}
	}

	/**
	 * 加V判断
	 */
	public int checkAddV(boolean addv) {

		if (addv == false) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 水军判断
	 */
	public int checkQuality(boolean addv, int fanssum, int weibosum) {

		if (addv == false) {
			if (fanssum < 50 || weibosum < 20) {
				return 0;
			} else if ((fanssum*3 < weibosum) && (fanssum < 150)) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}

	/**
	 * 根据时间戳（秒）获取日期（年月日）
	 * @param time
	 * @return
	 */
	public String getDate(long time) {

		String result = new String();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		result = format.format(new Date(time*1000l));
		result = result.replaceAll("-", "");

		return result;
	}

	/**
	 * 根据时间戳（秒）获取小时
	 * @param time
	 * @return
	 */
	public String getHour(long time) {

		String result = new String();
		SimpleDateFormat format = new SimpleDateFormat("HH");
		result = format.format(new Date(time*1000l));
		if (result.substring(0,1).equals("0")) {
			result = result.substring(1);
		} 

		return result;
	}

	/**
	 * 转发用户等级判断（按粉丝量）
	 */
	public int checkGradeByFans(int fanssum) {

		if (fanssum < 100) {
			return 0;  //0-----"X<100"
		} else if (fanssum < 1000) {
			return 1;  //1-----"100<X<1000"
		} else if (fanssum < 10000) {
			return 2;  //2-----"1000<X<1w"
		} else if (fanssum < 100000) {
			return 3;  //3-----"1w<X<10w"
		} else {
			return 4;  //4-----"X>10w"
		}
	}

	/**
	 * 转发时间线（按天）
	 */
	public void putRepostByDay(HashMap<String,Integer> reposttimelinebyDay, String date) {

		if (reposttimelinebyDay.get(date) == null) {
			reposttimelinebyDay.put(date, 1);
		} else {
			reposttimelinebyDay.put(date, reposttimelinebyDay.get(date) + 1);
		}
	}

	/**
	 * 获取单条微博信息
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public Status getShowStatus(String ip, String url) throws WeiboException, SQLException {

		WeiboJDBC weibojdbc = new WeiboJDBC(ip);
		if (weibojdbc.mysqlStatus()) {
			String accesstoken = weibojdbc.getAccessToken();
			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			MidToId midtoid = new MidToId();
			Status wbinfo = tm.showStatus(midtoid.mid2id(url));
			weibojdbc.sqlClose();
			return wbinfo;
		} else {
			return null;
		}
	}

	/**
	 * 获取token信息
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public StatusWapper getReposts(String ip, String wid, int cursor) throws WeiboException, SQLException {

		WeiboJDBC weibojdbc = new WeiboJDBC(ip);
		if (weibojdbc.mysqlStatus()) {
			String accesstoken = weibojdbc.getAccessToken();
			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			StatusWapper status = tm.getRepostTimeline(wid, new Paging(cursor, 200));
			weibojdbc.sqlClose();
			return status;
		} else {
			return null;
		}
	}

	/**
	 * 转发时间线结果整理（24小时）
	 */
	public void Reposttime24HArrange(BasedSingleWeiboResult result, int[] reposttimelineby24H, int existwb) {

		for (int i = 0; i < 24; i++) {
			result.getReposttimelineBy24H().put(Integer.toString(i), Float.toString((float)Math.round(((float)reposttimelineby24H[i]/existwb)*10000)/100) + "%");
		}
	}

	/**
	 * 纯转发结果整理
	 */
	public void pureRepostArrange(BasedSingleWeiboResult result, int purerepcount, int existwb) {

		result.setPureRepostratio(Float.toString((float)Math.round(((float)purerepcount/existwb)*10000)/100) + "%");
	}

	/**
	 * 性别分布结果整理
	 */
	public void genderArrange(BasedSingleWeiboResult result, int[] gender, int existwb) {

		result.getGender().put("m", Float.toString((float)Math.round(((float)gender[0]/existwb)*10000)/100) + "%");
		result.getGender().put("f", Float.toString((float)Math.round(((float)gender[1]/existwb)*10000)/100) + "%");
	}

	/**
	 * 区域分布结果整理
	 */
	public void locationArrange(BasedSingleWeiboResult result, int[] location, int existwb) {

		if (location[0] != 0) {
			result.getLocation().put("400", Float.toString((float)Math.round(((float)location[0]/existwb)*10000)/100) + "%");
		}
		for (int j = 1; j < location.length; j++) {
			if (location[j] != 0) {
				result.getLocation().put(Integer.toString(j), Float.toString((float)Math.round(((float)location[j]/existwb)*10000)/100) + "%");
			}
		}
	}

	/**
	 * 认证分布结果整理
	 */
	public void verifyArrange(BasedSingleWeiboResult result, int[] verifiedtype, int existwb) {

		if (verifiedtype[13] != 0) {
			result.getVerifiedType().put("other", Float.toString((float)Math.round(((float)verifiedtype[13]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[12] != 0) {
			result.getVerifiedType().put("-1", Float.toString((float)Math.round(((float)verifiedtype[12]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[11] != 0) {
			result.getVerifiedType().put("400", Float.toString((float)Math.round(((float)verifiedtype[11]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[10] != 0) {
			result.getVerifiedType().put("220", Float.toString((float)Math.round(((float)verifiedtype[10]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[9] != 0) {
			result.getVerifiedType().put("200", Float.toString((float)Math.round(((float)verifiedtype[9]/existwb)*10000)/100) + "%");
		}
		for (int n7 = 0; n7 < 9; n7++) {
			if (verifiedtype[n7] != 0) {
				result.getVerifiedType().put(Integer.toString(n7), Float.toString((float)Math.round(((float)verifiedtype[n7]/existwb)*10000)/100) + "%");
			}
		}
	}

	/**
	 * 曝光量时间线整理（24小时）
	 */
	public void exposion24HArrange(BasedSingleWeiboResult result, int[] exposionby24H, int existwb) {

		for (int i = 0; i < 24; i++) {
			result.getExposionBy24H().put(Integer.toString(i), exposionby24H[i]);
		}
	}

	/**
	 * 水军结果整理
	 */
	public void qualityArrange(BasedSingleWeiboResult result, int[] reposterquality, int existwb) {

		result.getReposterQuality().put("mask", Float.toString((float)Math.round(((float)reposterquality[0]/existwb)*10000)/100) + "%");
		result.getReposterQuality().put("real", Float.toString((float)Math.round(((float)reposterquality[1]/existwb)*10000)/100) + "%");
	}

	/**
	 * 转发用户等级结果整理（按粉丝量）
	 */
	public void gradeByFansArrange(BasedSingleWeiboResult result, int[] repostergrade, int existwb) {

		result.getReposterGrade().put("<100", Float.toString((float)Math.round(((float)repostergrade[0]/existwb)*10000)/100) + "%");
		result.getReposterGrade().put("100~1000", Float.toString((float)Math.round(((float)repostergrade[1]/existwb)*10000)/100) + "%");
		result.getReposterGrade().put("1000~1w", Float.toString((float)Math.round(((float)repostergrade[2]/existwb)*10000)/100) + "%");
		result.getReposterGrade().put("1w~10w", Float.toString((float)Math.round(((float)repostergrade[3]/existwb)*10000)/100) + "%");
		result.getReposterGrade().put(">10w", Float.toString((float)Math.round(((float)repostergrade[4]/existwb)*10000)/100) + "%");
	}

	/**
	 * 关键账号结果整理
	 */
	public void keyusersArrange(BasedSingleWeiboResult result, String[] keyusers) {

		for (int n = 0; n < 50; n++) {
			if (keyusers[n].length() > 5) {
				result.getKeyUsers().put(Integer.toString(n), keyusers[n]);
			} else {
				break;
			}
		}
	}

	/**
	 * 评论信息的情感值结果整理
	 */
	public void comemotionArrange(BasedSingleWeiboResult result, int[] commentemotion, int comsum) {

		result.getCommentionEmotion().put("negative", Float.toString((float)Math.round(((float)commentemotion[0]/comsum)*10000)/100) + "%");
		result.getCommentionEmotion().put("positive", Float.toString((float)Math.round(((float)commentemotion[1]/comsum)*10000)/100) + "%");
		result.getCommentionEmotion().put("neutral", Float.toString((float)Math.round(((float)(comsum-commentemotion[0]-commentemotion[1])/comsum)*10000)/100) + "%");
	}

	/**
	 * 负面评论信息结果的整理(选取20个)
	 */
	public void negcomArrange(BasedSingleWeiboResult result, String[] negativecomments) {

		for (int n = 0; n < 20; n++) {
			if (negativecomments[n].length() > 5) {
				result.getNegativeComments().put(Integer.toString(n), negativecomments[n].substring(0, negativecomments[n].lastIndexOf("=")));
			} else {
				break;
			}
		}
	}

}
