package weibo4j.examples.publicservice;

import com.sina.weibo.api.PublicService;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.model.WeiboException;

public class ProvinceList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String access_token = args[0];
		String country = args[1];
		PublicService ps = new PublicService();
		ps.client.setToken(access_token);
		try {
			JSONArray jo = ps.provinceList(country);
			System.out.println(jo.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
