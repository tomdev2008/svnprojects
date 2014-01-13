package weibo4j.examples.trends;

import com.sina.weibo.api.Trend;
import com.sina.weibo.model.UserTrend;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class TrendsFollow {

	public static void main(String[] args) {
		String access_token = args[0];
		Trend tm = new Trend();
		tm.client.setToken(access_token);
		String trend_name = args[1];
		try {
			UserTrend ut = tm.trendsFollow(trend_name);
			Log.logInfo(ut.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
