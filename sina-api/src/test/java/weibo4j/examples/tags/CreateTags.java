package weibo4j.examples.tags;

import com.sina.weibo.api.Tags;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class CreateTags {

	public static void main(String[] args) {
		String access_token = args[0];
		String tag = args[1];
		Tags tm = new Tags();
		tm.client.setToken(access_token);
		try {
			JSONArray tags = tm.createTags(tag);
			Log.logInfo(tags.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
