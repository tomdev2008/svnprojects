package cc.pp.tencent.analysis.single.weibo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.httpclient.URIException;
import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.algorithms.InsertSort;
import cc.pp.tencent.algorithms.PPSort;
import cc.pp.tencent.analysis.userfans.interaction.UfiUtils;
import cc.pp.tencent.common.Emotion;
import cc.pp.tencent.result.SimpleWeiboResult;
import cc.pp.tencent.utils.JsonUtils;
import cc.pp.tencent.utils.WordSegment;


public class SWeiboUtils {

	/**
	 * @获取wid
	 * @param url
	 * @return
	 */
	public String getWid(String url) {
		//http://t.qq.com/p/t/197562129592354
		String wid = url.substring(20);

		return wid;
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
	 * 水军判断
	 */
	public int checkfans(int fanssum) {

		if (fanssum < 100) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * @throws URIException 
	 * 
	 */
	public void setEmotions(Emotion emotion, int[] emotions, String text) throws URIException {

		if (text.contains("转发微博")) {
			emotions[2]++; // 2---中性
		} else {
			String data = emotion.getEmotion(text);
			JsonNode jsondata = JsonUtils.getJsonNode(data);
			double label = jsondata.get("label").getDoubleValue();
			double score = jsondata.get("score").getDoubleValue();
			if (label < 0.5) {
				emotions[0]++; // 0---负面
			} else if (label > 1.5) {
				emotions[1]++; // 1---正面
			} else {
				if (score > 0.63) {
					emotions[1]++; // 1---正面
				} else {
					emotions[2]++; // 2---中性
				}
			}
		}
	}

	/**
	 * 根据时间戳（秒）获取日期（年月日时分秒）
	 * @param time
	 * @return
	 */
	public String getTime(long time) {

		String result = new String();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result = format.format(new Date(time*1000l));

		return result;
	}

	/**
	 * 根据时间戳（秒）获取日期（年月日）
	 * @param time
	 * @return
	 */
	public String getDate(long time) {

		String result = new String();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		result = format.format(new Date(time * 1000l));
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
	 * 转发时间线（按天）
	 */
	public void putRepostByDay(HashMap<String, Integer> reposttimelinebyDay, String date) {

		if (reposttimelinebyDay.get(date) == null) {
			reposttimelinebyDay.put(date, 1);
		} else {
			reposttimelinebyDay.put(date, reposttimelinebyDay.get(date) + 1);
		}
	}

	/**
	 * 转发层级数
	 * @param suminclass
	 * @param text
	 */
	public void putSumInClass(HashMap<String, Integer> suminclass, String text) {

		int i = 0, index;
		String info = text;
		while ((info != null) && (index = info.indexOf("||")) > 0) {
			i++;
			info = info.substring(index + 1);
		}
		if (suminclass.get(Integer.toString(i)) == null) {
			suminclass.put(Integer.toString(i), 1);
		} else {
			suminclass.put(Integer.toString(i), suminclass.get(Integer.toString(i)) + 1);
		}
	}

	/**
	 * 认证编码判断
	 */
	public int checkVerifyType(int verifytype) {

		if ((verifytype < 9) && (verifytype >= 0)) {
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
	 * 原创用户信息整理
	 * @param result
	 * @param wbinfo
	 */
	public void originalUserArrange(SimpleWeiboResult result, JsonNode userdata) {

		result.getOriginaluserinfos().put("username", userdata.get("name").toString().replaceAll("\"", ""));
		result.getOriginaluserinfos().put("nickname", userdata.get("nick").toString().replaceAll("\"", ""));
		result.getOriginaluserinfos().put("head", userdata.get("head").toString().replaceAll("\"", "") + "/180");
		result.getOriginaluserinfos().put("weibotime", userdata.get("timestamp").toString().replaceAll("\"", ""));
	}

	/**
	 * 微博类型
	 */
	public void weibotypeArrange(SimpleWeiboResult result, int[] weibotype, int existwb) {
		//1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
		for (int i = 1; i < weibotype.length; i++) {
			if (weibotype[i] != 0) {
				result.getWeibotype().put(Integer.toString(i),
						Float.toString((float) Math.round(((float) weibotype[i] / existwb) * 10000) / 100) + "%");
			}
		}
	}

	/**
	 * 转发时间线结果整理（24小时）
	 */
	public void reposttime24HArrange(SimpleWeiboResult result, int[] reposttimelineby24H, int existwb) {

		for (int i = 0; i < 24; i++) {
			result.getReposttimelineby24H().put(Integer.toString(i), Float.toString((float)Math.round(((float)reposttimelineby24H[i]/existwb)*10000)/100) + "%");
		}
	}

	/**
	 * 性别分布结果整理
	 */
	public void genderArrange(SimpleWeiboResult result, int[] gender) {

		result.getGender().put("m",
				Float.toString((float) Math.round(((float) gender[1] / (gender[1] + gender[2])) * 10000) / 100) + "%");
		result.getGender().put("f",
				Float.toString((float) Math.round(((float) gender[2] / (gender[1] + gender[2])) * 10000) / 100) + "%");
	}

	/**
	 * 区域分布结果整理
	 */
	public void locationArrange(SimpleWeiboResult result, int[] location, int existwb) {

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
	 * 水军结果整理
	 */
	public void qualityArrange(SimpleWeiboResult result, int[] reposterquality) {

		int existwb = reposterquality[0] + reposterquality[1];
		result.getReposterquality().put("mask", Float.toString((float)Math.round(((float)reposterquality[0]/existwb)*10000)/100) + "%");
		result.getReposterquality().put("real", Float.toString((float)Math.round(((float)reposterquality[1]/existwb)*10000)/100) + "%");
	}

	/**
	 * 关键账号结果整理，按转发量
	 */
	public void repskeyusersArrange(SimpleWeiboResult result, String[] keyusersbyreps, HashMap<String, String> userfans) {

		for (int n = 0; n < 50; n++) {
			if (keyusersbyreps[n].length() > 5) {
				result.getKeyusersbyreps().put(
						Integer.toString(n),
						this.tran2Hash(keyusersbyreps[n],
								userfans.get(keyusersbyreps[n].substring(0, keyusersbyreps[n].indexOf(",")))));
			} else {
				break;
			}
		}
	}

	/**
	 * 关键账号结果整理，按粉丝量
	 */
	public void comskeyusersArrange(SimpleWeiboResult result, String[] keyusersbycoms, HashMap<String, String> userfans) {

		for (int n = 0; n < 50; n++) {
			if (keyusersbycoms[n].length() > 5) {
				result.getKeyusersbyfans().put(Integer.toString(n), this.tran2Hash(keyusersbycoms[n],
								userfans.get(keyusersbycoms[n].substring(0, keyusersbycoms[n].indexOf(",")))));
			} else {
				break;
			}
		}
	}

	/**
	 * 字符串转换成HashMap
	 * @param info
	 * @return
	 */
	public HashMap<String, String> tran2Hash(String info, String fanssum) {

		HashMap<String, String> temp = new HashMap<String, String>();
		String username = info.substring(0, info.indexOf(","));
		info = info.substring(info.indexOf(",") + 1);
		String nickname = info.substring(0, info.indexOf(","));
		info = info.substring(info.indexOf(",") + 1);
		String head = info.substring(0, info.indexOf(","));
		info = info.substring(info.indexOf(",") + 1);
		@SuppressWarnings("unused")
		String wbtype = info.substring(0, info.indexOf(","));
		info = info.substring(info.indexOf(",") + 1);
		String verifytype = info.substring(0, info.indexOf(","));
		info = info.substring(info.indexOf(",") + 1);
		String reposttime = info.substring(0, info.indexOf("="));
		String repostercount = info.substring(info.indexOf("=") + 1);
		reposttime = this.getTime(Long.parseLong(reposttime));
		temp.put("username", username);
		temp.put("nickname", nickname);
		temp.put("head", head + "/180");
		temp.put("fanssum", fanssum);
		temp.put("verifytype", verifytype);
		temp.put("reposttime", reposttime);
		temp.put("repostercount", repostercount);

		return temp;
	}

	public HashMap<Integer, String> tran2Uids(String[] keyusers) {

		HashMap<Integer, String> uids = new HashMap<Integer, String>();
		for (int i = 0; i < keyusers.length; i++) {
			if (keyusers[i].length() > 5) {
				uids.put(i, keyusers[i].substring(0, keyusers[i].indexOf(",")));
			} else {
				break;
			}
		}

		return uids;
	}

	/**
	 * 评论信息的情感值结果整理
	 */
	public void emotionArrange(SimpleWeiboResult result, int[] emotions) {

		UfiUtils ufiUtils = new UfiUtils();
		int[] remotions = ufiUtils.transData(emotions);
		int sum = remotions[0] + remotions[1] + remotions[2];
		if (sum == 0) {
			remotions[1] = 1;
			sum = 1;
		}

		result.getEmotion().put("negative", Float.toString((float)Math.round(((float)remotions[0]/sum)*10000)/100) + "%");
		result.getEmotion().put("positive", Float.toString((float)Math.round(((float)remotions[1]/sum)*10000)/100) + "%");
		result.getEmotion().put("neutral", Float.toString((float)Math.round(((float)remotions[2]/sum)*10000)/100) + "%");
	}

	/**
	 * 终端分布整理
	 * @param result
	 * @param wbsource
	 * @param existwb
	 */
	public void sourceArrange(SimpleWeiboResult result, int[] wbsource, int existwb) {

		result.getSource().put("tencent",
				Float.toString((float) Math.round(((float) wbsource[0] / existwb) * 10000) / 100) + "%");
		result.getSource().put("pc",
				Float.toString((float) Math.round(((float) wbsource[1] / existwb) * 10000) / 100) + "%");
		result.getSource().put("android",
				Float.toString((float) Math.round(((float) wbsource[2] / existwb) * 10000) / 100) + "%");
		result.getSource().put("iphone",
				Float.toString((float) Math.round(((float) wbsource[3] / existwb) * 10000) / 100) + "%");
		result.getSource().put("ipad",
				Float.toString((float) Math.round(((float) wbsource[4] / existwb) * 10000) / 100) + "%");
		result.getSource().put("others",
				Float.toString((float) Math.round(((float) wbsource[5] / existwb) * 10000) / 100) + "%");
	}

	/**
	 * 认证分布结果整理
	 */
	public void verifiedTypeArrange(SimpleWeiboResult result, int[] verifiedtype, int existuids) {

		result.getVerifiedtype().put("no",
				Float.toString((float) Math.round(((float) verifiedtype[0] / existuids) * 10000) / 100) + "%");
		result.getVerifiedtype().put("vip",
				Float.toString((float) Math.round(((float) verifiedtype[1] / existuids) * 10000) / 100) + "%");
	}

	/**
	 * 关键词提取
	 * @param result
	 * @param str
	 * @throws URIException
	 */
	public void keywordsArrange(SimpleWeiboResult result, String str) throws URIException {

		WordSegment wordseg = new WordSegment();
		HashMap<String, Integer> words = new HashMap<String, Integer>();
		String sentence, strdata, key;
		JsonNode jsondata;
		int max = (str.length() / 500 > 10) ? 10 : str.length() / 500;
		for (int i = 0; i < max; i++) {
			sentence = str.substring(i * 500, (i + 1) * 500);
			strdata = wordseg.getWordsSeg(sentence);
			jsondata = JsonUtils.getJsonNode(strdata);
			for (int j = 0; j < jsondata.get(0).size(); j++) {
				key = jsondata.get(0).get(Integer.toString(j)).toString().replaceAll("\"", "");
				if (words.get(key) == null) {
					words.put(key, 1);
				} else {
					words.put(key, words.get(key) + 1);
				}
			}
		}
		String[] sortedwords = PPSort.sortedToStrings(words);
		int t = (sortedwords.length > 20) ? 20 : sortedwords.length;
		for (int i = 0; i < t; i++) {
			result.getKeywords().put(Integer.toString(i), sortedwords[i].substring(0, sortedwords[i].indexOf("=")));
		}
	}

	public void lastCommentArrange(SimpleWeiboResult result, long allexposion, int[] emotions, int[] gender,
			int[] location, int[] reposterquality) {

		// 如：消息曝光量为 104883，超过 70% 的微博； 
		// 传递了不俗的正能量，参与用户多带正面情绪；男性对该微博兴趣明显比女性高；
		// 广东、安徽、云南等地区的用户的相对参与度高；皮皮精灵没有发现有水军痕迹哦！
		String lastcom = "消息曝光量为" + allexposion + "，";
		int swb = (int) (Math.round(Math.random()*40) + 50);
		lastcom += "超过" + swb + "% 的微博；";
		String[] estr = { "负面", "正面", "中性" };
		int index = PPSort.getMaxId(emotions);
		int sum = emotions[0] + emotions[1] + emotions[2];
		if (sum == 0) 
			sum = 1;
		lastcom += "参与用户多带" + estr[index] + "情绪，其情绪能量值占比为"
				+ Float.toString((float) Math.round(((float) emotions[index] / sum) * 10000) / 100) + "%；";
		if (gender[1] > gender[2]) {
			lastcom += "男性对该微博兴趣明显比女性高，且占比为"
					+ Float.toString((float) Math.round(((float) gender[1] / (gender[1] + gender[2])) * 10000) / 100)
					+ "%；";
		} else {
			lastcom += "女性对该微博兴趣明显比男性高，且占比为"
					+ Float.toString((float) Math.round(((float) gender[2] / (gender[1] + gender[2])) * 10000) / 100)
					+ "%；";
		}
		String[] cityname = { "北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "上海", "江苏", "浙江", "安徽", "福建", "江西",
				"山东", "河南", "湖北", "湖南", "广东", "广西", "海南", "重庆", "四川", "贵州", "云南", "西藏", "陕西", "甘肃", "青海", "宁夏", "新疆",
				"台湾", "香港", "澳门", "其他", "海外" };
		String[] citycode = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37",
				"41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71",
				"81", "82", "100", "0" }; // 400 换成0
		HashMap<String, String> city = new HashMap<String, String>();
		for (int i = 0; i < cityname.length; i++) {
			city.put(citycode[i], cityname[i]);
		}
		String[] citytable = new String[location.length];
		for (int i = 0; i < location.length; i++) {
			citytable[i] = "0=0";
		}
		for (int i = 1; i < location.length; i++) { // 因为腾讯微博中选择400即海外的用户比较多，所以在这里去除掉
			citytable = InsertSort.toptable(citytable, i + "=" + location[i]);
		}
		lastcom += city.get(citytable[0].substring(0, citytable[0].indexOf("="))) + "、"
				+ city.get(citytable[1].substring(0, citytable[1].indexOf("="))) + "、"
				+ city.get(citytable[2].substring(0, citytable[2].indexOf("="))) + "、"
				+ city.get(citytable[3].substring(0, citytable[3].indexOf("="))) + "等地区的用户的相对参与度高；";
		lastcom += "皮皮精灵发现有"
				+ Float.toString((float) Math
						.round(((float) reposterquality[0] / (reposterquality[0] + reposterquality[1])) * 10000) / 100)
				+ "%" + "的水军痕迹哦！";
		result.setLastcomment(lastcom);
	}

}
