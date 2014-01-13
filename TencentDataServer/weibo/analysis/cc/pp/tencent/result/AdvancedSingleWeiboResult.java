package cc.pp.tencent.result;

import java.util.HashMap;

/**
 * Title: 高级版单条微博分析结果类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class AdvancedSingleWeiboResult {

	private String repostcount = new String();  //0、转发次数
	private String commentcount = new String(); //1、评论次数
	private HashMap<String,Integer> reposttimelinebyDay = new HashMap<String,Integer>();  //3、转发时间线--按天
	private HashMap<String,Integer> reposttimelinebyHour = new HashMap<String,Integer>(); //按小时
	private HashMap<String,Integer> reposttimelineby24H = new HashMap<String,Integer>();  //按24小时内
	private HashMap<String,String> gender = new HashMap<String,String>();  //4、性别分布
	private HashMap<String,String> sourcecommon = new HashMap<String,String>();  //5、终端设备分布--按常用的
	private HashMap<String,String> sourcemobile = new HashMap<String,String>();  //按运营商（手机类型）
	private HashMap<String,String> location = new HashMap<String,String>();  //6、区域分布
	private HashMap<String,String> verifiedtype = new HashMap<String,String>(); //7、认证分布
	private HashMap<String,Integer> exposionbyDay = new HashMap<String,Integer>();  //8、曝光量时间线--按天
	private HashMap<String,Integer> exposionbyHour = new HashMap<String,Integer>(); //按小时
	private HashMap<String,Integer> exposionby24H = new HashMap<String,Integer>();  //按24小时内
	private HashMap<String,String> repostergrade = new HashMap<String,String>();  //9、转发用户等级（按粉丝量）
	private String addVRatio = new String();  //10、加V比例
	private HashMap<String,String> reposterquality = new HashMap<String,String>(); //11、转发用户质量（水军比例）
	private String exposionsum = new String();  //12、总曝光量
	private HashMap<String,HashMap<String,Integer>> repostinclass = new HashMap<String,HashMap<String,Integer>>(); //13、转发层级分析
	private HashMap<String,Integer> repostnuminclass = new HashMap<String,Integer>(); //14、每层的转发量
	private HashMap<String,HashMap<String,String>> keyusers = new HashMap<String,HashMap<String,String>>(); //15、关键账号
	private String weiboinfluence = new String();  //16、微博影响力
	private String commentemotion = new String();  //17、评论信息情感值
	private HashMap<String,String> negativecomments = new HashMap<String,String>();  //18、负面评论信息
	private HashMap<String,String> expectcomments = new HashMap<String,String>();    //19、负面评论信息

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
	public HashMap<String,Integer> getReposttimelineByDay() {
		return this.reposttimelinebyDay;
	}
	//按小时
	public HashMap<String,Integer> getReposttimelineByHour() {
		return this.reposttimelinebyHour;
	}
	//按小时
	public HashMap<String,Integer> getReposttimelineBy24H() {
		return this.reposttimelineby24H;
	}

	//4、性别
	public HashMap<String,String> getGender() {
		return this.gender;
	}

	//5、终端设备分布--按常用的
	public HashMap<String,String> getSourceCommon() {
		return this.sourcecommon;
	}
	public HashMap<String,String> getSourceMobile() {
		return this.sourcemobile;
	}

	//6、区域分布
	public HashMap<String,String> getLocation() {
		return this.location;
	}

	//7、认证分布
	public HashMap<String,String> getVerifiedType() {
		return this.verifiedtype;
	}

	//8、曝光量时间线--按天
	public HashMap<String,Integer> getExposionByDay() {
		return this.exposionbyDay;
	}
	//按小时
	public HashMap<String,Integer> getExposionByHour() {
		return this.exposionbyHour;
	}
	//按小时
	public HashMap<String,Integer> getExposionBy24H() {
		return this.exposionby24H;
	}
	
	//9、转发用户等级（按粉丝量）
	public HashMap<String,String> getReposterGrade() {
		return this.repostergrade;
	}
	
	//10、加V比例
	public void setAddVRatio(String addVRatio) {
		this.addVRatio = addVRatio;
	}
	public String getAddVRatio() {
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
	
	//13、转发层级分析
	public HashMap<String,HashMap<String,Integer>> getRepostInClass() {
		return this.repostinclass;
	}
	
	//14、每层的转发量
	public HashMap<String,Integer> getRepostNumInClass() {
		return this.repostnuminclass;
	}
	
	//15、关键账号
	public HashMap<String,HashMap<String,String>> getKeyUsers() {
		return this.keyusers;
	}
	
	//16、微博影响力
	public void setWeiboInfluence(String weiboinfluence) {
		this.weiboinfluence = weiboinfluence;
	}
	public String getWeiboInfluence() {
		return this.weiboinfluence;
	}
	
	//17、评论信息情感值
	public void setCommentEmotion(String commentemotion) {
		this.commentemotion = commentemotion;
	}
	public String getCommentionEmotion() {
		return this.commentemotion;
	}
	
	//18、负面评论信息
	public HashMap<String,String> getNegativeComments() {
		return this.negativecomments;
	}
	
	//19、负面评论信息
	public HashMap<String,String> getExpectComments() {
		return this.expectcomments;
	} 
	
}


