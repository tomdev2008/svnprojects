package cc.pp.tencent.result;

/**
 * Title: 用户基础信息（粉丝、关注、微博）分析结果类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UserBaseInfoResult {
	
	private int followerscount = 0;     // 粉丝量
	private int friendscount = 0;       // 关注量
	private int statusescount = 0;      // 微博量
	private int followersadd = 0;       // 粉丝增长量
	private int friendsadd = 0;         // 关注增长量
	private int statusesadd = 0;        // 微博增长量
	private String followersaddratio = new String();  // 粉丝增长比例（相对前一天）
	private String friendsaddratio = new String();    // 关注增长比例（相对前一天）
	private String statusesaddratio = new String();   // 微博增长比例（相对前一天）
	
	public void setFollowersCount(int followerscount) {
		this.followerscount = followerscount;
	}
	public int getFollowersCount() {
		return this.followerscount;
	}
	
	public void setFriendsCount(int friendscount) {
		this.friendscount = friendscount;
	}
	public int getFriendsCount() {
		return this.friendscount;
	}
	
	public void setStatusesCount(int statusescount) {
		this.statusescount = statusescount;
	}
	public int getStatusesCount() {
		return this.statusescount;
	}

	public void setFollowersAdd(int followersadd) {
		this.followersadd = followersadd;
	}
	public int getFollowersAdd() {
		return this.followersadd;
	}
	
	public void setFriendsAdd(int friendsadd) {
		this.friendsadd = friendsadd;
	}
	public int getFriendsAdd() {
		return this.friendsadd;
	}
	
	public void setStatusesAdd(int statusesadd) {
		this.statusesadd = statusesadd;
	}
	public int getStatusesAdd() {
		return this.statusesadd;
	}
	
	public void setFollowersAddRatio(String followersaddratio) {
		this.followersaddratio = followersaddratio;
	}
	public String getFollowersAddRatio() {
		return this.followersaddratio;
	}
	
	public void setFriendsAddRatio(String friendsaddratio) {
		this.friendsaddratio = friendsaddratio;
	}
	public String getFriendsAddRatio() {
		return this.friendsaddratio;
	}
	
	public void setStatusesAddRatio(String statusesaddratio) {
		this.statusesaddratio = statusesaddratio;
	}
	public String getStatusesAddRatio() {
		return this.statusesaddratio;
	}
	
}
