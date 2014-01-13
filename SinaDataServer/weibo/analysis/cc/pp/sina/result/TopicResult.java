package cc.pp.sina.result;

import java.util.HashMap;

/**
 * Title: 话题分析结果类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TopicResult {
	
    private String originaluser = new String();  //原创微博用户数
	private String repostcount = new String();  //0、转发次数
	private String commentcount = new String(); //1、评论次数
	private HashMap<String, Integer> reposttimelinebyDay = new HashMap<String, Integer>(); //3、转发时间线--按天
	private HashMap<String, Integer> reposttimelinebyHour = new HashMap<String, Integer>(); //按小时
	private HashMap<String, Integer> reposttimelineby24H = new HashMap<String, Integer>(); //按24小时内
	private HashMap<String, String> gender = new HashMap<String, String>(); //4、性别分布
	private HashMap<String, String> sourcecommon = new HashMap<String, String>(); //5、终端设备分布--按常用的
	private HashMap<String, String> sourcemobile = new HashMap<String, String>(); //按运营商（手机类型）
	private HashMap<String, String> location = new HashMap<String, String>(); //6、区域分布
	private HashMap<String, String> verifiedtype = new HashMap<String, String>(); //7、认证分布
	private HashMap<String, Integer> exposionbyDay = new HashMap<String, Integer>(); //8、曝光量时间线--按天
	private HashMap<String, Integer> exposionbyHour = new HashMap<String, Integer>(); //按小时
	private HashMap<String, Integer> exposionby24H = new HashMap<String, Integer>(); //按24小时内
	private HashMap<String, String> repostergrade = new HashMap<String, String>(); //9、转发用户等级（按粉丝量）
	private String addVRatio = new String();  //10、加V比例
	private HashMap<String, String> reposterquality = new HashMap<String, String>(); //11、转发用户质量（水军比例）
	private String exposionsum = new String();  //12、总曝光量
	private HashMap<String, HashMap<String, Integer>> repostinclass = new HashMap<String, HashMap<String, Integer>>(); //13、转发层级分析
	private HashMap<String, Integer> repostnuminclass = new HashMap<String, Integer>(); //14、每层的转发量
	private HashMap<String, HashMap<String, String>> keyusers = new HashMap<String, HashMap<String, String>>(); //15、关键账号
	private String weiboinfluence = new String();  //16、微博影响力
	private HashMap<String, String> commentemotion = new HashMap<String, String>(); //17、正负评论信息情感值
	private HashMap<String, String> negativecomments = new HashMap<String, String>(); //18、负面评论信息
	private HashMap<String, String> expectcomments = new HashMap<String, String>(); //19、期望评论信息
	// 7、关键转发用户
	private HashMap<String, String> keyusersbyreps = new HashMap<String, String>(); // 按转发量
	private HashMap<String, String> keyusersbyfans = new HashMap<String, String>(); // 按照粉丝量
	private String[] originalusers;
	
	public String[] getOriginalusers() {
		return originalusers;
	}

	public void setOriginalusers(String[] originalusers) {
		this.originalusers = originalusers;
	}

	public String getOriginaluser() {
		return originaluser;
	}

	public void setOriginaluser(String originaluser) {
		this.originaluser = originaluser;
	}

	public String getRepostcount() {
		return repostcount;
	}

	public void setRepostcount(String repostcount) {
		this.repostcount = repostcount;
	}

	public String getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}

	public HashMap<String, Integer> getReposttimelinebyDay() {
		return reposttimelinebyDay;
	}

	public void setReposttimelinebyDay(HashMap<String, Integer> reposttimelinebyDay) {
		this.reposttimelinebyDay = reposttimelinebyDay;
	}

	public HashMap<String, Integer> getReposttimelinebyHour() {
		return reposttimelinebyHour;
	}

	public void setReposttimelinebyHour(HashMap<String, Integer> reposttimelinebyHour) {
		this.reposttimelinebyHour = reposttimelinebyHour;
	}

	public HashMap<String, Integer> getReposttimelineby24H() {
		return reposttimelineby24H;
	}

	public void setReposttimelineby24H(HashMap<String, Integer> reposttimelineby24h) {
		reposttimelineby24H = reposttimelineby24h;
	}

	public HashMap<String, String> getGender() {
		return gender;
	}

	public void setGender(HashMap<String, String> gender) {
		this.gender = gender;
	}

	public HashMap<String, String> getSourcecommon() {
		return sourcecommon;
	}

	public void setSourcecommon(HashMap<String, String> sourcecommon) {
		this.sourcecommon = sourcecommon;
	}

	public HashMap<String, String> getSourcemobile() {
		return sourcemobile;
	}

	public void setSourcemobile(HashMap<String, String> sourcemobile) {
		this.sourcemobile = sourcemobile;
	}

	public HashMap<String, String> getLocation() {
		return location;
	}

	public void setLocation(HashMap<String, String> location) {
		this.location = location;
	}

	public HashMap<String, String> getVerifiedtype() {
		return verifiedtype;
	}

	public void setVerifiedtype(HashMap<String, String> verifiedtype) {
		this.verifiedtype = verifiedtype;
	}

	public HashMap<String, Integer> getExposionbyDay() {
		return exposionbyDay;
	}

	public void setExposionbyDay(HashMap<String, Integer> exposionbyDay) {
		this.exposionbyDay = exposionbyDay;
	}

	public HashMap<String, Integer> getExposionbyHour() {
		return exposionbyHour;
	}

	public void setExposionbyHour(HashMap<String, Integer> exposionbyHour) {
		this.exposionbyHour = exposionbyHour;
	}

	public HashMap<String, Integer> getExposionby24H() {
		return exposionby24H;
	}

	public void setExposionby24H(HashMap<String, Integer> exposionby24h) {
		exposionby24H = exposionby24h;
	}

	public HashMap<String, String> getRepostergrade() {
		return repostergrade;
	}

	public void setRepostergrade(HashMap<String, String> repostergrade) {
		this.repostergrade = repostergrade;
	}

	public String getAddVRatio() {
		return addVRatio;
	}
	public void setAddVRatio(String addVRatio) {
		this.addVRatio = addVRatio;
	}

	public HashMap<String, String> getReposterquality() {
		return reposterquality;
	}

	public void setReposterquality(HashMap<String, String> reposterquality) {
		this.reposterquality = reposterquality;
	}

	public String getExposionsum() {
		return exposionsum;
	}

	public void setExposionsum(String exposionsum) {
		this.exposionsum = exposionsum;
	}

	public HashMap<String, HashMap<String, Integer>> getRepostinclass() {
		return repostinclass;
	}

	public void setRepostinclass(HashMap<String, HashMap<String, Integer>> repostinclass) {
		this.repostinclass = repostinclass;
	}

	public HashMap<String, Integer> getRepostnuminclass() {
		return repostnuminclass;
	}

	public void setRepostnuminclass(HashMap<String, Integer> repostnuminclass) {
		this.repostnuminclass = repostnuminclass;
	}

	public HashMap<String, HashMap<String, String>> getKeyusers() {
		return keyusers;
	}

	public void setKeyusers(HashMap<String, HashMap<String, String>> keyusers) {
		this.keyusers = keyusers;
	}

	public String getWeiboinfluence() {
		return weiboinfluence;
	}

	public void setWeiboinfluence(String weiboinfluence) {
		this.weiboinfluence = weiboinfluence;
	}

	public HashMap<String, String> getCommentemotion() {
		return commentemotion;
	}

	public void setCommentemotion(HashMap<String, String> commentemotion) {
		this.commentemotion = commentemotion;
	}

	public HashMap<String, String> getNegativecomments() {
		return negativecomments;
	}

	public void setNegativecomments(HashMap<String, String> negativecomments) {
		this.negativecomments = negativecomments;
	}

	public HashMap<String, String> getExpectcomments() {
		return expectcomments;
	}

	public void setExpectcomments(HashMap<String, String> expectcomments) {
		this.expectcomments = expectcomments;
	}

	public HashMap<String, String> getKeyusersbyreps() {
		return keyusersbyreps;
	}

	public void setKeyusersbyreps(HashMap<String, String> keyusersbyreps) {
		this.keyusersbyreps = keyusersbyreps;
	}

	public HashMap<String, String> getKeyusersbyfans() {
		return keyusersbyfans;
	}

	public void setKeyusersbyfans(HashMap<String, String> keyusersbyfans) {
		this.keyusersbyfans = keyusersbyfans;
	}

}


