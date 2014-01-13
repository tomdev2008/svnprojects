package cc.pp.sina.analysis.userclassfy;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.analysis.userclassfy.dao.UserAndFansData;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.UserWapper;
import com.sina.weibo.model.WeiboException;

/**
 * 用户及其粉丝分析
 * 接口调用次数：1+1 = 11
 * @author Administrator
 *
 */
@SuppressWarnings("static-access")
public class UserAndFansParams implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		UserAndFansParams ua = new UserAndFansParams();
		String accesstoken = "2.002214mBdcZIJC85be2bb2d9teRmOE";
		UserAndFansData result = ua.analysis("3279409634", accesstoken);
		System.out.println(result.getNickname());
		System.out.println(result.getDescription());
		System.out.println(result.getFanscount());
		System.out.println(result.getWeibocount());
		System.out.println(result.getAveragewbs());
		System.out.println(result.getInfluence());
		System.out.println(result.getActivation());
		System.out.println(result.getActivecount());
		System.out.println(result.getAddvratio());
		System.out.println(result.getActiveratio());
		System.out.println(result.getMaleratio());
		System.out.println(result.getFansexistedratio());
		System.out.println(result.getVerify());
		System.out.println(result.getAllfanscount());
		System.out.println(result.getAllactivefanscount());
		System.out.println(result.getTop5provinces());
	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public UserAndFansData analysis(String uid, String accesstoken) throws WeiboException, SQLException {

		/*****************采集用户信息******************/
		Users um = new Users();
		um.client.setToken(accesstoken);
		User user = um.showUserById(uid);
		if (user.getStatusCode().equals("20003")) {
			return null;
		}
		int verify = user.getVerifiedType();
		String nickname = user.getScreenName();
		String description = user.getDescription().replaceAll("\"", "");
		/*******************1、总粉丝数*******************/
		int userfanssum = user.getFollowersCount();
		/*******************0、总微博数*******************/
		int userweibosum = user.getStatusesCount();
		long usercreateday = 0;
		try {
			usercreateday = user.getCreatedAt().getTime() / 1000;
		} catch (RuntimeException e) {
			return null;
		}
		/******************中间变量******************/
		int existuids = 0, fanssum, weibosum, city;
		long allfanscount = 0, allactivefanscount = 0; // 所有粉丝的粉丝总数、所以活跃粉丝的粉丝总数
		int[] gender = new int[2];
		int[] fansclassfans = new int[5];
		int[] quality = new int[2];
		int[] verifiedratio = new int[2];
		int[] province = new int[101]; //16、区域分布
		String sex;
		boolean addv;
		/***************************采集用户粉丝信息***************************/
		Friendships fm = new Friendships();
		fm.client.setToken(accesstoken);
		int cursor = 0;
		UserWapper followers = null;
		try {
			followers = fm.getFollowersById(uid, 200, cursor * 200);
		} catch (RuntimeException e) {
			return null;
		}
		/****************循环计算******************/
		while (cursor * 200 < user.getFollowersCount() + 200) {
			for (User u : followers.getUsers())
			{
				/*****************1、粉丝存在数*****************/
				if (u.getName() != null) {
					existuids++;
				} else {
					continue;
				}
				sex = u.getGender();
				city = u.getProvince();
				fanssum = u.getFollowersCount();
				weibosum = u.getStatusesCount();
				addv = u.isVerified();
				allfanscount += fanssum;
				/******************3、性别分析******************/
				if (sex.equals("m")) {
					gender[0]++; //0----m
				} else {
					gender[1]++; //1----f
				}
				/************4、用户等级分析（按粉丝量）*************/
				if (fanssum < 100) {
					fansclassfans[0]++; //0-----"X<100"
				} else if (fanssum < 1000) {
					fansclassfans[1]++; //1-----"100<X<1000"
				} else if (fanssum < 10000) {
					fansclassfans[2]++; //2-----"1000<X<1w"
				} else if (fanssum < 100000) {
					fansclassfans[3]++; //3-----"1w<X<10w"
				} else {
					fansclassfans[4]++; //4-----"X>10w"
				}
				/******************5、认证比例******************/
				if (addv == false) {
					if (fanssum < 50 || weibosum < 20) {
						quality[0]++;
					} else if ((fanssum * 3 < weibosum) && (fanssum < 150)) {
						quality[0]++;
					} else {
						quality[1]++;
						allactivefanscount += fanssum;
					}
					verifiedratio[0]++;
				} else {
					quality[1]++;
					allactivefanscount += fanssum;
					verifiedratio[1]++;
				}
				/******************16、区域分析******************/
				if (city == 400) {
					province[0]++;
				} else {
					province[city]++;
				}
			}
			if (cursor < 9) { // 采集10次
				cursor++;
				followers = fm.getFollowersById(uid, 200, cursor * 200);
			} else {
				break;
			}
		}
		if (existuids == 0) {
			return null;
		}
		/*****************1、粉丝存在比例*****************/
		float fansexistedratio = 0.0f;
		if (user.getFollowersCount() > 2000) {
			fansexistedratio = (float) existuids / 2000;
		} else {
			fansexistedratio = (float) existuids / user.getFollowersCount();
		}
		/******************3、男性比例******************/
		float maleratio = (float) gender[0] / existuids;
		/**************16、区域分析*********************/
		HashMap<String, String> top5provinces = new HashMap<String, String>();
		String[] cityname = { "北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "上海", "江苏", "浙江", "安徽", "福建", "江西",
				"山东", "河南", "湖北", "湖南", "广东", "广西", "海南", "重庆", "四川", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆",
				"台湾", "香港", "澳门", "其他", "海外" };
		String[] citycode = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37",
				"41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
				"81", "82", "100", "0" };
		HashMap<String, String> citys = new HashMap<String, String>();
		for (int i = 0; i < cityname.length; i++) {
			citys.put(citycode[i], cityname[i]);
		}
		String[] citytable = new String[province.length];
		for (int i = 0; i < province.length; i++) {
			citytable[i] = "0=0";
		}
		for (int i = 0; i < province.length; i++) {
			citytable = InsertSort.toptable(citytable, i + "=" + province[i]);
		}
		int index1 = 0;
		for (int i = 0; i < province.length; i++) {
			if (index1 > 4)
				break;
			int t = Integer.parseInt(citytable[i].substring(0, citytable[i].indexOf("=")));
			if ((t < 100) && (t > 0)) {
				index1++;
				top5provinces.put(citys.get(Integer.toString(t)),
						Float.toString((float) Math.round(((float) province[t] / existuids) * 10000) / 100) + "%");
			}
		}
		/******************6、认证比例******************/
		float temp = (float) verifiedratio[1] / existuids;
		float addvratio = (float) verifiedratio[1] / existuids;
		/*************7、该用户的平均每天发博量**************/
		int usertimediff = 1;
		if (System.currentTimeMillis() / 1000 - usercreateday > 86400) {
			usertimediff = (int) ((System.currentTimeMillis() / 1000 - usercreateday) / 86400);
		}
		float avgweibo = (float) userweibosum / usertimediff;
		float averagewbs = (float) Math.round(avgweibo * 100) / 100;
		/***************8、该用户的活跃度*****************/
		float active = 0.00f;
		float alpha = 0.4f;
		float beta = 0.6f;
		active = 1 - alpha / (1 + (float) userfanssum / usertimediff) - beta
				/ (1 + (float) userweibosum / usertimediff);
		int activation = Math.round(Math.abs(active) * 10);
		/**************9、该用户的微博创建时间*************/
		// usertimediff
		/******************4、综合活跃比例******************/
		float activeratio = ((float) quality[1] / existuids) * fansexistedratio;
		/******************5、综合活跃数******************/
		int activenum = Math.round(activeratio * userfanssum);
		/*****************10、该用户的影响力***************/
		float inf = 0.00f, iflowersclass = 0.0f;
		if (user.getFollowersCount() > 2000) {
			iflowersclass = (float) (fansclassfans[2] + fansclassfans[3] + fansclassfans[4]) / 2000;
		} else {
			iflowersclass = (float) (fansclassfans[2] + fansclassfans[3] + fansclassfans[4]) / user.getFollowersCount();
		}
		// 影响力计算公式
		inf = (float) (0.8 * (Math.log(activenum) / Math.log(1.5)) + //
				0.1 * Math.abs(Math.log(iflowersclass) / Math.log(2)) + //
		0.1 * Math.abs(Math.log(temp) / Math.log(2)));
		if (inf > 100) {
			inf = 3;
		}
		int influence = Math.round(inf * 100 / 30);
		if (influence > 100)
			influence = 100;
		/***************14、所有粉丝的粉丝覆盖量**************/
		if (user.getFollowersCount() > 2000) {
			allfanscount = allfanscount * user.getFollowersCount() / existuids;
		}
		/***************15、活跃粉丝的粉丝覆盖量**************/
		if (user.getFollowersCount() > 2000) {
			allactivefanscount = allactivefanscount * user.getFollowersCount() / existuids;
		}
		/*******************数据转换与存储*****************/
		UserAndFansData result = new UserAndFansData();
		result.setUid(uid);
		result.setNickname(nickname);
		result.setDescription(description);
		result.setFanscount(userfanssum);
		result.setWeibocount(userweibosum);
		result.setVerify(verify);
		result.setAveragewbs(averagewbs);
		result.setInfluence(influence);
		result.setActivecount(activenum);
		result.setActivation(activation);
		result.setAddvratio(addvratio);
		result.setActiveratio(activeratio);
		result.setMaleratio(maleratio);
		result.setCreatedtime(usertimediff);
		result.setFansexistedratio(fansexistedratio);
		result.setAllfanscount(allfanscount);
		result.setAllactivefanscount(allactivefanscount);
		result.setTop5provinces(top5provinces);

		return result;
	}

}

