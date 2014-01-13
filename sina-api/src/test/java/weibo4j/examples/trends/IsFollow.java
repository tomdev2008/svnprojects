package weibo4j.examples.trends;

import com.sina.weibo.api.Trend;
import com.sina.weibo.json.JSONObject;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class IsFollow {

	public static void main(String[] args) {
		//		String access_token = args[0];
		//		String trend_name = args[1];
		String access_token = new String("2.00dzHBCC05umf81823c6cbd7KZPJXE");
		String trend_name = new String("孔明社交管理");

		Trend tm = new Trend();
		tm.client.setToken(access_token);
		try {
			JSONObject result = tm.isFollow(trend_name);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
