package cc.pp.tencent.analysis.username.threads;

import java.util.List;

import com.tencent.weibo.dao.impl.OtherInfo;

public interface UsernameDao {

	/**
	 * 获取用户基础信息
	 */
	OtherInfo getUserBaseInfo(String uid);

	/**
	 * 获取用户的粉丝信息
	 *    ***最多拉取10000个粉丝信息***
	 */
	List<String> getUserFansInfo(String uid, int fansnum);

	/**
	 * 获取用户的关注信息
	 *    ***最多拉取10000个关注信息***
	 */
	List<String> getUserFriendsInfo(String uid, int idolnum);

	public int getApicount();

}
