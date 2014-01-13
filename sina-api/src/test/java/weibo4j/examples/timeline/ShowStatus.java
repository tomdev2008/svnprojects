package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.WeiboException;

public class ShowStatus {

	public static void main(String[] args) {
		//		String access_token = args[0];
		//		String id = args[1];
		String access_token = new String("2.00IwNlmBdcZIJC5ba9c4cd68gJo6nC");
		//		String id = new String("3518124169185543");
		String ids = "3568171405147338,3567742596239630,3568173045608571";
		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		try {
			Status status = tm.showBatch(ids, 0);

			System.out.println(status);

		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
