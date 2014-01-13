package weibo4j.examples.tags;

import com.sina.weibo.api.Tags;
import com.sina.weibo.json.JSONObject;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class DestroyTag {

	public static void main(String[] args) {
		String access_token = args[0];
		Tags tm = new Tags();
		tm.client.setToken(access_token);
		int tag_id = Integer.parseInt(args[1]);
		try {
			JSONObject result = tm.destoryTag(tag_id);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {

			e.printStackTrace();
		}
	}

}
