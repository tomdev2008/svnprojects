package cc.pp.sina.require.ppdongli.single.weibo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sina.weibo.model.Status;
import com.sina.weibo.model.User;

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
		// TODO Auto-generated method stub
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
	 * 原创用户信息整理
	 */
	public void oriUserArrange(OriginalUserInfo oriuser, User user) {

		oriuser.setUsername(user.getId());
		oriuser.setNickname(user.getName());
		oriuser.setGender(user.getGender());
		oriuser.setProvince(user.getProvince());
		oriuser.setVerifiedtype(user.getVerifiedType());
		oriuser.setFanscount(user.getFollowersCount());
		oriuser.setWeibocount(user.getStatusesCount());
	}

	/**
	 * 原创微博信息整理
	 */
	public void oriwbArrange(OriginalWeiboInfo oriwb, Status weibo) {

		oriwb.setMid(weibo.getMid());
		oriwb.setCreatedat(weibo.getCreatedAt().getTime() / 1000);
		oriwb.setRepostcount(weibo.getRepostsCount());
		oriwb.setCommentcount(weibo.getCommentsCount());
	}

	/**
	 * 原创用户和微博信息
	 * @param oriwb
	 * @param weibo
	 */
	public void oriArrange(PPDLData ori, Status weibo) {

		ori.setUsername(weibo.getUser().getId());
		ori.setNickname(weibo.getUser().getName());
		ori.setGender(weibo.getUser().getGender());
		ori.setProvince(weibo.getUser().getProvince());
		ori.setVerifiedtype(weibo.getUser().getVerifiedType());
		ori.setFanscount(weibo.getUser().getFollowersCount());
		ori.setWeibocount(weibo.getUser().getStatusesCount());
		ori.setMid(weibo.getMid());
		ori.setCreatedat(weibo.getCreatedAt().getTime() / 1000);
		ori.setRepostcount(weibo.getRepostsCount());
		ori.setCommentcount(weibo.getCommentsCount());
	}

	/**
	 * 0转评时
	 * @param ori
	 * @param weibo
	 */
	public void oriZeroArrange(PPDLData ori, Status weibo) {

		ori.setUsername(weibo.getUser().getId());
		ori.setNickname(weibo.getUser().getName());
		ori.setGender(weibo.getUser().getGender());
		ori.setProvince(weibo.getUser().getProvince());
		ori.setVerifiedtype(weibo.getUser().getVerifiedType());
		ori.setFanscount(weibo.getUser().getFollowersCount());
		ori.setWeibocount(weibo.getUser().getStatusesCount());
		ori.setMid(weibo.getMid());
		ori.setCreatedat(weibo.getCreatedAt().getTime() / 1000);
		ori.setRepostcount(weibo.getRepostsCount());
		ori.setCommentcount(weibo.getCommentsCount());
	}

	/**
	 * 转发时间线结果整理（24小时）
	 */
	public void Reposttime24HArrange(RepostedInfo result, int[] reposttimelineby24H, int existwb) {

		for (int i = 0; i < 24; i++) {
			result.getReposttimelineby24H().put(Integer.toString(i), Float.toString((float)Math.round(((float)reposttimelineby24H[i]/existwb)*10000)/100) + "%");
		}
	}

	/**
	 * 性别分布结果整理
	 */
	public void genderArrange(RepostedInfo result, int[] gender, int existwb) {

		result.getGender().put("m", Float.toString((float)Math.round(((float)gender[0]/existwb)*10000)/100) + "%");
		result.getGender().put("f", Float.toString((float)Math.round(((float)gender[1]/existwb)*10000)/100) + "%");
	}

	/**
	 * 区域分布结果整理
	 */
	public void locationArrange(RepostedInfo result, int[] location, int existwb) {

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
	public void verifyArrange(RepostedInfo result, int[] verifiedtype, int existwb) {

		if (verifiedtype[13] != 0) {
			result.getVerifiedtype().put("other", Float.toString((float)Math.round(((float)verifiedtype[13]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[12] != 0) {
			result.getVerifiedtype().put("-1", Float.toString((float)Math.round(((float)verifiedtype[12]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[11] != 0) {
			result.getVerifiedtype().put("400", Float.toString((float)Math.round(((float)verifiedtype[11]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[10] != 0) {
			result.getVerifiedtype().put("220", Float.toString((float)Math.round(((float)verifiedtype[10]/existwb)*10000)/100) + "%");
		}
		if (verifiedtype[9] != 0) {
			result.getVerifiedtype().put("200", Float.toString((float)Math.round(((float)verifiedtype[9]/existwb)*10000)/100) + "%");
		}
		for (int n7 = 0; n7 < 9; n7++) {
			if (verifiedtype[n7] != 0) {
				result.getVerifiedtype().put(Integer.toString(n7), Float.toString((float)Math.round(((float)verifiedtype[n7]/existwb)*10000)/100) + "%");
			}
		}
	}

	/**
	 * 水军结果整理
	 */
	public void qualityArrange(RepostedInfo result, int[] reposterquality, int existwb) {

		result.getReposterquality().put("mask", Float.toString((float)Math.round(((float)reposterquality[0]/existwb)*10000)/100) + "%");
		result.getReposterquality().put("real", Float.toString((float)Math.round(((float)reposterquality[1]/existwb)*10000)/100) + "%");
	}

}
