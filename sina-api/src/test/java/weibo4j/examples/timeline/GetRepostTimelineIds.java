package weibo4j.examples.timeline;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class GetRepostTimelineIds {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String access_token = "2.00VMADJBdcZIJCb6635033cf01NOFd";
		//		String url = new String("http://www.weibo.com/1784501333/zsq6ThV11");
		//		String wid = WeiboAnalysis.mid2id(url);
		String wid = "";
		Timeline tm = new Timeline();
		tm.client.setToken(access_token);
		int cursor = 1;
		Paging page = new Paging(cursor, 200);

		try {
			StatusWapper status = tm.getRepostTimeline(wid, page);
			int i = 0;

			while ((cursor * 200 < status.getTotalNumber() + 200) && (cursor < 150)) {
				for (Status reposter : status.getStatuses()) {
					i++;
					//					System.out.println(reposter);
				}
				cursor++;
				page = new Paging(cursor, 200);
				status = tm.getRepostTimeline(wid, page);
			}
			System.out.println(i);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
