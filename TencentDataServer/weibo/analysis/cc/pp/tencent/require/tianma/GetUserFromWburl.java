package cc.pp.tencent.require.tianma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import cc.pp.tencent.jdbc.LocalJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.OtherInfo;
import com.tencent.weibo.oauthv1.OAuthV1;

public class GetUserFromWburl {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws Exception {

		LocalJDBC myjdbc = new LocalJDBC();
		if (myjdbc.mysqlStatus()) {
			String[] token = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
			OAuthV1 oauth = new OAuthV1();
			OauthInit.oauthInit(oauth, token[0], token[1]);
			UserAPI user = new UserAPI(oauth.getOauthVersion());
			ObjectMapper mapper = new ObjectMapper();
			TAPI weibo = new TAPI(oauth.getOauthVersion());
			BufferedReader br = new BufferedReader(new FileReader(new File("testdata/tencentuser.txt")));
			String uid = "";
			while ((uid = br.readLine()) != null) {
				String wid = uid.substring(uid.lastIndexOf("/") + 1);
				String userinfo = weibo.show(oauth, "json", wid);
				JsonNode userdata = JsonUtils.getJsonNode(userinfo, "data");
				String users = user.otherInfo(oauth, "json", userdata.get("name").toString().replaceAll("\"", ""), "");
				OtherInfo otherinfo = mapper.readValue(users, OtherInfo.class);
				myjdbc.insertTempsinauserinfo("ztencentuserinfo", userdata.get("name").toString().replaceAll("\"", ""),
						userdata.get("nick").toString().replaceAll("\"", ""), otherinfo.getData().getSex(), userdata
								.get("location").toString().replaceAll("\"", "").replaceAll("中国", ""),
						userdata.get("isrealname").toString().replaceAll("\"", ""));
			}
			br.close();
			myjdbc.sqlClose();
		}

	}

}
