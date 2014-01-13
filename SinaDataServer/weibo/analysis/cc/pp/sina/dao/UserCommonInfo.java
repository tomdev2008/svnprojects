package cc.pp.sina.dao;

public class UserCommonInfo {

	private String id; //用户UID
	private int followers_count; //粉丝数
	private int friends_count; //关注数
	private int statuses_count; //微博数

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getFollowers_count() {
		return followers_count;
	}

	public void setFollowers_count(int followers_count) {
		this.followers_count = followers_count;
	}

	public int getFriends_count() {
		return friends_count;
	}

	public void setFriends_count(int friends_count) {
		this.friends_count = friends_count;
	}

	public int getStatuses_count() {
		return statuses_count;
	}

	public void setStatuses_count(int statuses_count) {
		this.statuses_count = statuses_count;
	}

}
