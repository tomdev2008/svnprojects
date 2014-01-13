package cc.pp.sina.analysis.single.weibo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import weibo4j.Timeline;
import weibo4j.model.Status;
import cc.pp.nlp.keyword.test.KeyWord;
import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.common.Emotion;
import cc.pp.sina.common.SourceType;
import cc.pp.sina.domain.RepInfo;
import cc.pp.sina.domain.RepUser;
import cc.pp.sina.domain.RepWeibo;
import cc.pp.sina.domain.SimpleWeiboResult;
import cc.pp.sina.sql.SpiderJDBC;
import cc.pp.sina.utils.MidToId;
import cc.pp.sina.utils.SWeiboUtils;

public class SingleWeiboAnalysis {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// 
		SingleWeiboAnalysis singleWeiboAnalysis = new SingleWeiboAnalysis();
		String wburl = "http://weibo.com/3345820740/A5igBaQUX";
		int index = singleWeiboAnalysis.analysis(wburl);
		System.out.println(index);

	}

	/**
	 * 分析数据
	 * @throws Exception 
	 */
	@SuppressWarnings("unused")
	public int analysis(String url) throws Exception {

		List<RepInfo> repinfos = new ArrayList<RepInfo>();
		// 从数据库拉取转发微博和转发用户信息
		SpiderJDBC myJdbc = new SpiderJDBC("192.168.1.26");
		if (myJdbc.mysqlStatus()) {
			repinfos = myJdbc.getRepInfos("repinfos", 7000);
			myJdbc.sqlClose();
		}

		/***********结果变量初始化***********/
		SimpleWeiboResult result = new SimpleWeiboResult();
		SWeiboUtils weiboUtils = new SWeiboUtils();
		Emotion emotion = new Emotion();
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
		int existwb = repinfos.size(), wbsum = 0, repostercount, cursor = 1;
		String head, name, sex, hour, text, source, date;
		int city, fanssum, weibosum, verifytype;
		boolean addv;
		long reposttime;
		/***********获取该微博数据************/
		//		String accesstoken = weibojdbc.getAccessToken();
		String accesstoken = "2.00OKXevBdcZIJCee8ff5f68dtzKiEB";
		Timeline tm = new Timeline();
		tm.client.setToken(accesstoken);
		MidToId midtoid = new MidToId();
		if (url.indexOf("?") != -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		String wid = midtoid.mid2id(url);
		Status wbinfo = tm.showStatus(wid);
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

		/************************分析转发数据******************************/
		for (RepInfo reposter : repinfos) {

			name = reposter.getNickname();
			sex = reposter.getGender();
			head = "";
			city = reposter.getProvince();
			fanssum = reposter.getFanscount();
			weibosum = reposter.getWeibocount();
			addv = reposter.isVerify();
			verifytype = reposter.getVerifytype();
			//			repostercount = reposter.getRepostsCount();
			//			reposttime = reposter.getCreatedAt().getTime() / 1000;
			//			hour = weiboUtils.getHour(reposttime);
			//			date = weiboUtils.getDate(reposttime);
			source = reposter.getSource();
			text = reposter.getWeibotext();
			str += text;
			/****************5、关键账号****************/
			//			keyusersbyreps = InsertSort.toptable(keyusersbyreps, reposter.getUsername() + "," + name + "," + head + ","
			//					+ fanssum + "," + verifytype + "," + reposttime + "=" + repostercount);
			keyusersbyfans = InsertSort.toptable(keyusersbyreps,
					reposter.getUsername() + "," + name + "," + head + "," + fanssum + "," + verifytype + ","
							+ reposter.getCreateat().substring(reposter.getCreateat().indexOf(",") + 1,
									reposter.getCreateat().length() - 1) + "=" + fanssum);
			/************6、转发时间线（24小时）************/
			//			reposttimelineby24H[Integer.parseInt(hour)]++;
			/************3、转发时间线（按天）**************/
			//			weiboUtils.putRepostByDay(reposttimelinebyDay, date);
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
			//			if (cursor < 2) {
			//				weiboUtils.setEmotions(emotion, emotions, text);
			//			}
			/***************认证分布*****************/
			verifiedtype[weiboUtils.checkVerifyType(verifytype)]++;
			/***************层级分析*****************/
			weiboUtils.putSumInClass(suminclass, text);

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
		/********************微博来源分布**********************/
		weiboUtils.sourceArrange(result, wbsource, existwb);
		/***************7、认证分布*****************/
		weiboUtils.verifyArrange(result, verifiedtype, existwb);
		/*****************层级分析*****************/
		result.setSuminclass(suminclass);
		/***************关键词提取*****************/
		//				weiboUtils.keywordsArrange(result, str);
		KeyWord mykeyword = new KeyWord();
		result.setKeys(mykeyword.extractKeyword(str, "", 50));
		/***************总体评价*****************/
		weiboUtils.lastCommentArrange(result, allexposion, emotions, gender, location, reposterquality);
		/************结果数据存储************/
		System.out.println(JSONArray.fromObject(result));

		return 0;
	}

	public void unionRepUserAndWeibo() throws SQLException {

		HashMap<String, RepUser> repusers = new HashMap<String, RepUser>();
		HashMap<String, RepWeibo> repweibos = new HashMap<String, RepWeibo>();
		// 从数据库拉取转发微博和转发用户信息
		SpiderJDBC myJdbc = new SpiderJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			repusers = myJdbc.getRepUserInfos("repuserinfo");
			repweibos = myJdbc.getRepWeiboInfo("repweiboinfo");
			for (Entry<String, RepUser> temp : repusers.entrySet()) {
				RepWeibo repweibo = repweibos.get(temp.getKey());
				RepUser repuser = temp.getValue();
				myJdbc.insertRepInfo("repinfos", repuser.getUsername(), repuser.getNickname(), "", repuser.getGender(),
						repuser.getProvince(), repuser.getFanscount(), repuser.getFriendscount(),
						repuser.getWeibocount(), repuser.isVerify(), repuser.getVerifytype(),
						repuser.getVerifiedreason(), repuser.getLastwbcreated(), repweibo.getWeibotext(),
						repweibo.getCreateat(), repweibo.getSource());
			}
			myJdbc.sqlClose();
		}
	}

}
