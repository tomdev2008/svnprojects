package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetHomeTimeline {

	public static void main(String[] args) {
		String access_token = args[0];
		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		try {
			StatusWapper status = tm.getHomeTimeline();
			for (Status s : status.getStatuses()) {
				Log.logInfo(s.toString());
			}
			System.out.println(status.getNextCursor());
			System.out.println(status.getPreviousCursor());
			System.out.println(status.getTotalNumber());
			System.out.println(status.getHasvisible());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}