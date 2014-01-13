package cc.pp.tencent.result;

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
	private HashMap<String,String> gender = new HashMap<String,String>();  //4、性别分布
	private HashMap<String,String> reposttimelinebyDay = new HashMap<String,String>();  //3、转发时间线--按天
	private HashMap<String,Integer> reposttimelinebyHour = new HashMap<String,Integer>(); //按小时
	private HashMap<String,String> reposttimelineby24H = new HashMap<String,String>();  //按24小时内
	private HashMap<String,String> location = new HashMap<String,String>();  //6、区域分布
	private HashMap<String,String> viptype = new HashMap<String,String>(); //7、vip类型
	private HashMap<String,String> isself = new HashMap<String,String>(); //7、是否是自己发的微博
	private HashMap<String,String> addVRatio = new HashMap<String,String>();  //10、加V比例  
	private HashMap<String,String> repostergrade = new HashMap<String,String>();  //9、转发用户等级（按粉丝量）
	private HashMap<String,String> reposterquality = new HashMap<String,String>(); //11、转发用户质量（水军比例）
	private String exposionsum = new String();  //12、总曝光量
	private HashMap<String,String> weibotype = new HashMap<String,String>();  //微博类型
	private HashMap<String,String> weibostatus = new HashMap<String,String>();  //微博状态
	private HashMap<String,String> keyusersbyrep = new HashMap<String,String>(); //15、关键账号，按照转发量
	private HashMap<String,String> keyusersbycom = new HashMap<String,String>(); //关键账号(按照评论量)
	private HashMap<String,String> commentemotion = new HashMap<String,String>();  //17、正负评论信息情感值
	private HashMap<String,String> negativecomments = new HashMap<String,String>();  //18、负面评论信息

	//原创微博用户数
	public void setOriginalUser(String originaluser) {
		this.originaluser = originaluser;
	}
	public String getOriginalUser() {
		return this.originaluser;
	}

	//0、转发次数
	public void setRepostCount(String repostcount) {
		this.repostcount = repostcount;
	}
	public String getRepostCount(){
		return this.repostcount;
	}

	//1、评论次数
	public void setCommentCount(String commentcount) {
		this.commentcount = commentcount;
	}
	public String getCommentCount() {
		return this.commentcount;
	}

	//3、转发时间线--按天
	public HashMap<String,String> getReposttimelineByDay() {
		return this.reposttimelinebyDay;
	}
	//按小时
	public HashMap<String,Integer> getReposttimelineByHour() {
		return this.reposttimelinebyHour;
	}
	//24小时
	public HashMap<String,String> getReposttimelineBy24H() {
		return this.reposttimelineby24H;
	}

	//4、性别
	public HashMap<String,String> getGender() {
		return this.gender;
	}

	//6、区域分布
	public HashMap<String,String> getLocation() {
		return this.location;
	}

	//7、认证分布
	public HashMap<String,String> getVipType() {
		return this.viptype;
	}

	//微博类型
	public HashMap<String,String> getWeiboType() {
		return this.weibotype;
	}

	//微博状态
	public HashMap<String,String> getWeiboStatus() {
		return this.weibostatus;
	}

	//是否是自己发的微博
	public HashMap<String,String> getIsSelf() {
		return this.isself;
	}

	//9、转发用户等级（按粉丝量）
	public HashMap<String,String> getReposterGrade() {
		return this.repostergrade;
	}

	//10、加V比例
	public HashMap<String,String> getAddVRatio() {
		return this.addVRatio;
	}

	//11、转发用户质量（水军比例）
	public HashMap<String,String> getReposterQuality() {
		return this.reposterquality;
	}

	//12、总曝光量
	public void setExposionSum(String exposionsum) {
		this.exposionsum = exposionsum;
	}
	public String getExposionSum() {
		return this.exposionsum;
	}

	//15、关键账号
	public HashMap<String,String> getKeyUsersByRep() {
		return this.keyusersbyrep;
	}
	public HashMap<String,String> getKeyUserByCom() {
		return this.keyusersbycom;
	}

	//17、评论信息情感值
	public HashMap<String,String> getCommentionEmotion() {
		return this.commentemotion;
	}

	//18、负面评论信息
	public HashMap<String,String> getNegativeComments() {
		return this.negativecomments;
	}

}

