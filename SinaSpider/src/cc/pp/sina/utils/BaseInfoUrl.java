package cc.pp.sina.utils;

public class BaseInfoUrl {

	// 微博：http://weibo.cn/1893752581/profile?vt=4&gsid=4u4O831f1RUGyPu6kdkK67OpF6l&st=a767&page=2
	// 关注：http://weibo.cn/1893752581/follow?vt=4&gsid=4u4O831f1RUGyPu6kdkK67OpF6l&st=a767&page=2
	// 粉丝：http://weibo.cn/1893752581/fans?vt=4&gsid=4u4O831f1RUGyPu6kdkK67OpF6l&st=a767&page=2
	private final String BASEURL = "http://weibo.cn/";
	private String GSID = "4u4O831f1RUGyPu6kdkK67OpF6l";
	private String others = "vt=4&st=a767";

	public BaseInfoUrl() {
		//
	}

	/**
	 * 主函数
	 * @param args
	 */
	public static void main(String[] args) {

		BaseInfoUrl baseInfoUrl = new BaseInfoUrl();
		System.out.println(baseInfoUrl.getUrl("1893752581", "profile", 2));
		System.out.println(baseInfoUrl.getUrl("1893752581", "follow", 2));
		System.out.println(baseInfoUrl.getUrl("1893752581", "fans", 2));
	}

	/**
	 * 获取爬取页面Url
	 * @param prop：profile, follow, fans
	 *        分别代表：微博、关注、粉丝
	 * @param page：页码
	 * @param username：用户名
	 * @return
	 */
	public String getUrl(String username, String prop, int page) {
		String url = getBaseUrl() + username + "/" + prop + "?" + //
				getOthers() + "&gsid=" + getGSID() + "&page=" + page;
		return url;
	}

	public String getBaseUrl() {
		return BASEURL;
	}

	public String getGSID() {
		return GSID;
	}

	public void setGSID(String gSID) {
		GSID = gSID;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

}
