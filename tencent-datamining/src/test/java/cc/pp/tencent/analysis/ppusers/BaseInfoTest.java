package cc.pp.tencent.analysis.ppusers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;

import cc.pp.tencent.analysis.user.baseinfo.BasedInfoGet;

import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.dao.impl.Infos;
import com.tencent.weibo.dao.impl.SimpleUser;
import com.tencent.weibo.oauthv1.OAuthV1;

public class BaseInfoTest {

	@Test
	public void batchBaseinfoTest() throws Exception {
		String[] accesstoken = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
		BaseInfo baseinfo = new BaseInfo("sinauserinfo", "sinausers", "sinaunusedusers");
		List<String> uids = new ArrayList<String>();
		uids.add("herky828");
		uids.add("lickyfun");
		uids.add("langdao2010");
		List<SimpleUser> userinfos = baseinfo.batchBaseinfo1(accesstoken, uids);
		assertEquals(3, userinfos.size());
	}

	@Ignore
	public void Test() throws Exception {
		/********获取accesstoken和用户username*********/
		String[] accesstoken = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
		/*****************采集用户信息******************/
		OAuthV1 oauth = new OAuthV1();
		BasedInfoGet.oauthInit(oauth, accesstoken[0], accesstoken[1]);
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String uids = "herky828,lickyfun,langdao2010";
		String usersinfo = user.infos(oauth, "json", uids, "");
		ObjectMapper mapper = new ObjectMapper();
		Infos userinfos = mapper.readValue(usersinfo, Infos.class);
		System.out.println(usersinfo);
		System.out.println(JSONObject.fromObject(userinfos.getData().getInfo().get(0)));
		assertEquals(3, userinfos.getData().getInfo().size());
	}

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
