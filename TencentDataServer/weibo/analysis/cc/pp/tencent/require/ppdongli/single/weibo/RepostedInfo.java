package cc.pp.tencent.require.ppdongli.single.weibo;

import java.util.HashMap;

public class RepostedInfo {
	
	private HashMap<String,String> reposttimelineby24H = new HashMap<String,String>();  //3、转发时间线--按24小时内
	private HashMap<String,String> gender = new HashMap<String,String>();  //4、性别分布
	private HashMap<String,String> location = new HashMap<String,String>();  //6、区域分布
	private HashMap<String,String> verifiedtype = new HashMap<String,String>(); //7、认证分布
	private HashMap<String,String> addVRatio = new HashMap<String,String>();  //10、加V比例  
	private HashMap<String,String> reposterquality = new HashMap<String,String>(); //11、转发用户质量（水军比例）
	private String exposionsum = new String();  //12、总曝光量
	private HashMap<String,String> viptype = new HashMap<String,String>(); //7、vip类型
	private HashMap<String,String> isself = new HashMap<String,String>(); //7、是否是自己发的微博
	private HashMap<String,String> weibotype = new HashMap<String,String>();  //微博类型
	private HashMap<String,String> weibostatus = new HashMap<String,String>();  //微博状态
	
	public HashMap<String, String> getReposttimelineby24H() {
		return reposttimelineby24H;
	}
	public void setReposttimelineby24H(HashMap<String, String> reposttimelineby24h) {
		reposttimelineby24H = reposttimelineby24h;
	}
	public HashMap<String, String> getGender() {
		return gender;
	}
	public void setGender(HashMap<String, String> gender) {
		this.gender = gender;
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
	public HashMap<String, String> getAddVRatio() {
		return addVRatio;
	}
	public void setAddVRatio(HashMap<String, String> addVRatio) {
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
	public HashMap<String, String> getViptype() {
		return viptype;
	}
	public void setViptype(HashMap<String, String> viptype) {
		this.viptype = viptype;
	}
	public HashMap<String, String> getIsself() {
		return isself;
	}
	public void setIsself(HashMap<String, String> isself) {
		this.isself = isself;
	}
	public HashMap<String, String> getWeibotype() {
		return weibotype;
	}
	public void setWeibotype(HashMap<String, String> weibotype) {
		this.weibotype = weibotype;
	}
	public HashMap<String, String> getWeibostatus() {
		return weibostatus;
	}
	public void setWeibostatus(HashMap<String, String> weibostatus) {
		this.weibostatus = weibostatus;
	}

}

