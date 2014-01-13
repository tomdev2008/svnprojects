package cc.pp.tencent.analysis.update.tokens;

import java.util.TimerTask;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.analysis.single.weibo.SimpleWeiboAnalysis;
import cc.pp.tencent.jdbc.WeiboJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

public class TaskUT extends TimerTask {

	@Override
	public void run() {
		try {
			WeiboJDBC weibojdbc = new WeiboJDBC("192.168.1.27");
			if (weibojdbc.mysqlStatus()) {
				String[] token = weibojdbc.getAccessToken();
				OAuthV1 oauth = new OAuthV1();
				SimpleWeiboAnalysis.oauthInit(oauth, token[0], token[1]);
				/************单条微博信息***************/
				TAPI weibo = new TAPI(oauth.getOauthVersion());
				String userinfo = weibo.show(oauth, "json", "198553008061952");
				JsonNode userdata = JsonUtils.getJsonNode(userinfo, "data");
				if (userdata.toString().length() < 10) {
					weibojdbc.deleteAccessToken(token[0]);
				}
				weibojdbc.sqlClose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
