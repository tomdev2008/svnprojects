package cc.pp.sina.analysis.single.weibo;

import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;
import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.analysis.common.Analysis;
import cc.pp.sina.common.MidToId;
import cc.pp.sina.jdbc.WeiboJDBC;
import cc.pp.sina.result.BasedSingleWeiboResult;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class BasedWeiboAnalysis1 extends Analysis {
	
	/**
	 * 默认的序列化的版本号
	 */
	private static final long serialVersionUID = 1L;

	public BasedWeiboAnalysis1(String ip) {
		super(ip);
	}

	/**
	 * @param args
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException {
		// 
		BasedWeiboAnalysis1 bwa = new BasedWeiboAnalysis1("192.168.1.151");
		String url = new String("http://e.weibo.com/1807956030/zwOZttkEM");
		long time = System.currentTimeMillis();
		String[] resultString = bwa.analysis(url);
		System.out.println(resultString[0]);
		System.out.println(resultString[1]);
		System.out.println((System.currentTimeMillis()-time)/1000);
	}

	@Override
	public String[] analysis(String url) {
		// 
		try {
			int apicount = 0;
			/**************************************/
			String[] tworesult = new String[2];
			JSONArray jsonresult = null;
			WeiboUtils weiboUtils = new WeiboUtils();
			WeiboJDBC weibomysql = new WeiboJDBC(this.ip);
			if (weibomysql.mysqlStatus())
			{
				BasedSingleWeiboResult result = new BasedSingleWeiboResult();
				/**********提取accesstoken*************/
				String accesstoken = weibomysql.getAccessToken();
				Timeline tm = new Timeline();
				tm.client.setToken(accesstoken);
				MidToId midtoid = new MidToId();
				String wid = midtoid.mid2id(url);
				/*************变量初始化****************/
				Status wbinfo = tm.showStatus(wid);
				apicount++;
				result.setRepostCount(Integer.toString(wbinfo.getRepostsCount()));    //1、转发量
				result.setCommentCount(Integer.toString(wbinfo.getCommentsCount()));  //2、评论量
				HashMap<String,Integer> reposttimelinebyDay = new HashMap<String,Integer>();  //3、转发时间线--按天
				int[] reposttimelineby24H = new int[24];  //按24小时内
				int[] gender = new int[2];  //4、性别分布
				int[] location = new int[101];  //6、区域分布
				int[] verifiedtype = new int[14];; //7、认证分布
				int[] exposionby24H = new int[24];  //按24小时内
				int[] repostergrade = new int[5];  //9、转发用户等级（按粉丝量）
				int[] addVRatio = new int[2];  //10、加V比例
				int[] reposterquality = new int[2]; //11、转发用户质量（水军比例）
				int exposionsum = 0;  //12、总曝光量
				String[] keyusers = new String[50]; //15、关键账号
				weiboUtils.initStrArray(keyusers);
				String[] keyusersbyfans = new String[50]; //15、关键账号,按粉丝量
				weiboUtils.initStrArray(keyusersbyfans);
				int[] commentemotion = new int[2];  //17、评论信息情感值
				String[] negativecomments = new String[20];   //18、负面评论信息
				weiboUtils.initStrArray(negativecomments);
				int existwb = 0, wbsum = 0, comsum = 0;
				String head, name, sex, hour, date;
				int repostercount, city, fanssum, weibosum;
				boolean addv;
				int verifytype;
				long reposttime;
				/***************************************采集转发信息***********************************************/
				int cursor = 1;
				Paging page = new Paging(cursor, 200);
				StatusWapper status = tm.getRepostTimeline(wid, page);
				apicount++;
				wbsum = (int) status.getTotalNumber();
				if (wbinfo.getUser() == null) {     //转发微博0时
					tworesult[0] = "20003";
					tworesult[1] = "0";
					return tworesult;
				} else {
					exposionsum += wbinfo.getUser().getFollowersCount();
				}

				/************开始循环计算***************/
				while ((cursor*200 < wbsum + 200) && (cursor < 1000))
				{
					System.out.println(cursor);
					if (cursor % 100 == 0) {
						accesstoken = weibomysql.getAccessToken();
						tm.client.setToken(accesstoken);
					}
					
					for(Status reposter : status.getStatuses())
					{
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
						verifytype = reposter.getUser().getVerifiedType();
						reposttime = reposter.getCreatedAt().getTime()/1000;
						hour = weiboUtils.getHour(reposttime);
						date = weiboUtils.getDate(reposttime);
						/****************15、关键账号****************/
						keyusers = InsertSort.toptable(keyusers, name + "," + head + "=" + repostercount);
						keyusersbyfans = InsertSort.toptable(keyusersbyfans, name + "," + head + "=" + fanssum);
						/************3、转发时间线（24小时）************/
						reposttimelineby24H[Integer.parseInt(hour)]++;
						/************3、转发时间线（按天）************/
						weiboUtils.putRepostByDay(reposttimelinebyDay, date);
						/***************4、性别分布*****************/
						gender[weiboUtils.checkSex(sex)]++;
						/***************6、区域分布*****************/
						location[weiboUtils.checkCity(city)]++;
						/***************7、认证分布*****************/
						verifiedtype[weiboUtils.checkVerifyType(verifytype)]++;
						/************8、曝光时间线（24小时）************/
						exposionby24H[Integer.parseInt(hour)]++;
						/***************10、加V比例*****************/
						addVRatio[weiboUtils.checkAddV(addv)]++;
						/***************11、水军比例****************/
						reposterquality[weiboUtils.checkQuality(addv, fanssum, weibosum)]++;
						/***********9、转发用户等级（按粉丝量）**********/
						repostergrade[weiboUtils.checkGradeByFans(fanssum)]++; 
						/***************12、总曝光量**************/
						exposionsum += fanssum;
					}
					
					try {
						cursor++;
						page = new Paging(cursor, 200);;
						status = tm.getRepostTimeline(wid, page);
						apicount++;
					} catch (RuntimeException e) {
						cursor++;
					}
				}
				/**********************采集评论信息************************/
				/**********************整理数据结果************************/
				/************3、转发时间线（24小时）************/
				weiboUtils.Reposttime24HArrange(result, reposttimelineby24H, existwb);
				/************3、转发时间线（按天）************/
				result.setReposttimelineByDay(reposttimelinebyDay);
				/***************4、性别分布*****************/
				weiboUtils.genderArrange(result, gender, existwb);
				/***************6、区域分布*****************/
				weiboUtils.locationArrange(result, location, existwb);
				/***************7、认证分布*****************/
				weiboUtils.verifyArrange(result, verifiedtype, existwb);
				/************8、曝光时间线（24小时）************/
				weiboUtils.exposion24HArrange(result, exposionby24H, existwb);
				/***************10、加V比例*****************/
				result.setAddVRatio(Float.toString((float)Math.round(((float)addVRatio[1]/existwb)*10000)/100) + "%");
				/***************11、水军比例****************/
				weiboUtils.qualityArrange(result, reposterquality, existwb);
				/***********9、转发用户等级（按粉丝量）**********/
				weiboUtils.gradeByFansArrange(result, repostergrade, existwb);
				/***************12、总曝光量**************/
				result.setExposionSum(Integer.toString(exposionsum));
				/****************15、关键账号****************/
				weiboUtils.keyusersArrange(result, keyusers);
				weiboUtils.keyusersArrange(result, keyusersbyfans);
				/****************17、评论信息情感值*****************/
				weiboUtils.comemotionArrange(result, commentemotion, comsum);
				/****************18、负面评论信息******************/
				weiboUtils.negcomArrange(result, negativecomments);
				/***************数据转换与存储**************/
				jsonresult = JSONArray.fromObject(result);

				weibomysql.sqlClose();
			}
			tworesult[0] = jsonresult.toString();
			tworesult[1] = Integer.toString(apicount);

			return tworesult;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (WeiboException e) {
			e.printStackTrace();
			return null;
		} 
	}

}
