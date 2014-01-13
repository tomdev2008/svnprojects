package weibo4j.examples.trends;

import java.util.List;

import com.sina.weibo.api.Trend;
import com.sina.weibo.model.UserTrend;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetTrends {

	public static void main(String[] args) {
		//		String access_token = args[0];
		//		String uid = args[1];
		String access_token = new String("2.00dzHBCC05umf81823c6cbd7KZPJXE");
		String uid = new String("1862087393");

		Trend tm = new Trend();
		tm.client.setToken(access_token);
		List<UserTrend> trends = null;
		try {
			trends = tm.getTrends(uid);
			for (UserTrend t : trends) {
				Log.logInfo(t.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
