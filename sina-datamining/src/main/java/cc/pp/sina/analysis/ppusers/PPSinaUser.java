package cc.pp.sina.analysis.ppusers;

public class PPSinaUser {

	private String username;
	private String nickname;
	private String description;
	private int gender;
	private int province;
	private int fanscount;
	private int friendscount;
	private int weibocount;
	private int verify;
	private int verifytype;
	private String verifiedreason;
	private long lastwbcreated;

	public long getLastwbcreated() {
		return lastwbcreated;
	}

	public void setLastwbcreated(long lastwbcreated) {
		this.lastwbcreated = lastwbcreated;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public int getFanscount() {
		return fanscount;
	}

	public void setFanscount(int fanscount) {
		this.fanscount = fanscount;
	}

	public int getFriendscount() {
		return friendscount;
	}

	public void setFriendscount(int friendscount) {
		this.friendscount = friendscount;
	}

	public int getWeibocount() {
		return weibocount;
	}

	public void setWeibocount(int weibocount) {
		this.weibocount = weibocount;
	}

	public int getVerify() {
		return verify;
	}

	public void setVerify(int verify) {
		this.verify = verify;
	}

	public int getVerifytype() {
		return verifytype;
	}

	public void setVerifytype(int verifytype) {
		this.verifytype = verifytype;
	}

	public String getVerifiedreason() {
		return verifiedreason;
	}

	public void setVerifiedreason(String verifiedreason) {
		this.verifiedreason = verifiedreason;
	}

}
