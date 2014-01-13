package cc.pp.spider.constant;

import java.util.HashMap;

import cc.pp.spider.common.Params;

public class ParamsConstant {
	
	private static Params params = new Params();
	public static final String INPUT_TAGS = "weiboname";
	
	/**
	 * 平台
	 */
	public static final String[] WEIBO_TYPE_FILTER = { "新浪微博", "腾讯微博", "微信", "美丽说", "QQ空间" };
	
	/**
	 * 常见分类 
	 */
	public static final String[] CONTENT_CATEGORIES = { "网购团购", "服装", "美食", "美容", "育儿", "星座", //
			"婚恋", "心情", "IT", "娱乐", "幽默搞笑", "健康养生", "体育活动", "旅游", "商业", "汽车", "文学", "家居生活", "摄影" };

	public static final String[] CATEGORIES = { "wgtg", "fz", "ms", "mr", "yr", "xz", "hn", "xq", //
			"it", "yl", "ymgx", "jkys", "xyhd", "ly", "sy", "qc", "wx", "jjsh", "sy" };

	public static final String[] TENCENT_CATEGORIES = { "电商", "电商", "生活服务", "生活服务", "生活服务", //
			"生活服务", "生活服务", "生活服务", "运营商", "电商", "电商", "医疗", "生活服务", "旅游", "金融", //
			"生活服务", "生活服务", "生活服务", "旅游" };

	/**
	 * 价格类型
	 */
	public static final String[] SWITCH_PRICE = { "硬广转发价", "软广转发价", "硬广直发价", "软广直发价", "带号报价" };

	/**
	 * 价格
	 */
	public static final int[] PRICE_FROM = { 0, 20, 50, 100, 200, 500, 1000 };
	public static final int[] PRICE_TO = { 20, 50, 100, 200, 500, 1000, 0 };
	
	/**
	 * 粉丝量
	 */
	public static final int[] FOLLOWERS_COUNT_MIN = { 0, 10000, 50000, 100000, 200000, 400000, //
		800000, 1200000, 2000000};
	public static final int[] FOLLOWERS_COUNT_MAX = { 10000, 50000, 100000, 200000, 400000, //
		800000, 1200000, 2000000, 0};
	
	/**
	 * 粉丝性别
	 */
	public static final String[] AUDIENCE_GENDER_FILTER = { "男", "女", "泛" };
	
	public static String getCate(String key) throws Exception {
		return params.getCatesProps(key);
	}

	public static String getUserAndPw(String key) throws Exception {
		return params.getUserAndPwProps(key);
	}

	public static HashMap<String, String> getEngCates() {

		HashMap<String, String> cates = new HashMap<String, String>();
		for (int i = 0; i < CATEGORIES.length; i++) {
			cates.put(CONTENT_CATEGORIES[i], CATEGORIES[i]);
		}

		return cates;
	}

	public static HashMap<String, String> getTencentCates() {

		HashMap<String, String> cates = new HashMap<String, String>();
		for (int i = 0; i < TENCENT_CATEGORIES.length; i++) {
			cates.put(CONTENT_CATEGORIES[i], TENCENT_CATEGORIES[i]);
		}

		return cates;
	}

}
