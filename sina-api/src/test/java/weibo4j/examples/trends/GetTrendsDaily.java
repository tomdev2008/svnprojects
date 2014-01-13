package weibo4j.examples.trends;

import java.util.List;

import com.sina.weibo.api.Trend;
import com.sina.weibo.model.Trends;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetTrendsDaily {
	public static void main(String[] args) {
		//		String access_token = args[0];
		String access_token = new String("2.00dzHBCC05umf81823c6cbd7KZPJXE");

		Trend tm = new Trend();
		tm.client.setToken(access_token);
		List<Trends> trends = null;
		try {
			trends = tm.getTrendsDaily();
			for (Trends ts : trends) {
				Log.logInfo(ts.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
