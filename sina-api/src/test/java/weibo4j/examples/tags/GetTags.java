package weibo4j.examples.tags;

import java.util.List;

import com.sina.weibo.api.Tags;
import com.sina.weibo.model.Tag;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetTags {

	public static void main(String[] args) {
		String access_token = args[0];
		Tags tm = new Tags();
		tm.client.setToken(access_token);
		List<Tag> tags = null;
		String uid = args[1];
		try {
			tags = tm.getTags(uid);
			for (Tag tag : tags) {
				Log.logInfo(tag.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
