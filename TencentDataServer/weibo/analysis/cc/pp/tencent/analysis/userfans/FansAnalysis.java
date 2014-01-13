package cc.pp.tencent.analysis.userfans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.algorithms.InsertSort;
import cc.pp.tencent.common.SourceType;
import cc.pp.tencent.jdbc.UserJDBC;
import cc.pp.tencent.result.FansResult;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

public class FansAnalysis implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private final int limit = 100;

	private final UserJDBC usermysql;

	private final SWeiboAnalysis swa;

	public FansAnalysis(String ip) {
		usermysql = new UserJDBC(ip);
		usermysql.mysqlStatus();
		swa = new SWeiboAnalysis(ip);
	}

	public FansAnalysis(String ip, String user, String password) {
		usermysql = new UserJDBC(ip, user, password);
		usermysql.mysqlStatus();
		swa = new SWeiboAnalysis(ip, user, password);
	}

	public void closeMysql() throws SQLException {
		usermysql.sqlClose();
		swa.closeMysql();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//
		//		UserJDBC mysql = new UserJDBC(172);
		//		if (mysql.mysqlStatus()) {
		//			FansAnalysis ua = new FansAnalysis(172);
		//			HashMap<String, String> uids = mysql.getUnAnalyzedUsers();
		//			if (uids != null) {
		//				for (Entry<String, String> temp : uids.entrySet()) {
		//					int index = ua.analysis(temp.getKey());
		//					if (index > 0) {
		//						mysql.updateTencentUser(temp.getKey());
		//					}
		//				}
		//			}
		//			mysql.sqlClose();
		//		}
		//		FansAnalysis ua = new FansAnalysis("192.168.1.27");
		FansAnalysis ua = new FansAnalysis("127.0.0.1", "root", "root");
		int index = ua.analysis("painys");
		System.out.println(index);
		ua.closeMysql();
	}

	/**
	 * @分析函数
	 * @param token
	 * @throws Exception
	 */
	public int analysis(String uid) throws Exception {

		int apicount = 0;

		FansUtils fansUtils = new FansUtils();
		FansResult result = new FansResult();
		/**********************用户授权****************************/
		String[] token = usermysql.getAccessToken();
		//		String[] token = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
		OAuthV1 oauth = new OAuthV1();
		BasedUserAnalysis.oauthInit(oauth, token[0], token[1]);
		/**********************用户信息****************************/
		//获取该用户资料
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String userinfo = user.otherInfo(oauth, "json", uid, "");
		apicount++;
		JsonNode userdata = JsonUtils.getJsonNode(userinfo);

		//		System.out.println(userdata);

		if (!userdata.get("errcode").toString().equals("0")) { //返回错误信息处理
			return -1;
		}

		int userfanssum = Integer.parseInt(userdata.get("data").get("fansnum").toString());
		/*********************1、总粉丝数***********************/
		result.setFanssum(Long.toString(userfanssum));
		int maxcount = userfanssum < this.limit ? userfanssum : this.limit;
		/**********************粉丝信息****************************/
		int existuids = 0;
		int[] wbsource = new int[6]; // 终端设备分布，或者微博来源分布
		int[] province = new int[101]; // 9、区域分析
		int[] ishead = new int[2];
		int[] gender = new int[3]; // 8、性别分析
		int[] gradebyfans = new int[5]; // 6、粉丝等级分布（按粉丝量）
		int[] age = new int[5]; // 7、年龄分析
		int[] quality = new int[2]; // 10、水军分析
		int[] addvratio = new int[2]; // 12、认证比例
		int[] verifiedtype = new int[2]; // 认证分布，或者是否为VIP
		HashMap<String, Integer> hottags = new HashMap<String, Integer>(); // 5、粉丝的热门标签
		String[] top40fans = new String[40]; // top40粉丝，按照粉丝量
		fansUtils.initTopArray(top40fans);
		String[] top10uids = new String[10]; // 存放待分析的用户名，用户分析这些用户的发博时间线
		// 13、粉丝活跃时间分析
		String name, citys, headurl, source, nextstartpos = "0";
		int city, sex, fanssum, addv, isvip, cursor = 0, curnum; // isvip是否为名人用户,cursor页码
		JsonNode jsondata, jsoninfo, tags;

		FriendsAPI userfriends = new FriendsAPI(oauth.getOauthVersion());

		while (cursor * 30 < maxcount) {

			//				System.out.println(cursor);

			String baseinfo = userfriends.userFanslist(oauth, "json", "30", nextstartpos, uid, "", "1", "0");
			apicount++;
			cursor++;
			jsondata = JsonUtils.getJsonNode(baseinfo);
			jsoninfo = null;
			curnum = 0;
			try {
				nextstartpos = jsondata.get("data").get("nextstartpos").toString();
				jsoninfo = jsondata.get("data").get("info");
				curnum = Integer.parseInt(jsondata.get("data").get("curnum").toString());
			} catch (RuntimeException e) {
				cursor = maxcount;
				return -1;
			}

			for (int i = 0; i < curnum; i++) {
				/*****************0、粉丝存在数*****************/
				if (jsoninfo.get(i) != null) {
					existuids++;
				} else {
					continue;
				}
				//exp：经验值，level：微博等级
				headurl = jsoninfo.get(i).get("head").toString();
				headurl = headurl.substring(1, headurl.length() - 1);
				sex = Integer.parseInt(jsoninfo.get(i).get("sex").toString());
				citys = jsoninfo.get(i).get("province_code").toString();
				if (citys.length() == 2) {
					city = 0;
				} else {
					city = Integer.parseInt(citys.substring(1, citys.length() - 1));
				}
				name = jsoninfo.get(i).get("name").toString().replaceAll("\"", "");
				fanssum = Integer.parseInt(jsoninfo.get(i).get("fansnum").toString());
				isvip = Integer.parseInt(jsoninfo.get(i).get("isvip").toString());
				addv = Integer.parseInt(jsoninfo.get(i).get("isrealname").toString()); //
				source = jsoninfo.get(i).get("tweet").get(0).get("from").toString();
				tags = jsoninfo.get(i).get("tag");
				/******************3、微博来源******************/
				wbsource[SourceType.getCategory(source)]++;
				/******************4、认证分布******************/
				verifiedtype[isvip]++; //0-不是，1-是
				/******************5、热门标签******************/
				fansUtils.tackleTags(hottags, tags);
				/************6、用户等级分析（按粉丝量）*************/
				gradebyfans[fansUtils.checkFansGrade(fanssum)]++;
				/******************7、年龄分析******************/
				age[fansUtils.checkAge(fanssum)]++;
				/******************8、性别分析******************/
				if (sex < 3)
					gender[sex]++; //1-男，2-女，0-未填写
				/******************9、区域分析******************/
				province[fansUtils.checkLocation(city)]++;
				/******************10、水军分析******************/
				quality[fansUtils.checkQuality(fanssum)]++;
				/****************存放待分析的用户名****************/
				//top10uids = InsertSort.toptable(top10uids, name + "=" + fanssum);
				/***********11、Top40粉丝UID（按粉丝量）************/
				top40fans = InsertSort.toptable(top40fans, name + "=" + fanssum);
				/******************12、认证比例******************/
				addvratio[fansUtils.checkVerified(addv)]++;

				/******************2、头像分析******************/
				if (headurl == null) {
					ishead[0]++;
				} else {
					ishead[1]++;
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			top10uids[i] = top40fans[i];
		}
		/*********************数据整理******************************/
		/****************2、活跃粉丝数******************/
		fansUtils.activeCountArrange(result, quality, userfanssum, existuids);
		/******************3、微博来源******************/
		fansUtils.sourceArrange(result, wbsource, existuids);
		/******************4、认证分布******************/
		fansUtils.verifiedTypeArrange(result, verifiedtype, existuids);
		/******************5、热门标签******************/
		fansUtils.tagsArrange(result, hottags);
		/************6、用户等级分析（按粉丝量）*************/
		fansUtils.fansGradeArrange(result, gradebyfans, existuids);
		/******************7、年龄分析******************/
		fansUtils.ageArrange(result, age, existuids);
		/******************8、性别分析******************/
		fansUtils.genderArrange(result, gender);
		/******************9、区域分析******************/
		fansUtils.locationArrange(result, province, existuids);
		/******************10、水军分析******************/
		fansUtils.qualityArrange(result, quality, existuids);
		/***********11、Top40粉丝UID（按粉丝量）************/
		fansUtils.topFansArrange(result, top40fans);
		/******************12、认证比例******************/
		result.setAddVRatio(Float.toString((float) Math.round(((float) addvratio[1] / existuids) * 10000) / 100) + "%");
		/***************13、粉丝活跃时间线分析**************/
		result.setActivetimeline(this.getSwbAnalysis(top10uids));
		/****************14、粉丝增长趋势*****************/
		result.setFansaddtimeline(usermysql.getFansAddInfo(uid));
		/*******************数据转换与存储*****************/
		JSONArray jsonresult = JSONArray.fromObject(result);

		//			System.out.println(jsonresult);
		/**
		 * 数据存储到154服务器上
		 */
		UserJDBC mysql = new UserJDBC("192.168.1.154");
		if (mysql.mysqlStatus()) {
			mysql.insertFansResult(uid, jsonresult.toString());
			mysql.sqlClose();
		}

		return apicount;
	}

	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public static void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) {
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}

	/**
	 * 获取粉丝的24小时的活跃时间线
	 * @param top10uids
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public HashMap<String, String> getSwbAnalysis(String[] top10uids) throws Exception {

		String[] uids = InsertSort.trans(top10uids);
		int[] wbfenbubyhour = swa.analysis(uids); // 24小时内的微博发布分布

		int sum = 0;
		for (int i = 0; i < wbfenbubyhour.length; i++) {
			sum += wbfenbubyhour[i];
		}
		HashMap<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < wbfenbubyhour.length; i++) {
			result.put(Integer.toString(i),
					Float.toString((float) Math.round(((float) wbfenbubyhour[i] / sum) * 10000) / 100) + "%");
		}

		return result;
	}

}
