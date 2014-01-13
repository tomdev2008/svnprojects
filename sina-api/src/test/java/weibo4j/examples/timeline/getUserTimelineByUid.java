package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class getUserTimelineByUid {

	public static void main(String[] args) {
		//		String access_token = args[0];
		//		String uid = args[1];
		String access_token = new String("2.002wI6kBdcZIJC934f3c3c31swtDUD");
		String uid = new String("2708276981");

		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		int page = 0;
		boolean flag = true;
		while (flag) {
			page++;
			try {
				StatusWapper status = tm.getUserTimelineByUid(uid, new Paging(page, 200), 0, 0);

				for (Status s : status.getStatuses()) {
					Log.logInfo(s.toString());
				}
			} catch (WeiboException e) {
				if (e.getStatusCode() == 200) {
					flag = false;
					break;
				} else {
					e.printStackTrace();
				}
			}
		}
	}

}
