package cc.pp.sina.result;

import java.util.HashMap;

/**
 * Title: 基础版用户分析结果类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class BasedUserResult {

	private String fanscount = new String();
	private HashMap<String,String> location = new HashMap<String,String>();
	private HashMap<String,String> gender = new HashMap<String,String>();
	private HashMap<String,String> fansQuality = new HashMap<String,String>();
	private HashMap<String,String> fansClass = new HashMap<String,String>();
	private String addVRatio = new String();
	private HashMap<String,String> verifiedType = new HashMap<String,String>();
	private HashMap<String,String> ishead = new HashMap<String,String>();
	private String activation = new String();
	private String influence = new String(); 
	
	public void setFanscount(String fanscount) {
		this.fanscount = fanscount;
	}
	
	public String getFanscount() {
		return this.fanscount;
	}
	
	public HashMap<String, String> getLocation() {
		return this.location;
	}
	
	public HashMap<String, String> getGender() {
		return this.gender;
	}
	
	public HashMap<String, String> getFansQuality() {
		return this.fansQuality;
	}
	
	public HashMap<String, String> getGradeByFans() {
		return this.fansClass;
	}
	
	public void setAddVRatio(String addVRatio) {
		this.addVRatio = addVRatio;
	}
	
	public String getAddVRatio() {
		return this.addVRatio;
	}
	
	public HashMap<String, String> getVerifiedType() {
		return this.verifiedType;
	}
	
	public HashMap<String, String> getIshead() {
		return this.ishead;
	}
	
	public void setActivation(String activation) {
		this.activation = activation;
	}
	
	public String getActivation() {
		return this.activation;
	}
	
	public void setInfluence(String influence) {
		this.influence = influence;
	}
	
	public String getInfluence() {
		return this.influence;
	}
	
}
