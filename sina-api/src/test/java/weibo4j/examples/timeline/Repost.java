package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class Repost {

	public static void main(String[] args) {
		//		String access_token = args[0];
		//		String id =  args[1];
		String access_token = new String("2.00dzHBCC05umf81823c6cbd7KZPJXE");
		String id = new String("3518124169185543");

		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		try {
			Status status = tm.Repost(id);
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
