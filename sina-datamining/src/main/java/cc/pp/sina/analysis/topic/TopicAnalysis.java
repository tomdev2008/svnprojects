package cc.pp.sina.analysis.topic;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.result.TopicResult;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

/**
 * Title: 话题分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TopicAnalysis implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	private String ip = "";

	/**
	 * @构造函数
	 * @param ip,服务器IP地址
	 * @param pages,新浪微博中话题页码数
	 */
	public TopicAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) {
		// 
		TopicAnalysis ta = new TopicAnalysis("192.168.1.172");
		String url = new String(
				"http://s.weibo.com/weibo/%25E5%258C%2597%25E4%25BA%25AC%25E7%2594%25B7%25E5%25AD%2590%25E5%2587%259D%25E8%25A7%2586%25E5%2585%25AC%25E4%25BA%25A4%25E7%25AB%2599%25E7%2589%258C%25E7%258B%2582%25E5%2593%25AD2%25E5%25B0%258F%25E6%2597%25B6&Refer=STopic_box");

		try {
			long time = System.currentTimeMillis();
			String[] result = ta.getAnalysisResult(url, 3);
			System.out.println(result[0]);
			System.out.println(result[1]);
			System.out.println((System.currentTimeMillis() - time) / 1000);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @获取分析结果
	 * @param originalurl
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 * @throws HttpException
	 * @throws IOException
	 */
	public String[] getAnalysisResult(String originalurl, int pages) throws WeiboException, SQLException, HttpException, IOException {
		
		String[] result = null;
		String[] wids = TopicUtils.getWids(originalurl, pages);
		if (wids != null) {
			result = this.getAllReposterResult(wids);
		}
		
		return result;
	}

	
	/**
	 * @分析所有微博
	 * @param wids
	 * @return
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public String[] getAllReposterResult(String[] wids) throws WeiboException, SQLException {
		
		String[] tworesult = new String[2];
		int apicount = 0;
		TopicResult result = new TopicResult();
		/********************提取accesstoken***********************/
		String[] tokens = TopicUtils.getAccessTokens(wids.length, this.ip);
		Timeline tm = new Timeline();
		/**********************变量初始化****************************/
		//		int originaluser = 0;   //0、原创用户数
		String[] originaluser = new String[wids.length];
		int repostcount = 0; //1、转发总量
		int commentcount = 0; //2、评论总量
		int[] reposttimelineby24H = new int[24]; //按24小时内
		int[] gender = new int[2]; //4、性别分布
		int[] location = new int[101]; //6、区域分布
		int[] verifiedtype = new int[14]; //7、认证分布
		int[] exposionby24H = new int[24]; //按24小时内
		int[] repostergrade = new int[5]; //9、转发用户等级（按粉丝量）
		int[] addVRatio = new int[2]; //10、加V比例
		int[] reposterquality = new int[2]; //11、转发用户质量（水军比例）
		int exposionsum = 0; //12、总曝光量
		String[] keyusersbyreps = new String[50]; // 5、关键转发用户，按转发量
		for (int i = 0; i < keyusersbyreps.length; i++) {
			keyusersbyreps[i] = "0=0";
		}
		String[] keyusersbyfans = new String[50]; // 5、关键账号,按粉丝量
		for (int i = 0; i < keyusersbyfans.length; i++) {
			keyusersbyfans[i] = "0=0";
		}
		int existwb = 0, wbsum = 0, city, fanssum, weibosum, verifytype, cursor, repostercount;
		String sex, hour, text;
		boolean addv;
		long reposttime;
		SimpleDateFormat fo1;
		Paging page;
		StatusWapper status;
		
		result.setOriginaluser(Integer.toString(wids.length));
		for (int nums = 0; nums < tokens.length; nums++)
		{
			System.out.println("Users: " + nums);
			
			tm.client.setToken(tokens[nums]);
			/******************单条微博信息*******************/
			Status wbinfo = tm.showStatus(wids[nums]);
			apicount++;
			if ((wbinfo.getUser() == null) || (wbinfo.getRetweetedStatus() != null)) {
				continue;
			}
			repostcount += wbinfo.getRepostsCount();
			commentcount += wbinfo.getCommentsCount();
			exposionsum += wbinfo.getUser().getFollowersCount();
			text = wbinfo.getText();
			if ((!text.contains("//@")) && (!text.contains("转发微博"))) {
				originaluser[nums] = wbinfo.getUser().getName() + "=" + wbinfo.getUser().getFollowersCount();
			}

//			System.out.println("RepostsCount: " + wbinfo.getRepostsCount());
			
			if (wbinfo.getRepostsCount() == 0) {
				continue;
			}
			/******************微博转发信息*******************/
			cursor = 1;
			page = new Paging(cursor, 200);
			status = tm.getRepostTimeline(wids[nums], page);
			apicount++;
			wbsum += (int) status.getTotalNumber();
		
			/************开始循环计算***************/
			while ((cursor * 200 < wbsum + 200) && (cursor < 11))
			{
				for(Status reposter : status.getStatuses())
				{
					if (reposter.getId() == null) {
						continue;
					} else {
						existwb++;
					}
					sex = reposter.getUser().getGender();
					city = reposter.getUser().getProvince();
					repostercount = reposter.getRepostsCount();
					fanssum = reposter.getUser().getFollowersCount();
					weibosum = reposter.getUser().getStatusesCount();
					addv = reposter.getUser().isVerified();
					verifytype = reposter.getUser().getVerifiedType();
					reposttime = reposter.getCreatedAt().getTime()/1000;
					fo1 = new SimpleDateFormat("HH");
					hour = fo1.format(new Date(reposttime*1000l));
					if (hour.substring(0,1).equals("0")) {
						hour = hour.substring(1);
					} 
					/************3、转发时间线（24小时）************/
					reposttimelineby24H[Integer.parseInt(hour)]++;
					/***************4、性别分布*****************/
					if (sex.equals("m")) {
						gender[0]++; //0----m
					} else {
						gender[1]++; //1----f
					}
					/***************6、区域分布*****************/
					if (city == 400) {
						location[0]++;
					} else {
						location[city]++;
					}
					/***************7、认证分布*****************/
					if ((verifytype < 9)&&(verifytype >= 0)) {
						verifiedtype[verifytype]++;
					} else if (verifytype == 200) {
						verifiedtype[9]++;
					} else if (verifytype == 220) {
						verifiedtype[10]++;
					} else if (verifytype == 400) {
						verifiedtype[11]++;
					} else if (verifytype == -1) {
						verifiedtype[12]++;
					} else {
						verifiedtype[13]++;
					}
					/************8、曝光时间线（24小时）************/
					exposionby24H[Integer.parseInt(hour)]++;
					/***************10、加V比例*****************/
					/***************11、水军比例****************/
					if (addv == false) {
						if (fanssum < 50 || weibosum < 20) {
							reposterquality[0]++;
						} else if ((fanssum*3 < weibosum) && (fanssum < 150)) {
							reposterquality[0]++;
						} else {
							reposterquality[1]++;
						}
						addVRatio[0]++;
					} else {
						reposterquality[1]++;
						addVRatio[1]++;
					}
					/***********9、转发用户等级（按粉丝量）**********/
					if (fanssum < 100) {
						repostergrade[0]++;  //0-----"X<100"
					} else if (fanssum < 1000) {
						repostergrade[1]++;  //1-----"100<X<1000"
					} else if (fanssum < 10000) {
						repostergrade[2]++;  //2-----"1000<X<1w"
					} else if (fanssum < 100000) {
						repostergrade[3]++;  //3-----"1w<X<10w"
					} else {
						repostergrade[4]++;  //4-----"X>10w"
					}
					/***************12、总曝光量**************/
					exposionsum += fanssum;
					/***************13、关键转发用户**************/
					keyusersbyreps = InsertSort.toptable(keyusersbyreps, reposter.getUser().getName() + "="
							+ repostercount);
					keyusersbyfans = InsertSort.toptable(keyusersbyfans, reposter.getUser().getName() + "=" + fanssum);
				}
				cursor++;
				page = new Paging(cursor, 200);;
				try {
					status = tm.getRepostTimeline(wids[nums], page);
				} catch (WeiboException e) {
					break;
				}
				apicount++;
			}
		}
		result.setOriginalusers(originaluser);
		/***********************************整理数据结果***********************************************/
		result.setRepostcount(Integer.toString(repostcount)); //1、转发量
		result.setCommentcount(Integer.toString(commentcount)); //2、评论量
		/************3、转发时间线（24小时）************/
		for (int i = 0; i < 24; i++) {
			result.getReposttimelineby24H().put(Integer.toString(i), reposttimelineby24H[i]);
		}
		/***************4、性别分布*****************/
		result.getGender().put("m", Float.toString((float)Math.round(((float)gender[0]/existwb)*10000)/100) + "%");
		result.getGender().put("f", Float.toString((float)Math.round(((float)gender[1]/existwb)*10000)/100) + "%");
		/***************6、区域分布*****************/
		if (location[0] != 0) {
			result.getLocation().put("400", Float.toString((float)Math.round(((float)location[0]/existwb)*10000)/100) + "%");
		}
		for (int j = 1; j < location.length; j++) {
			if (location[j] != 0) {
				result.getLocation().put(Integer.toString(j), Float.toString((float)Math.round(((float)location[j]/existwb)*10000)/100) + "%");
			}
		}
		/***************7、认证分布*****************/
		if (verifiedtype[13] != 0) {
			result.getVerifiedtype().put("other",
					Float.toString((float) Math.round(((float) verifiedtype[13] / existwb) * 10000) / 100) + "%");
		}
		if (verifiedtype[12] != 0) {
			result.getVerifiedtype().put("-1",
					Float.toString((float) Math.round(((float) verifiedtype[12] / existwb) * 10000) / 100) + "%");
		}
		if (verifiedtype[11] != 0) {
			result.getVerifiedtype().put("400",
					Float.toString((float) Math.round(((float) verifiedtype[11] / existwb) * 10000) / 100) + "%");
		}
		if (verifiedtype[10] != 0) {
			result.getVerifiedtype().put("220",
					Float.toString((float) Math.round(((float) verifiedtype[10] / existwb) * 10000) / 100) + "%");
		}
		if (verifiedtype[9] != 0) {
			result.getVerifiedtype().put("200",
					Float.toString((float) Math.round(((float) verifiedtype[9] / existwb) * 10000) / 100) + "%");
		}
		for (int n7 = 0; n7 < 9; n7++) {
			if (verifiedtype[n7] != 0) {
				result.getVerifiedtype().put(Integer.toString(n7),
						Float.toString((float) Math.round(((float) verifiedtype[n7] / existwb) * 10000) / 100) + "%");
			}
		}
		/************8、曝光时间线（24小时）************/
		for (int i = 0; i < 24; i++) {
			result.getExposionby24H().put(Integer.toString(i), exposionby24H[i]);
		}
		/***************10、加V比例*****************/
		result.setAddVRatio(Float.toString((float)Math.round(((float)addVRatio[1]/existwb)*10000)/100) + "%");
		/***************11、水军比例****************/
		result.getReposterquality().put("mask",
				Float.toString((float) Math.round(((float) reposterquality[0] / existwb) * 10000) / 100) + "%");
		result.getReposterquality().put("real",
				Float.toString((float) Math.round(((float) reposterquality[1] / existwb) * 10000) / 100) + "%");
		/***********9、转发用户等级（按粉丝量）**********/
		result.getRepostergrade().put("<100",
				Float.toString((float) Math.round(((float) repostergrade[0] / existwb) * 10000) / 100) + "%");
		result.getRepostergrade().put("100~1000",
				Float.toString((float) Math.round(((float) repostergrade[1] / existwb) * 10000) / 100) + "%");
		result.getRepostergrade().put("1000~1w",
				Float.toString((float) Math.round(((float) repostergrade[2] / existwb) * 10000) / 100) + "%");
		result.getRepostergrade().put("1w~10w",
				Float.toString((float) Math.round(((float) repostergrade[3] / existwb) * 10000) / 100) + "%");
		result.getRepostergrade().put(">10w",
				Float.toString((float) Math.round(((float) repostergrade[4] / existwb) * 10000) / 100) + "%");
		/***************12、总曝光量**************/
		result.setExposionsum(Integer.toString(exposionsum));
		/***************13、关键转发用户**************/
		for (int n = 0; n < 50; n++) {
			if (keyusersbyreps[n].length() > 5) {
				result.getKeyusersbyreps().put(Integer.toString(n), keyusersbyreps[n]);
			} else {
				break;
			}
		}
		for (int n = 0; n < 50; n++) {
			if (keyusersbyfans[n].length() > 5) {
				result.getKeyusersbyfans().put(Integer.toString(n), keyusersbyfans[n]);
			} else {
				break;
			}
		}

		/***************数据转换与存储**************/
		JSONArray jsonresult = JSONArray.fromObject(result);
        tworesult[0] = jsonresult.toString();
        tworesult[1] = Integer.toString(apicount);;
        
		return tworesult;		
	}

}
