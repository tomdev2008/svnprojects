package weibo4j.examples.account;

import com.sina.weibo.api.Account;
import com.sina.weibo.json.JSONObject;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetAccountPrivacy {

	public static void main(String[] args) {
		String access_token = args[0];
		Account am = new Account();
		am.client.setToken(access_token);
		try {
			JSONObject json = am.getAccountPrivacy();
			Log.logInfo(json.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
