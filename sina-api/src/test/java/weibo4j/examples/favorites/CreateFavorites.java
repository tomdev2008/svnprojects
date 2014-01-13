package weibo4j.examples.favorites;

import com.sina.weibo.api.Favorite;
import com.sina.weibo.model.Favorites;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class CreateFavorites {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = args[0];
		String id = args[1];
		Favorite fm = new Favorite();
		fm.client.setToken(access_token);
		try {
			Favorites favors = fm.createFavorites(id);
			Log.logInfo(favors.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
