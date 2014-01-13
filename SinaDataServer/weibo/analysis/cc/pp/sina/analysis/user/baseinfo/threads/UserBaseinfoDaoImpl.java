package cc.pp.sina.analysis.user.baseinfo.threads;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;


public class UserBaseinfoDaoImpl implements UserBaseinfoDao {

	private static final Logger logger = LoggerFactory.getLogger(UserBaseinfoDaoImpl.class);

	private final List<String> tokens;
	private final Random random = new Random();

	Users user = new Users();

	public UserBaseinfoDaoImpl(List<String> tokens) {
		this.tokens = tokens;
	}

	@Override
	public User getUserBaseinfo(String uid) {

		String token = getRandomToken();
		user.client.setToken(token);
		User userinfo = null;
		try {
			userinfo = user.showUserById(uid);
		} catch (WeiboException e) {
			if (e.getErrorCode() == 10022) {
				try {
					Thread.sleep(1000 * 60);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			logger.info("Error uid is: " + uid);
			//			throw new RuntimeException(String.format("WeiboException: %d\t%s\t%s", e.getErrorCode(), uid, token), e);
		}

		return userinfo;
	}

	/**
	 * 随机获取token
	 * @return
	 */
	public String getRandomToken() {
		return tokens.get(random.nextInt(tokens.size()));
	}

}
