package cc.pp.tencent.require.ppdongli.single.weibo;

import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.analysis.common.Analysis;
import cc.pp.tencent.jdbc.WeiboJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

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
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		SingleWeiboAnalysis1 bwa = new SingleWeiboAnalysis1("192.168.1.151");
		String url = new String("http://t.qq.com/p/t/256405090178560");
		String wid = WeiboUtils.getWid(url);
		String[] resultString = bwa.analysis(wid);
		System.out.println(resultString[0]);
		System.out.println(resultString[1]);

	}

	/**
	 * 分析函数
	 * @throws Exception 
	 */
	@SuppressWarnings("unused")
	@Override
	public String[] analysis(String wid) {
		// TODO Auto-generated method stub
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
				String[] token = WeiboUtils.getAccessToken(this.ip);
				OAuthV1 oauth = new OAuthV1();
				weiboUtils.oauthInit(oauth, token[0], token[1]);
				TAPI tapi = new TAPI(oauth.getOauthVersion());
				String wbinfo = tapi.show(oauth, "json", wid);
				apicount++;				
				/*************获取微博信息****************/
				String username = null;
				try {
					username = weiboUtils.oriWbArrange(result, wbinfo);
				} catch (RuntimeException e) {
					tworesult[0] = "20003";
					tworesult[1] = "1";
					return tworesult;
				}
				/*************获取用户信息****************/
				UserAPI user = new UserAPI(oauth.getOauthVersion());
				String userinfo = user.otherInfo(oauth, "json", username, "");
				
				weiboUtils.oriUserArrange(result, userinfo);
				/*************变量初始化****************/
				int[] reposttimelineby24H = new int[24];  //3、转发时间线--按24小时内
				int[] gender = new int[3];  //4、性别分布
				int[] location = new int[101];  //6、区域分布
				int[] addVRatio = new int[3];  //10、加V比例,0-老用户，1-已实名认证，2-未实名认证
				int[] reposterquality = new int[2]; //11、转发用户质量（水军比例）
				int exposionsum = 0;  //12、总曝光量
				
				int[] viptype = new int[14];; //7、VIP类型
				int[] isself = new int[2] ; //9、是否自己发的微博 
				int[] weibotype = new int[8]; //12、微博类型,1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
				int[] weibostatus = new int[5]; //14、微博状态,0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
				HashMap<Integer,String> usernames = new HashMap<Integer,String>(); //存放用户名
				int existwb = 0, wbsum = 0, city;
				String hour, text;
				int addv;  //加V信息
				long reposttime;
				/**********************转发数据****************************/
				int comsum = 0, self; //是否自己发的微博
				String citys, uid;
				int wbtype; //微博类型,1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
				//微博状态,0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
				int isvip, wbstatus; 
				JsonNode weibodata, jsoninfo;
				/**********************转发数据****************************/
				TAPI weibo = new TAPI(oauth.getOauthVersion());
				String weiboinfo = weibo.reList(oauth, "json", "0", wid, "1", "0", "100", "0");
			    apicount++;
				weibodata = JsonUtils.getJsonNode(weiboinfo);
				if (!weibodata.get("errcode").toString().equals("0") || (weibodata.get("data") == null)) {  //返回错误信息处理
					tworesult[0] = "20003";
					tworesult[1] = "1";
					return tworesult;
				}
				jsoninfo = weibodata.get("data").get("info");
				if (jsoninfo == null) {
					tworesult[0] = "20003";
					tworesult[1] = "1";
					return tworesult;
				}
				//1、转发量
				wbsum = Integer.parseInt(weibodata.get("data").get("totalnum").toString());
				
	            int cursor = 0;
	            String lastreposttime = null;
	            String lastwid = null;
	            int index = 99;
				while ((cursor*100 < wbsum) && (apicount < 10)) 
				{					
					jsoninfo = weibodata.get("data").get("info");
					if (jsoninfo == null) {
						break;
					}
					for (int i = 0; i < 100; i++)
					{	
						if (jsoninfo.get(i) == null) {
							index = i - 1;
							break;
						}
						existwb++;
						uid = jsoninfo.get(i).get("name").toString().replaceAll("\"", "");
						usernames.put(existwb, uid);
						text = jsoninfo.get(i).get("text").toString().replaceAll("\"", "");
						self = Integer.parseInt(jsoninfo.get(i).get("self").toString());
						wbtype = Integer.parseInt(jsoninfo.get(i).get("type").toString());
						citys = jsoninfo.get(i).get("province_code").toString();
						if (citys.length() == 2) {
							city = 0;
						} else {
							city = Integer.parseInt(citys.substring(1,citys.length()-1));
						}
						isvip = Integer.parseInt(jsoninfo.get(i).get("isvip").toString());
						wbstatus = Integer.parseInt(jsoninfo.get(i).get("status").toString());
						addv = Integer.parseInt(jsoninfo.get(i).get("isrealname").toString());
						reposttime = Integer.parseInt(jsoninfo.get(i).get("timestamp").toString());
						hour = weiboUtils.getHour(reposttime);
						/**************************6、区域分布*********************************/
						location[weiboUtils.checkCity(city)]++;
						/**************************7、VIP类型*********************************/
						viptype[isvip]++;;
						/************************9、是否自己发的微博******************************/
						isself[self]++; 
						/**************************10、加V比例*********************************/
						addVRatio[addv]++;
						/********************11、转发用户质量（水军比例）****************************/
						reposterquality[weiboUtils.checkQuality(text)]++;
						/**************************12、微博类型*********************************/
						weibotype[wbtype]++; //1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
						/**************************13、心情类型*********************************/
						/**************************14、微博状态*********************************/
						weibostatus[wbstatus]++; //0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
					}
					lastreposttime = jsoninfo.get(index).get("timestamp").toString();
					lastwid = jsoninfo.get(index).get("id").toString();
					weiboinfo = weibo.reList(oauth, "json", "0", wid, "1", lastreposttime, "100", lastwid);
					cursor++;
					apicount++;
					weibodata = JsonUtils.getJsonNode(weiboinfo);
					if (weibodata.get("data") == null) {  //返回错误信息处理
						break;
					}
				}
				/**************************用户数据*********************************/	
				token = WeiboUtils.getAccessToken(this.ip);
				weiboUtils.oauthInit(oauth, token[0], token[1]);
				user = new UserAPI(oauth.getOauthVersion());
				String uids = null;
				JsonNode userdata;
				int sex, fanssum, usersum = 0, count = usernames.size() / 30;
				count = (count < 10) ? count : 10;
				for (int i = 0; i < count; i++)
				{
					for (int j = 0; j < 30; j++) {
						uids += usernames.get(i*30+j) + ",";
					}
					uids = uids.substring(0, uids.length()-1);
					userinfo = user.infos(oauth, "json", uids, "");
					userdata = JsonUtils.getJsonNode(userinfo);
					if (!userdata.get("errcode").toString().equals("0")) {
						continue;
					}
					jsoninfo = userdata.get("data").get("info");
					if (jsoninfo == null) {
						continue;
					}
					for (int j = 0; j < 30; j++) 
					{
						if (jsoninfo.get(i) == null) {
						    break;
						}
						usersum++;
						fanssum = Integer.parseInt(jsoninfo.get(i).get("fansnum").toString());
						sex = Integer.parseInt(jsoninfo.get(i).get("sex").toString());
						/*************************4、性别分布****************************/
						gender[sex]++; //1-男，2-女，0-未填写
						/*********************11、转发用户质量（水军比例）**********************/
						reposterquality[weiboUtils.checkfans(fanssum)]++;
						/*************************12、总曝光****************************/
						exposionsum += fanssum;
					}
					this.apicount++;
				}
				/************************数据整理*******************************/
				/***************3、转发时间线--按天**************/
				weiboUtils.Reposttime24HArrange(result.getRepinfo(), reposttimelineby24H, existwb);
				/**************************6、区域分布*********************************/
				weiboUtils.locationArrange(result.getRepinfo(), location, existwb);
				/**************************7、VIP类型*********************************/
				weiboUtils.vipTypeArrange(result.getRepinfo(), viptype, existwb);
				/************************9、是否自己发的微博******************************/
				weiboUtils.isselfArrange(result.getRepinfo(), isself, existwb);
				/**************************10、加V比例*********************************/
				weiboUtils.verifyArrange(result.getRepinfo(), addVRatio, existwb);
				/********************11、转发用户质量（水军比例）****************************/
				weiboUtils.qualityArrange(result.getRepinfo(), reposterquality);
				/**************************12、微博类型*********************************/
				weiboUtils.weibotypeArrange(result.getRepinfo(), weibotype, existwb);
				/**************************14、微博状态*********************************/
				weiboUtils.wbStatusArrange(result.getRepinfo(), weibostatus, existwb);
				/****************************12、总曝光量******************************/
				result.getRepinfo().setExposionsum(Long.toString((long) exposionsum * wbsum / usersum));
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

}
