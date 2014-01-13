package weibo4j.examples.favorites;

import java.util.List;

import com.sina.weibo.api.Favorite;
import com.sina.weibo.model.FavoritesTag;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetFavoritesTags {

	public static void main(String[] args) {
		String access_token = args[0];
		Favorite fm = new Favorite();
		fm.client.setToken(access_token);
		try {
			List<FavoritesTag> favors = fm.getFavoritesTags();
			for (FavoritesTag s : favors) {
				Log.logInfo(s.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}