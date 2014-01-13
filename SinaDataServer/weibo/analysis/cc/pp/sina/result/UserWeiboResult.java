package cc.pp.sina.result;

import java.util.HashMap;

/**
 * Title: 用户微博分析结果类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UserWeiboResult {
	
	private String weibosum = new String();   // 1、微博总数
	private HashMap<String,String> aveweibo = new HashMap<String,String>();   // 2、平均每天、每周、每月的发博量
	private String lastwbtimediff = new String();  // 3、最新的一条微博距当前的时间差（天）
	private String favoritedratio = new String();  // 4、被收藏比例
	private String picratio = new String();  // 5、微博中含图片比例
	private String oriratio = new String();  // 6、原创比例
	private String aveorirepcom = new String();  // 7、原创中的平均转评量
	private String avereposted = new String();  // 8、平均每条微博转发量
	private String avecommented = new String();  // 9、平均每条微博评论量
	private HashMap<String,String> wbgradebyrep = new HashMap<String,String>();  // 10、微博按转发量等级分布
	private HashMap<String,String> wbgradebycom = new HashMap<String,String>();  // 11、微博按评论量等级分布
	private HashMap<String,String> reptop10 = new HashMap<String,String>();  // 12、转发量前10的微博
	private HashMap<String,String> comtop10 = new HashMap<String,String>();  // 13、评论量前10的微博
	private HashMap<String,String> wbtimelinebyy = new HashMap<String,String>(); // 14、微博发布时间曲线（按年、按月、按周）
	private HashMap<String,Integer> wbtimelinebym = new HashMap<String,Integer>();  // 15、按月
//	private HashMap<String,Integer> wbtimelinebyw = new HashMap<String,Integer>();
	private HashMap<String,String> wbfenbubyhour = new HashMap<String,String>(); // 16、24小时内的微博发布分布
	private HashMap<String,String> wbsource = new HashMap<String,String>();  // 17、微博来源分布
	
    public HashMap<String, String> getWbrepcomtimelinebym() {
		return wbrepcomtimelinebym;
	}
	public void setWbrepcomtimelinebym(HashMap<String, String> wbrepcomtimelinebym) {
		this.wbrepcomtimelinebym = wbrepcomtimelinebym;
	}
	public HashMap<String, String> getWbrepcomtimelinebyw() {
		return wbrepcomtimelinebyw;
	}
	public void setWbrepcomtimelinebyw(HashMap<String, String> wbrepcomtimelinebyw) {
		this.wbrepcomtimelinebyw = wbrepcomtimelinebyw;
	}
	public String getLastweekaverepcom() {
		return lastweekaverepcom;
	}
	public void setLastweekaverepcom(String lastweekaverepcom) {
		this.lastweekaverepcom = lastweekaverepcom;
	}
	public String getLastmonthaverepcom() {
		return lastmonthaverepcom;
	}
	public void setLastmonthaverepcom(String lastmonthaverepcom) {
		this.lastmonthaverepcom = lastmonthaverepcom;
	}
	//	private HashMap<String,String> keywords = new HashMap<String,String>();  // 微博关键词
//	private HashMap<String,Integer> wbemotions = new HashMap<String,Integer>(); // 微博情感曲线
	private HashMap<String,String> wbrepcomtimelinebym = new HashMap<String,String>(); // 18、微博转评时间曲线（按月）
	private HashMap<String,String> wbrepcomtimelinebyw = new HashMap<String,String>(); // 18、微博转评时间曲线（按周）
	private String lastweekaverepcom = new String();  // 19、最近一周平均每条微博转发量
	private String lastmonthaverepcom = new String();  // 20、最近一个月平均每条微博评论量
	
	// 微博总数
	public void setWeiboCount(String weibosum) {
		this.weibosum = weibosum;
	}
	public String getWeiboCount() {
		return this.weibosum;
	}
	// 平均每天、每周、每月的发博量
	public HashMap<String,String> getAverageWeibo() {
		return this.aveweibo;
	}
	// 最新的一条微博距当前的时间差（天）
	public void setLastWbTimeDiff(String lastwbtimediff) {
		this.lastwbtimediff = lastwbtimediff;
	}
	public String getLastWbTimeDiff() {
		return this.lastwbtimediff;
	}
	// 被收藏比例
	public void setFavoritedRatio(String favoritedratio) {
		this.favoritedratio = favoritedratio;
	}
	public String getFavoritedRatio() {
		return this.favoritedratio;
	}
	// 微博中含图片比例
	public void setPicRatio(String picratio) {
		this.picratio = picratio;
	}
	public String getPicRatio() {
		return this.picratio;
	}
	// 原创比例
	public void setOriginalRatio(String oriratio) {
		this.oriratio = oriratio;
	}
	public String getOriginalRatio() {
		return this.oriratio;
	}
	// 转评中的原创比例
	public void setAveOriginalRepCom(String aveorirepcom) {
		this.aveorirepcom = aveorirepcom;
	}
	public String getAveOriginalRepCom() {
		return this.aveorirepcom;
	}
	// 平均转发量
	public void setAveRepostedCount(String avereposted) {
		this.avereposted = avereposted;
	}
	public String getAveRepostedCount() {
		return this.avereposted;
	}
	// 平均评论量
	public void setAveCommentedCount(String avecommented) {
		this.avecommented = avecommented;
	}
	public String getAveCommentedCount() {
		return this.avecommented;
	}
	// 微博按转发量等级分布
	public HashMap<String,String> getWbGradeByRep() {
		return this.wbgradebyrep;
	}
	// 微博按评论量等级分布
	public HashMap<String,String> getWbGradeByCom() {
		return this.wbgradebycom;
	}
	
	// 转发量前10的微博
	public HashMap<String,String> getRepWbTop10() {
		return this.reptop10;
	}
	// 评论量前10的微博
	public HashMap<String,String> getComWbTop10() {
		return this.comtop10;
	}
	// 微博发布时间曲线（按年、按月、按周）
	public HashMap<String,String> getWbTimelineByYear() {
		return this.wbtimelinebyy;
	}
	public void setWbTimelineByMonth(HashMap<String,Integer> wbtimelinebym) {
		this.wbtimelinebym = wbtimelinebym;
	}
	public HashMap<String,Integer> getWbTimelineByMonth() {
		return this.wbtimelinebym;
	}
//	public void setWbTimelineByWeek(HashMap<String,Integer> wbtimelinebyw) {
//		this.wbtimelinebyw = wbtimelinebyw;
//	}
//	public HashMap<String,Integer> getWbTimelineByWeek() {
//		return this.wbtimelinebyw;
//	}
	// 24小时内的微博发布分布
	public HashMap<String,String> getWbFenbuByHour() {
		return this.wbfenbubyhour;
	}
	// 微博来源分布
	public HashMap<String,String> getWbSource() {
		return this.wbsource;
	}
	
}
