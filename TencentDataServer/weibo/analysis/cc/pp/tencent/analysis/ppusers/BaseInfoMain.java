package cc.pp.tencent.analysis.ppusers;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import cc.pp.tencent.analysis.user.baseinfo.BasedInfoGet;
import cc.pp.tencent.jdbc.PPJDBC;

import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.dao.impl.OtherInfo;
import com.tencent.weibo.dao.impl.OtherInfoData;
import com.tencent.weibo.oauthv1.OAuthV1;

public class BaseInfoMain {

	private String tablename = "";
	private String utablename = "";
	@SuppressWarnings("unused")
	private String ntablename = "";
	private final int MAX = 100;
	private String ip = "";

	public BaseInfoMain(String tablename, String utablename, String ntablename, String ip) {
		this.tablename = tablename;
		this.utablename = utablename;
		this.ntablename = ntablename;
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		BaseInfoMain baseinfo = new BaseInfoMain("tencentuserinfo", "pptencentusers", "tencentunusedusers",
				"192.168.1.188");
		while (true) {
			long time1 = System.currentTimeMillis() / 1000;
			baseinfo.importDataToDB();
			long time2 = System.currentTimeMillis() / 1000;
			long diff = time2 - time1;
			if (diff < 3600) {
				Thread.sleep(1000 * (3600 - diff));
			}
		}
	}

	public void importDataToDB() throws Exception {

		PPJDBC myjdbc = new PPJDBC(ip);
		if (myjdbc.mysqlStatus()) {
			/****************获取token数据*****************/
			String[] tokens = myjdbc.getAccessTokens(390); // 注意要添加token索引，否则很慢
			String[] accesstoken = new String[2];
			for (int i = 0; i < tokens.length; i++) {
				System.out.println(i);
				accesstoken[0] = tokens[i].substring(0, tokens[i].indexOf(","));
				accesstoken[1] = tokens[i].substring(tokens[i].indexOf(",") + 1);
				/****************获取sina用户名*****************/
				List<String> tencentusers = myjdbc.getTencentUsers(utablename, MAX); // 同样username要加索引
				/****************采集用户信息并存储***************/
				this.importDataToDbDis(accesstoken, tencentusers);
				/****************批量删除用户名******************/
				myjdbc.deleteBatch(utablename, tencentusers); // 注意要添加token索引，否则很慢
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * 把数据导入数据库
	 * @param accesstoken
	 * @param uids
	 * @throws Exception
	 */
	public void importDataToDbDis(String[] accesstoken, List<String> uids) throws Exception {

		PPJDBC myjdbc = new PPJDBC(ip);
		if (myjdbc.mysqlStatus()) {
			List<OtherInfoData> users = this.batchBaseinfo(accesstoken, uids);
			if (users != null) {
				for (OtherInfoData user : users) {
					long lastwbcreated = 0;
					try {
						lastwbcreated = user.getTweetinfo().get(0).getTimestamp();
						try {
							myjdbc.insertUsersInfo(tablename, user.getName(), user.getNick(), "0",
									user.getIsvip(), user.getFansnum(), user.getIdolnum(), user.getTweetnum(), user.getExp(),
									user.getIsrealname(), user.getLevel(), user.getSex(), user.getIndustry_code(),
									user.getIsent(), user.getProvince_code(), lastwbcreated, user.getVerifyinfo());
						} catch (RuntimeException e) {
							continue;
						}
					} catch (RuntimeException e) {
						continue;
					}
				}
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * 批量获取用户基础信息
	 * @param accesstoken
	 * @param uids，最大30个
	 * @return
	 * @throws Exception
	 */
	public List<OtherInfoData> batchBaseinfo(String[] accesstoken, List<String> uids) throws Exception {

		List<OtherInfoData> result = new ArrayList<OtherInfoData>();
		OAuthV1 oauth = new OAuthV1();
		BasedInfoGet.oauthInit(oauth, accesstoken[0], accesstoken[1]);
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String userinfo = "";
		ObjectMapper mapper = new ObjectMapper();
		for (String uid : uids) {
			userinfo = user.otherInfo(oauth, "json", uid, "");
			try {
				OtherInfo otherinfo = mapper.readValue(userinfo, OtherInfo.class);
				result.add(otherinfo.getData());
			} catch (UnrecognizedPropertyException e) {
				//
			} catch (JsonMappingException e) {
				// 
			} catch (EOFException e) {
				//
			}
		}

		return result;
	}
	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public static void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) {
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}

}

