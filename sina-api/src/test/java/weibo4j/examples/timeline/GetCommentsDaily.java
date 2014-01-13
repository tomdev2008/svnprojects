package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetCommentsDaily {
	public static void main(String[] args) {
		//		String access_token = args[0];
		String access_token = new String("2.00dzHBCC05umf81823c6cbd7KZPJXE");

		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		try {
			JSONArray status = tm.getCommentsDaily();
			Log.logInfo(status.toString());

		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
