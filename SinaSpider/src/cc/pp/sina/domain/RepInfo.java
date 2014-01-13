package cc.pp.sina.domain;

public class RepInfo {

	private String username;
	private String nickname;
	private String description;
	private String gender;
	private int province;
	private int fanscount;
	private int friendscount;
	private int weibocount;
	private boolean verify;
	private int verifytype;
	private String verifiedreason;
	private long lastwbcreated;
	private String weibotext;
	private String createat;
	private String source;

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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
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

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
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

	public long getLastwbcreated() {
		return lastwbcreated;
	}

	public void setLastwbcreated(long lastwbcreated) {
		this.lastwbcreated = lastwbcreated;
	}

	public String getWeibotext() {
		return weibotext;
	}

	public void setWeibotext(String weibotext) {
		this.weibotext = weibotext;
	}

	public String getCreateat() {
		return createat;
	}

	public void setCreateat(String createat) {
		this.createat = createat;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}
