package cc.pp.sina.result;

import java.util.HashMap;

public class FansResult {
	
	// 1、粉丝总数
	private String fanssum = new String();
	// 2、活跃粉丝数
	private String activefanssum = new String();
	// 3、粉丝使用的终端设备来源分布
	private HashMap<String,String> source = new HashMap<String,String>();
	// 4、认证分布
	private HashMap<String,String> verifiedtype = new HashMap<String,String>();
	// 5、粉丝的热门标签
	private HashMap<String, HashMap<String, String>> hottags = new HashMap<String, HashMap<String, String>>();
	// 6、粉丝等级分布（按粉丝量）
	private HashMap<String,String> gradebyfans = new HashMap<String,String>();
	// 7、年龄分析
	private HashMap<String,String> age = new HashMap<String,String>();
	// 8、性别分析
	private HashMap<String,String> gender = new HashMap<String,String>();
	// 9、区域分析
	private HashMap<String,String> location = new HashMap<String,String>();
	// 10、水军分析
	private HashMap<String,String> fansQuality = new HashMap<String,String>();
	// 11、粉丝top分析（按粉丝量）
	private HashMap<String, String> top40byfans = new HashMap<String, String>();
	// 12、认证比例
	private String addVRatio = new String();
	// 13、粉丝活跃时间分析
	private HashMap<String,String> activetimeline = new HashMap<String,String>();
	// 14、增长趋势分析
	private HashMap<String,String> fansaddtimeline = new HashMap<String,String>();
	
	public String getFanssum() {
		return fanssum;
	}
	public void setFanssum(String fanssum) {
		this.fanssum = fanssum;
	}
	public String getActivefanssum() {
		return activefanssum;
	}
	public void setActivefanssum(String activefanssum) {
		this.activefanssum = activefanssum;
	}
	public HashMap<String, String> getSource() {
		return source;
	}
	public void setSource(HashMap<String, String> source) {
		this.source = source;
	}
	public HashMap<String, String> getVerifiedtype() {
		return verifiedtype;
	}
	public void setVerifiedtype(HashMap<String, String> verifiedtype) {
		this.verifiedtype = verifiedtype;
	}
	public HashMap<String, HashMap<String, String>> getHottags() {
		return hottags;
	}
	public void setHottags(HashMap<String, HashMap<String, String>> hottags) {
		this.hottags = hottags;
	}
	public HashMap<String, String> getGradebyfans() {
		return gradebyfans;
	}
	public void setGradebyfans(HashMap<String, String> gradebyfans) {
		this.gradebyfans = gradebyfans;
	}
	public HashMap<String, String> getAge() {
		return age;
	}
	public void setAge(HashMap<String, String> age) {
		this.age = age;
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
	public HashMap<String, String> getFansQuality() {
		return fansQuality;
	}
	public void setFansQuality(HashMap<String, String> fansQuality) {
		this.fansQuality = fansQuality;
	}

	public HashMap<String, String> getTop40byfans() {
		return top40byfans;
	}

	public void setTop40byfans(HashMap<String, String> top40byfans) {
		this.top40byfans = top40byfans;
	}
	public String getAddVRatio() {
		return addVRatio;
	}
	public void setAddVRatio(String addVRatio) {
		this.addVRatio = addVRatio;
	}
	public HashMap<String, String> getActivetimeline() {
		return activetimeline;
	}
	public void setActivetimeline(HashMap<String, String> activetimeline) {
		this.activetimeline = activetimeline;
	}
	public HashMap<String, String> getFansaddtimeline() {
		return fansaddtimeline;
	}
	public void setFansaddtimeline(HashMap<String, String> fansaddtimeline) {
		this.fansaddtimeline = fansaddtimeline;
	}

}
