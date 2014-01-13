package cc.pp.tencent.require.ppdongli.single.weibo;

/**
 * 原创用户信息
 * @author Administrator
 *
 */
public class OriginalUserInfo {
	
	private String username;      // 用户名
	private String nickname;      // 昵称
	private String province;      // 省份
	private String gender;        // 性别
	private String fanscount;     // 粉丝数
	private String weibocount;    // 微博数
	private String verifiedtype;  // 认证类型
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getFanscount() {
		return fanscount;
	}
	public void setFanscount(String fanscount) {
		this.fanscount = fanscount;
	}
	public String getWeibocount() {
		return weibocount;
	}
	public void setWeibocount(String weibocount) {
		this.weibocount = weibocount;
	}
	public String getVerifiedtype() {
		return verifiedtype;
	}
	public void setVerifiedtype(String verifiedtype) {
		this.verifiedtype = verifiedtype;
	}

}
