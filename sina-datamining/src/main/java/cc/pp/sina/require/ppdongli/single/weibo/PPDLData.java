package cc.pp.sina.require.ppdongli.single.weibo;

public class PPDLData {

	private String username; // 用户名
	private String nickname; // 昵称
	private int province; // 省份
	private String gender; // 性别
	private int fanscount; // 粉丝数
	private int weibocount; // 微博数
	private int verifiedtype; // 认证类型
	private String mid; // 微博id
	private long createdat; // 创建时间（秒）
	private int repostcount; // 转发量
	private int commentcount; // 评论量

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

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getFanscount() {
		return fanscount;
	}

	public void setFanscount(int fanscount) {
		this.fanscount = fanscount;
	}

	public int getWeibocount() {
		return weibocount;
	}

	public void setWeibocount(int weibocount) {
		this.weibocount = weibocount;
	}

	public int getVerifiedtype() {
		return verifiedtype;
	}

	public void setVerifiedtype(int verifiedtype) {
		this.verifiedtype = verifiedtype;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public long getCreatedat() {
		return createdat;
	}

	public void setCreatedat(long createdat) {
		this.createdat = createdat;
	}

	public int getRepostcount() {
		return repostcount;
	}

	public void setRepostcount(int repostcount) {
		this.repostcount = repostcount;
	}

	public int getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(int commentcount) {
		this.commentcount = commentcount;
	}

}
