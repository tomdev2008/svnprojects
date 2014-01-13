package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetRepostDaily {
	public static void main(String[] args) {
		String access_token = args[0];
		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		try {
			JSONArray status = tm.getRepostDaily();
			Log.logInfo(status.toString());

		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
