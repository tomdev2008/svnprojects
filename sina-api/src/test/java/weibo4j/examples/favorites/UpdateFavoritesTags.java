package weibo4j.examples.favorites;

import com.sina.weibo.api.Favorite;
import com.sina.weibo.model.Favorites;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class UpdateFavoritesTags {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = args[0];
		Favorite fm = new Favorite();
		fm.client.setToken(access_token);
		try {
			String tags = args[1];
			String id = args[2];
			Favorites favors = fm.updateFavoritesTags(id, tags);
			Log.logInfo(favors.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
