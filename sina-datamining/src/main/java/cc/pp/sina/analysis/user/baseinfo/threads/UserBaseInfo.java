package cc.pp.sina.analysis.user.baseinfo.threads;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.sina.analysis.userweibo.MultiThreadsUserWeibos;
import cc.pp.sina.jdbc.UidJDBC;

import com.sina.weibo.model.User;

public class UserBaseInfo implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsUserWeibos.class);

	private final String uid;
	private final UserBaseinfoDao userBaseinfoDao;
	private final UidJDBC myJdbc;
	private final String tablename;
	private final String sinaunusedtable;

	//	private static AtomicInteger count = new AtomicInteger(0);

	public UserBaseInfo(String uid, UserBaseinfoDao userBaseinfoDao, UidJDBC myJdbc, String tablename,
			String sinaunusedtable) {
		this.uid = uid;
		this.userBaseinfoDao = userBaseinfoDao;
		this.myJdbc = myJdbc;
		this.tablename = tablename;
		this.sinaunusedtable = sinaunusedtable;
	}

	@Override
	public void run() {
		try {
			//			System.out.println(count.addAndGet(1));
			try {
				User userinfo = userBaseinfoDao.getUserBaseinfo(uid);
				/**
				 * 请求API达到上限，休息1分钟
				 */
				//								if ("10022".equals(userinfo.getStatusCode())) {
				//									Thread.sleep(60 * 1000);
				//								}
				myJdbc.inserUserBaseinfo(tablename, userinfo.getId(), userinfo.getScreenName(),
						userinfo.getName(), userinfo.getProvince(), userinfo.getCity(), userinfo.getLocation(),
						userinfo.getDescription(), userinfo.getUrl(), userinfo.getProfileImageUrl(),
						userinfo.getUserDomain(), userinfo.getGender(), userinfo.getFollowersCount(),
						userinfo.getFriendsCount(), userinfo.getStatusesCount(), userinfo.getFavouritesCount(),
						userinfo.getCreatedAt().getTime() / 1000, userinfo.isVerified(), userinfo.getVerifiedType(),
						userinfo.getAvatarLarge(), userinfo.getBiFollowersCount(), userinfo.getRemark(),
						userinfo.getVerifiedReason(), userinfo.getWeihao());
			} catch (SQLException e) {
				myJdbc.insertUnusedUsers(sinaunusedtable, uid);
			} catch (RuntimeException e) {
				myJdbc.insertUnusedUsers(sinaunusedtable, uid);
			} catch (Exception e) { // WeiboException
				myJdbc.insertUnusedUsers(sinaunusedtable, uid);
			}
		} catch (Exception e) {
			logger.info("Error uid: " + uid);
			//			e.printStackTrace();
		}
	}

}
