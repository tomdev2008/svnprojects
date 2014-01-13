package cc.pp.sina.analysis.userclassfy.dao;

import java.util.HashMap;

public class UserAndFansData {

	private static String uid;
	private static String nickname;
	private static String description;
	private static int fanscount;
	private static int weibocount;
	private static int verify;
	private static float averagewbs;
	private static int influence;
	private static int activation;
	private static int activecount;
	private static float addvratio;
	private static float activeratio;
	private static float maleratio;
	private static HashMap<String, String> top5provinces;
	private static int createdtime;
	private static float fansexistedratio;
	private static long allfanscount;
	private static long allactivefanscount;

	public static String getUid() {
		return uid;
	}

	public static void setUid(String uid) {
		UserAndFansData.uid = uid;
	}

	public static String getNickname() {
		return nickname;
	}

	public static void setNickname(String nickname) {
		UserAndFansData.nickname = nickname;
	}

	public static String getDescription() {
		return description;
	}

	public static void setDescription(String description) {
		UserAndFansData.description = description;
	}

	public static int getFanscount() {
		return fanscount;
	}

	public static void setFanscount(int fanscount) {
		UserAndFansData.fanscount = fanscount;
	}

	public static int getWeibocount() {
		return weibocount;
	}

	public static void setWeibocount(int weibocount) {
		UserAndFansData.weibocount = weibocount;
	}

	public static int getVerify() {
		return verify;
	}

	public static void setVerify(int verify) {
		UserAndFansData.verify = verify;
	}

	public static float getAveragewbs() {
		return averagewbs;
	}

	public static void setAveragewbs(float averagewbs) {
		UserAndFansData.averagewbs = averagewbs;
	}

	public static int getInfluence() {
		return influence;
	}

	public static void setInfluence(int influence) {
		UserAndFansData.influence = influence;
	}

	public static int getActivation() {
		return activation;
	}

	public static void setActivation(int activation) {
		UserAndFansData.activation = activation;
	}

	public static int getActivecount() {
		return activecount;
	}

	public static void setActivecount(int activecount) {
		UserAndFansData.activecount = activecount;
	}

	public static float getAddvratio() {
		return addvratio;
	}

	public static void setAddvratio(float addvratio) {
		UserAndFansData.addvratio = addvratio;
	}

	public static float getActiveratio() {
		return activeratio;
	}

	public static void setActiveratio(float activeratio) {
		UserAndFansData.activeratio = activeratio;
	}

	public static float getMaleratio() {
		return maleratio;
	}

	public static void setMaleratio(float maleratio) {
		UserAndFansData.maleratio = maleratio;
	}

	public static int getCreatedtime() {
		return createdtime;
	}

	public static void setCreatedtime(int createdtime) {
		UserAndFansData.createdtime = createdtime;
	}

	public static float getFansexistedratio() {
		return fansexistedratio;
	}

	public static void setFansexistedratio(float fansexistedratio) {
		UserAndFansData.fansexistedratio = fansexistedratio;
	}

	public static long getAllfanscount() {
		return allfanscount;
	}

	public static void setAllfanscount(long allfanscount) {
		UserAndFansData.allfanscount = allfanscount;
	}

	public static long getAllactivefanscount() {
		return allactivefanscount;
	}

	public static void setAllactivefanscount(long allactivefanscount) {
		UserAndFansData.allactivefanscount = allactivefanscount;
	}

	public static HashMap<String, String> getTop5provinces() {
		return top5provinces;
	}

	public static void setTop5provinces(HashMap<String, String> top5provinces) {
		UserAndFansData.top5provinces = top5provinces;
	}

}
