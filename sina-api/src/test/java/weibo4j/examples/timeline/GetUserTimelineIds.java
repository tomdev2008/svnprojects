package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.json.JSONObject;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetUserTimelineIds {
	public static void main(String[] args) {
		//		String access_token = args[0];
		String access_token = new String("2.00dzHBCC05umf81823c6cbd7KZPJXE");
		String uid = new String("1862087393");

		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		try {
			//			JSONObject ids = tm.getUserTimelineIdsByUid(args[1]);
			JSONObject ids = tm.getUserTimelineIdsByUid(uid);
			Log.logInfo(ids.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
