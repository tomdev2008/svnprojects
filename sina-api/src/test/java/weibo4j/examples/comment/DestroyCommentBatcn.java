package weibo4j.examples.comment;

import com.sina.weibo.api.Comments;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class DestroyCommentBatcn {

	public static void main(String[] args) {
		String access_token = args[0];
		String cids = args[1];
		Comments cm = new Comments();
		cm.client.setToken(access_token);
		try {
			JSONArray com = cm.destoryCommentBatch(cids);
			Log.logInfo(com.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
