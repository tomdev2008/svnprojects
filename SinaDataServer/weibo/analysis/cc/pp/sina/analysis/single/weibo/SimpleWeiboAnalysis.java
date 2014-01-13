package cc.pp.sina.analysis.single.weibo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.URIException;

import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.common.MidToId;
import cc.pp.sina.common.SourceType;
import cc.pp.sina.jdbc.WeiboJDBC;
import cc.pp.sina.result.SimpleWeiboResult;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class SimpleWeiboAnalysis implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException
	 * @throws WeiboException
	 * @throws URIException
	 */
	public static void main(String[] args) throws WeiboException, SQLException, URIException {

		//		SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis("192.168.1.27");
		SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis("127.0.0.1", "root", "root");
		String url = new String("http://e.weibo.com/1747752902/AeyWKdV9I");
		int apicount = swa.analysis(url);
		swa.closeMysql();
		System.out.println(apicount);

	}

	private final WeiboJDBC weibojdbc;

	public SimpleWeiboAnalysis(String ip) {
		weibojdbc = new WeiboJDBC(ip);
		weibojdbc.mysqlStatus();
	}

	public SimpleWeiboAnalysis(String ip, String user, String password) {
		weibojdbc = new WeiboJDBC(ip, user, password);
		weibojdbc.mysqlStatus();
	}

	/**
	 * 分析函数
	 * @throws WeiboException
	 * @throws SQLException
	 * @throws URIException
	 */
	public int analysis(String url) {

		int apicount = 0;
		try {
			/*************结果初始化*************/
			JSONArray jsonresult = null;
			SimpleWeiboResult result = new SimpleWeiboResult();
			SWeiboUtils weiboUtils = new SWeiboUtils();
			//			Emotion emotion = new Emotion();
			/***********结果变量初始化***********/
			int exposionsum = 0; // 1、总曝光量
			int[] emotions = new int[3]; // 4、情感值
			String[] keyusersbyreps = new String[50]; // 5、关键转发用户，按转发量
			weiboUtils.initStrArray(keyusersbyreps);
			String[] keyusersbyfans = new String[50]; // 5、关键账号,按粉丝量
			weiboUtils.initStrArray(keyusersbyfans);
			int[] reposttimelineby24H = new int[24]; // 6、24小时内的转发分布
			HashMap<String, Integer> reposttimelinebyDay = new HashMap<String, Integer>(); //3、转发时间线--按天
			int[] gender = new int[2]; // 7、性别分析
			int[] location = new int[101]; // 8、区域分布
			int[] wbsource = new int[6]; // 9、终端分布
			int[] reposterquality = new int[2]; // 10、转发用户质量
			int[] verifiedtype = new int[14]; //认证分布
			HashMap<String, Integer> suminclass = new HashMap<String, Integer>(); // 2、层级分析 //@个数确定
			String str = ""; // str为待分词的所有微博内容
			// 总体评价
			/***********中间变量初始化************/
			int existwb = 0, wbsum = 0, repostercount, cursor = 1;
			String head, name, sex, hour, text, source, date;
			int city, fanssum, weibosum, verifytype;
			boolean addv;
			long reposttime;
			/***********获取该微博数据************/
			String accesstoken = weibojdbc.getAccessToken();
			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			MidToId midtoid = new MidToId();
			if (url.indexOf("?") != -1) {
				url = url.substring(0, url.indexOf("?"));
			}
			String wid = midtoid.mid2id(url);
			Status wbinfo = tm.showStatus(wid);
			apicount++;
			/*********存放该原创用户信息***********/
			try {
				weiboUtils.originalUserArrange(result, wbinfo);
			} catch (RuntimeException e) {
				return -1;
			}
			/*********存放该微博内容信息***********/
			result.setWbcontent(wbinfo.getText().replaceAll("\"", "\\\""));
			/*********存放转发和评论数据***********/
			result.setRepostcount(Integer.toString(wbinfo.getRepostsCount())); // 2、总转发量
			result.setCommentcount(Integer.toString(wbinfo.getCommentsCount())); // 3、总评论量
			/********************采集微博转发数据**********************/
			StatusWapper status;
			try {
				status = tm.getRepostTimeline(wid, new Paging(cursor, 200));
			} catch (RuntimeException | WeiboException e) {
				return -1;
			}
			apicount++;
			wbsum = (int) status.getTotalNumber();
			if (wbinfo.getUser() == null) { //转发微博0时
				return -1;
			} else {
				exposionsum += wbinfo.getUser().getFollowersCount();
			}
			//			int k = 0;
			/************开始循环计算***************/
			while ((cursor * 200 < wbsum + 200) && (cursor < 11))
			{

				//				System.out.println(cursor);

				for(Status reposter : status.getStatuses())
				{
					//					System.out.println(reposter);

					if (reposter.getId() == null) {
						continue;
					} else {
						existwb++;
					}
					name = reposter.getUser().getName();
					sex = reposter.getUser().getGender();
					city = reposter.getUser().getProvince();
					fanssum = reposter.getUser().getFollowersCount();
					head = reposter.getUser().getProfileImageUrl();
					repostercount = reposter.getRepostsCount();
					weibosum = reposter.getUser().getStatusesCount();
					addv = reposter.getUser().isVerified();
					reposttime = reposter.getCreatedAt().getTime()/1000;
					hour = weiboUtils.getHour(reposttime);
					text = reposter.getText();
					str += text;
					source = reposter.getSource().getName();
					verifytype = reposter.getUser().getVerifiedType();
					date = weiboUtils.getDate(reposttime);
					/****************5、关键账号****************/
					keyusersbyreps = InsertSort.toptable(keyusersbyreps, reposter.getUser().getId() + "," + name + ","
							+ head + "," + fanssum + "," + verifytype + "," + reposttime + "=" + repostercount);
					keyusersbyfans = InsertSort.toptable(keyusersbyreps, reposter.getUser().getId() + "," + name + ","
							+ head + "," + fanssum + "," + verifytype + "," + reposttime + "=" + fanssum);
					/************6、转发时间线（24小时）************/
					reposttimelineby24H[Integer.parseInt(hour)]++;
					/************3、转发时间线（按天）**************/
					weiboUtils.putRepostByDay(reposttimelinebyDay, date);
					/***************7、性别分布*****************/
					gender[weiboUtils.checkSex(sex)]++;
					/***************8、区域分布*****************/
					location[weiboUtils.checkCity(city)]++;
					/***************9、终端分布*****************/
					wbsource[SourceType.getCategory(source)]++;
					/***************10、水军比例****************/
					reposterquality[weiboUtils.checkQuality(addv, fanssum, weibosum)]++;
					/***************1、总曝光量**************/
					exposionsum += fanssum;
					/***************4、情感值***************/
					/**
					 * 这里很耗时间
					 */
					//					if (cursor == 1) {
					//						if (k++ < 10) {
					//							weiboUtils.setEmotions(emotion, emotions, text);
					//						}
					//					}
					/***************认证分布*****************/
					verifiedtype[weiboUtils.checkVerifyType(verifytype)]++;
					/***************层级分析*****************/
					weiboUtils.putSumInClass(suminclass, text);
				}
				try {
					status = tm.getRepostTimeline(wid, new Paging(cursor++, 200));
					apicount++;
				} catch (RuntimeException e) {
					cursor++;
				} catch (WeiboException e) {
					cursor++;
				}
			}
			if (existwb == 0) {
				return -1;
			}
			/******************整理数据结果*******************/
			/************3、转发时间线（24小时）************/
			weiboUtils.reposttime24HArrange(result, reposttimelineby24H, existwb);
			/************3、转发时间线（按天）**************/
			result.setReposttimelinebyDay(reposttimelinebyDay);
			/***************4、性别分布*****************/
			weiboUtils.genderArrange(result, gender, existwb);
			/***************6、区域分布*****************/
			weiboUtils.locationArrange(result, location, existwb);
			/***************11、水军比例****************/
			weiboUtils.qualityArrange(result, reposterquality, existwb);
			/***************12、总曝光量**************/
			long allexposion = (long) exposionsum * wbinfo.getRepostsCount() / existwb;
			result.setExposionsum(Long.toString(allexposion));
			/****************15、关键账号****************/
			weiboUtils.repskeyusersArrange(result, keyusersbyreps);
			weiboUtils.fanskeyusersArrange(result, keyusersbyfans);
			/****************17、情感值*****************/
			weiboUtils.emotionArrange(result, emotions);
			/***************微博来源分布****************/
			weiboUtils.sourceArrange(result, wbsource, existwb);
			/***************7、认证分布*****************/
			weiboUtils.verifyArrange(result, verifiedtype, existwb);
			/*****************层级分析*****************/
			result.setSuminclass(suminclass);
			/***************关键词提取*****************/
			weiboUtils.keywordsArrange(result, str);
			/***************总体评价*****************/
			weiboUtils.lastCommentArrange(result, allexposion, emotions, gender, location, reposterquality);
			/************结果数据存储************/
			jsonresult = JSONArray.fromObject(result);
			//			System.out.println(jsonresult);
			WeiboJDBC mysql = new WeiboJDBC("192.168.1.154"); // 存储到154上面
			if (mysql.mysqlStatus()) {
				mysql.insertWeiboResult(wid, url, jsonresult.toString());
				mysql.sqlClose();
			}
		} catch (WeiboException | SQLException | URIException e) {
			e.printStackTrace();
		}

		return apicount;
	}

	public void closeMysql() throws SQLException {
		weibojdbc.sqlClose();
	}

}
