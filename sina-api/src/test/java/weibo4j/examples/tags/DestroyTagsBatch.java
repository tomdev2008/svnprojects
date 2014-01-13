package weibo4j.examples.tags;

import java.util.List;

import com.sina.weibo.api.Tags;
import com.sina.weibo.model.Tag;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class DestroyTagsBatch {

	public static void main(String[] args) {
		String access_token = args[0];
		Tags tm = new Tags();
		tm.client.setToken(access_token);
		String ids = args[1];
		List<Tag> tags = null;
		try {
			tags = tm.destroyTagsBatch(ids);
			for (Tag t : tags) {
				Log.logInfo(t.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
