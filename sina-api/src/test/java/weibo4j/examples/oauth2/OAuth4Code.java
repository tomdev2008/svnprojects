package weibo4j.examples.oauth2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sina.weibo.api.Oauth;
import com.sina.weibo.model.WeiboException;
import com.sina.weibo.util.BareBonesBrowserLaunch;

public class OAuth4Code {
	public static void main(String[] args) throws WeiboException, IOException {
		Oauth oauth = new Oauth();
		//		BareBonesBrowserLaunch.openURL(oauth.authorize("code",args[0],args[1]));
		BareBonesBrowserLaunch.openURL(oauth.authorize("code", null));
		//		System.out.println(oauth.authorize("code",args[0],args[1]));
		System.out.println(oauth.authorize("code", null));
		System.out.print("Hit enter when it's done.[Enter]:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();
		Log.logInfo("code: " + code);
		try {
			System.out.println(oauth.getAccessTokenByCode(code));
		} catch (WeiboException e) {
			if (401 == e.getStatusCode()) {
				Log.logInfo("Unable to get the access token.");
			} else {
				e.printStackTrace();
			}
		}
	}
}

//AccessToken [accessToken=2.00dzHBCC05umf81823c6cbd7KZPJXE, expireIn=157679999, refreshToken=,uid=1862087393]

