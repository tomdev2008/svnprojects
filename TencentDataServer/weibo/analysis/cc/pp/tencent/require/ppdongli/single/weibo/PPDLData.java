package cc.pp.tencent.require.ppdongli.single.weibo;

public class PPDLData {

	private String username; // 用户名
	private String nickname; // 昵称
	private String province; // 省份
	private String gender; // 性别
	private String fanscount; // 粉丝数
	private String weibocount; // 微博数
	private String verifiedtype; // 认证类型
	private String mid; // 微博id
	private String createdat; // 创建时间（秒）
	private String repostcount; // 转发量
	private String commentcount; // 评论量

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
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getCreatedat() {
		return createdat;
	}

	public void setCreatedat(String createdat) {
		this.createdat = createdat;
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

}
