package cc.pp.sina.require.ppdongli.single.weibo;

import java.sql.SQLException;

import net.sf.json.JSONArray;
import cc.pp.sina.analysis.common.Analysis;
import cc.pp.sina.common.MidToId;
import cc.pp.sina.jdbc.WeiboJDBC;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class SingleWeiboAnalysis1 extends Analysis {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = -4980658229991475129L;

	public SingleWeiboAnalysis1(String ip) {
		super(ip);
	}

	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		
		SingleWeiboAnalysis1 bwa = new SingleWeiboAnalysis1("192.168.1.151");
		String url = new String("weibo.com/3212765521/zCUEsxMwz");
		MidToId midtoid = new MidToId();
		String wid = midtoid.mid2id(url);
		String[] resultString = bwa.analysis(wid);
		System.out.println(resultString[0]);
		System.out.println(resultString[1]);

	}

	/**
	 * 分析函数
	 */
	@Override
	public String[] analysis(String wid) {

		try {
			int apicount = 0;
			/**************************************/
			String[] tworesult = new String[2];
			JSONArray jsonresult = null;
			WeiboUtils weiboUtils = new WeiboUtils();
			WeiboJDBC weibomysql = new WeiboJDBC(this.ip);
			if (weibomysql.mysqlStatus())
			{
				UnionResult result = new UnionResult();
				/**********提取accesstoken*************/
				String accesstoken = weibomysql.getAccessToken();
				Timeline tm = new Timeline();
				tm.client.setToken(accesstoken);
				Status wbinfo = tm.showStatus(wid);
				apicount++;
				/*************获取用户信息****************/
				weiboUtils.oriUserArrange(result.getOriuserinfo(), wbinfo.getUser());
				/*************获取微博信息****************/
				weiboUtils.oriwbArrange(result.getOriwbinfo(), wbinfo);
				/*************变量初始化****************/
				int[] reposttimelineby24H = new int[24]; //3、转发时间线--按24小时内
				int[] gender = new int[2]; //4、性别分布
				int[] location = new int[101]; //6、区域分布
				int[] verifiedtype = new int[14];
				; //7、认证分布
				int[] addVRatio = new int[2]; //10、加V比例
				int[] reposterquality = new int[2]; //11、转发用户质量（水军比例）
				int exposionsum = 0; //12、总曝光量
				
				int existwb = 0, wbsum = 0, city, fanssum, weibosum;
				String sex, hour;
				boolean addv;
				int verifytype;
				long reposttime;
				/***************************************采集转发信息***********************************************/
				int cursor = 1;
				Paging page = new Paging(cursor, 200);
				StatusWapper status = tm.getRepostTimeline(wid, page);
				apicount++;
				wbsum = (int) status.getTotalNumber();
				if (wbinfo.getUser() == null) { //转发微博0时
					tworesult[0] = "20003";
					tworesult[1] = "0";
					return tworesult;
				} else {
					exposionsum += wbinfo.getUser().getFollowersCount();
				}
				if (wbsum == 0) {
					result.setRepinfo(null);
					jsonresult = JSONArray.fromObject(result);
					tworesult[0] = jsonresult.toString();
					tworesult[1] = Integer.toString(apicount);
					return tworesult;
				}
				/************开始循环计算***************/
				while ((cursor*200 < wbsum + 200) && (cursor < 10))
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
						sex = reposter.getUser().getGender();
						city = reposter.getUser().getProvince();
						fanssum = reposter.getUser().getFollowersCount();
						weibosum = reposter.getUser().getStatusesCount();
						addv = reposter.getUser().isVerified();
						verifytype = reposter.getUser().getVerifiedType();
						reposttime = reposter.getCreatedAt().getTime()/1000;
						hour = weiboUtils.getHour(reposttime);
						/************3、转发时间线（24小时）************/
						reposttimelineby24H[Integer.parseInt(hour)]++;
						/***************4、性别分布*****************/
						gender[weiboUtils.checkSex(sex)]++;
						/***************6、区域分布*****************/
						location[weiboUtils.checkCity(city)]++;
						/***************7、认证分布*****************/
						verifiedtype[weiboUtils.checkVerifyType(verifytype)]++;
						/***************10、加V比例*****************/
						addVRatio[weiboUtils.checkAddV(addv)]++;
						/***************11、水军比例****************/
						reposterquality[weiboUtils.checkQuality(addv, fanssum, weibosum)]++;
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
				weiboUtils.Reposttime24HArrange(result.getRepinfo(), reposttimelineby24H, existwb);
				/***************4、性别分布*****************/
				weiboUtils.genderArrange(result.getRepinfo(), gender, existwb);
				/***************6、区域分布*****************/
				weiboUtils.locationArrange(result.getRepinfo(), location, existwb);
				/***************7、认证分布*****************/
				weiboUtils.verifyArrange(result.getRepinfo(), verifiedtype, existwb);
				/***************10、加V比例*****************/
				result.getRepinfo().setAddVRatio(Float.toString((float)Math.round(((float)addVRatio[1]/existwb)*10000)/100) + "%");
				/***************11、水军比例****************/
				weiboUtils.qualityArrange(result.getRepinfo(), reposterquality, existwb);
				/***************12、总曝光量**************/
				// 以转发微博总数为主，而不是粉丝量
				result.getRepinfo().setExposionsum(Long.toString(exposionsum * status.getTotalNumber() / existwb));
				/***************数据转换与存储**************/
				jsonresult = JSONArray.fromObject(result);

				weibomysql.sqlClose();
			}
			if (jsonresult.toString() == null) {
				tworesult[0] = "20003";
			} else {
				tworesult[0] = jsonresult.toString();
			}
			tworesult[1] = Integer.toString(apicount);

			return tworesult;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (WeiboException e) {
			e.printStackTrace();
			return null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}
	}

}
