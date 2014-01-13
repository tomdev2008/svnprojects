package com.sina.weibo.api;

import com.sina.weibo.http.Response;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.model.PostParameter;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;
import com.sina.weibo.util.WeiboConfig;

public class Users extends Weibo {

	private static final long serialVersionUID = 4742830953302255953L;

	/*----------------------------用户接口----------------------------------------*/
	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param uid
	 *            需要查询的用户ID
	 * @return User
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a href="http://open.weibo.com/wiki/2/users/show">users/show</a>
	 * @since JDK 1.5
	 */
	public User showUserById(String uid) throws WeiboException {
		Response response = client.get(WeiboConfig.getValue("baseURL") + "users/show.json",
				new PostParameter[] { new PostParameter("uid", uid) });
		//		System.out.println(response.asJSONObject());
		//		if (response.toString().indexOf("20003") != -1) {
		//			return new User(null);
		//		} else {
		//			return new User(response.asJSONObject());
		//		}
		return new User(response.asJSONObject());
	}

	/**
	 * 根据用户ID获取用户信息
	 * 
	 * @param screen_name
	 *            用户昵称
	 * @return User
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a href="http://open.weibo.com/wiki/2/users/show">users/show</a>
	 * @since JDK 1.5
	 */
	public User showUserByScreenName(String screen_name) throws WeiboException {
		return new User(client.get(WeiboConfig.getValue("baseURL") + "users/show.json",
				new PostParameter[] { new PostParameter("screen_name", screen_name) }).asJSONObject());
	}

	/**
	 * 通过个性化域名获取用户资料以及用户最新的一条微博
	 * 
	 * @param domain
	 *            需要查询的个性化域名。
	 * @return User
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/users/domain_show">users/domain_show</a>
	 * @since JDK 1.5
	 */
	public User showUserByDomain(String domain) throws WeiboException {
		return new User(client.get(WeiboConfig.getValue("baseURL") + "users/domain_show.json",
				new PostParameter[] { new PostParameter("domain", domain) }).asJSONObject());
	}

	/**
	 * 批量获取用户的粉丝数、关注数、微博数
	 * 
	 * @param uids
	 *            需要获取数据的用户UID，多个之间用逗号分隔，最多不超过100个
	 * @return jsonobject
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @version weibo4j-V2 1.0.1
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/users/domain_show">users/domain_show</a>
	 * @since JDK 1.5
	 */
	//	public JSONArray getUserCount(String uids) throws WeiboException{
	//		return  client.get(WeiboConfig.getValue("baseURL") + "users/counts.json",
	//				new PostParameter[] { new PostParameter("uids", uids)}).asJSONArray();
	//	}
	public JSONArray getUserCount(String uids) throws WeiboException {
		Response response = client.get(WeiboConfig.getValue("baseURL") + "users/counts.json",
				new PostParameter[] { new PostParameter("uids", uids) });
		if (response.toString().contains("expired_token") || response.toString().contains("invalid_access_token")) {
			return null;
		} else {
			return response.asJSONArray();
		}
	}

}
