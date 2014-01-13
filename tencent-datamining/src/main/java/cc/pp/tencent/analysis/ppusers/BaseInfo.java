package cc.pp.tencent.analysis.ppusers;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import cc.pp.tencent.analysis.user.baseinfo.BasedInfoGet;
import cc.pp.tencent.jdbc.LocalJDBC;

import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.dao.impl.Infos;
import com.tencent.weibo.dao.impl.OtherInfo;
import com.tencent.weibo.dao.impl.OtherInfoData;
import com.tencent.weibo.dao.impl.SimpleUser;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * 注意添加token和username索引，在删除操作的时候
 * @author Administrator
 *
 */
public class BaseInfo {

	private String tablename = "";
	private String utablename = "";
	@SuppressWarnings("unused")
	private String ntablename = "";
	private final int MAX = 100;

	public BaseInfo(String tablename, String utablename, String ntablename) {
		this.tablename = tablename;
		this.utablename = utablename;
		this.ntablename = ntablename;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		BaseInfo baseinfo = new BaseInfo("tencentuserinfo", "tencentusers", "tencentunusedusers");
		baseinfo.importDataToDB();
	}

	public void importDataToDB() throws Exception {

		LocalJDBC myjdbc = new LocalJDBC();
		if (myjdbc.mysqlStatus()) {
			/****************获取token数据*****************/
			String[] tokens = myjdbc.getAccessTokens(100); // 注意要添加token索引，否则很慢
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
	public void importDataToDbDis1(String[] accesstoken, List<String> uids) throws Exception {

		LocalJDBC myjdbc = new LocalJDBC();
		if (myjdbc.mysqlStatus()) {
			List<SimpleUser> users = this.batchBaseinfo1(accesstoken, uids);
			if (users != null) {
				for (SimpleUser user : users) {
					myjdbc.insertUsersInfo(tablename, user.getName(), user.getName(), user.getIsvip(), //
							user.getFansnum(), user.getIdolnum(), user.getExp(), user.getIsrealname(), //
							user.getLevel(), user.getSex());
				}
			}
			myjdbc.sqlClose();
		}
	}

	public void importDataToDbDis(String[] accesstoken, List<String> uids) throws Exception {

		LocalJDBC myjdbc = new LocalJDBC();
		if (myjdbc.mysqlStatus()) {
			List<OtherInfoData> users = this.batchBaseinfo(accesstoken, uids);
			if (users != null) {
				for (OtherInfoData user : users) {
					long lastwbcreated = 0;
					try {
						lastwbcreated = user.getTweetinfo().get(0).getTimestamp();
					} catch (RuntimeException e) {
						//
					}
					myjdbc.insertUsersInfo(tablename, user.getName(), user.getNick(), user.getIntroduction(),
							user.getIsvip(), user.getFansnum(), user.getIdolnum(), user.getTweetnum(), user.getExp(),
							user.getIsrealname(), user.getLevel(), user.getSex(), user.getIndustry_code(),
							user.getIsent(), user.getProvince_code(), lastwbcreated, user.getVerifyinfo());
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
	public List<SimpleUser> batchBaseinfo1(String[] accesstoken, List<String> uids) throws Exception {

		OAuthV1 oauth = new OAuthV1();
		BasedInfoGet.oauthInit(oauth, accesstoken[0], accesstoken[1]);
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String uidstr = "";
		for (String uid : uids) {
			uidstr += uid + ",";
		}
		uidstr = uidstr.substring(0, uidstr.length() - 1);
		String usersinfo = user.infos(oauth, "json", uidstr, "");
		ObjectMapper mapper = new ObjectMapper();
		Infos userinfos = new Infos();
		try {
			userinfos = mapper.readValue(usersinfo, Infos.class);
		} catch (UnrecognizedPropertyException e) {
			/**
			 * {"data":null,"detailerrinfo":{"accesstoken":"a54ba6d122484bcb95e4e1514eff01e3",
			 * "apiname":"weibo.user.infos","appkey":"11b5a3c188484c3f8654b83d32e19bab",
			 * "clientip":"122.224.126.3","cmd":0,"proctime":0,"ret1":3,"ret2":3,"ret3":-1,
			 * "ret4":1210221834,"timestamp":1376410022},"errcode":-1,"msg":"check sign error",
			 * "ret":3,"seqid":5911636026086435598}
			 */
			return null;
			//			System.out.println(usersinfo);
		}

		return userinfos.getData().getInfo();
	}

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
