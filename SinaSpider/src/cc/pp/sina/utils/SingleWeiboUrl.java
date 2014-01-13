package cc.pp.sina.utils;

public class SingleWeiboUrl {

	// http://weibo.cn/repost/A2qkmnbRY?uid=1895964183&rl=1&vt=4&st=a767&gsid=4u4O831f1RUGyPu6kdkK67OpF6l&page=2
	// http://weibo.cn/repost/A3dNSqjnN?uid=3692483623&rl=1&vt=4&st=a767&gsid=4u4O831f1RUGyPu6kdkK67OpF6l&page=2
	private final String BASEURL = "http://weibo.cn/repost/";
	private String OTHERS = "rl=1&vt=4&st=a767";
	private final String GSID = "4u4O831f1RUGyPu6kdkK67OpF6l";
	private String username = "";
	private String id = "";
	
	
	public static void main(String[] args) {

		SingleWeiboUrl singleWeiboUrl = new SingleWeiboUrl();
		//		String wburl = "http://e.weibo.com/2287591944/A64nkaj04?ref=http%3A%2F%2Fwww.weibo.com%2Fu%2F1862087393%3Fwvr%3D5%26";
		String wburl = "http://www.weibo.com/2023833992/A5Y2PuOiD";
		singleWeiboUrl.tackleWeiboUrl(wburl);
		System.out.println(singleWeiboUrl.getUrl(3));
	}

	/**
	 * 获取转发微博url
	 * @param wburl
	 * @param page
	 * @return
	 */
	public String getUrl(int page) {

		String url = getBASEURL() + getId() + "?uid=" + getUsername() + "&" + //
				getOTHERS() + "&gsid=" + getGSID() + "&page=" + page;
		return url;
	}

	/**
	 * 从微博url中提取username、id
	 * @param wburl
	 */
	public void tackleWeiboUrl(String wburl) {

		String url = wburl;
		if (url.indexOf("?") > 0) {
			url = url.substring(0, url.indexOf("?"));
			username = url.substring(19, url.lastIndexOf("/"));
			id = url.substring(url.lastIndexOf("/") + 1);
		} else {
			username = url.substring(21, url.lastIndexOf("/"));
			id = url.substring(url.lastIndexOf("/") + 1);
		}
	}

	public String getUsername() {
		return username;
	}

	public String getId() {
		return id;
	}

	public String getOTHERS() {
		return OTHERS;
	}

	public void setOTHERS(String oTHERS) {
		OTHERS = oTHERS;
	}

	public String getGSID() {
		return GSID;
	}

	public String getBASEURL() {
		return BASEURL;
	}

}
