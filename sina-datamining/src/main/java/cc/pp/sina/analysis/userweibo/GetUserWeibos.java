package cc.pp.sina.analysis.userweibo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.sina.jdbc.UserWeiboJDBC;
import cc.pp.sina.utils.Nettool;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class GetUserWeibos implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(GetUserWeibos.class);

	private final String[] VISIBLE = { "普通微博", "私密微博", "指定分组微博", "密友微博" };
	String accesstoken = null;
	String sinauser = null;

	public GetUserWeibos(String accesstoken, String sinauser) {
		this.accesstoken = accesstoken;
		this.sinauser = sinauser;
	}

	@Override
	public void run() {
		try {
			//			UserWeiboJDBC myJdbc = new UserWeiboJDBC(Nettool.getServerLocalIp());
			UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");
			if (myJdbc.mysqlStatus()) {
				this.dumpWeibosToDB(myJdbc);
				myJdbc.sqlClose();
			}
		} catch (SQLException e) {
			throw new RuntimeException("uid: " + sinauser, e);
		}
	}

	public void disSinaUsers() throws SQLException {

		UserWeiboJDBC myJDBC = new UserWeiboJDBC(Nettool.getServerLocalIp());
		if (myJDBC.mysqlStatus()) {
			List<String> sinausers = myJDBC.getSinaUsers("sinausers", 100000);
			int[] ips = { 51, 36, 37, 38, 39, 40, 41, 42, 43, 44 };
			for (int i = 0; i < ips.length; i++) {

				System.out.println("ip: " + i);

				UserWeiboJDBC jdbc = new UserWeiboJDBC("192.168.1." + ips[i]);
				if (jdbc.mysqlStatus()) {
					jdbc.deleteTable("sinausers");
					jdbc.createSinaUsersTable("sinausers");
					List<String> uids = new ArrayList<String>();
					if (i == ips.length - 1) {
						for (int j = i * 9500; j < sinausers.size(); j++) {
							uids.add(sinausers.get(j));
						}
					} else {
						for (int j = i * 9500; j < (i + 1) * 9500; j++) {
							uids.add(sinausers.get(j));
						}
					}
					jdbc.insertSinausers("sinausers", uids);
					jdbc.sqlClose();
				}
			}
			myJDBC.sqlClose();
		}
	}

	/**
	 * 采集用户的历史微博信息
	 * @param uid
	 * @param accesstoken
	 * @return
	 * @throws WeiboException
	 * @throws SQLException 
	 */
	public int dumpWeibosToDB(UserWeiboJDBC myJdbc) throws SQLException {

		try {
			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			StatusWapper status = null;
			int page = 1;
			boolean isoriginal;
			String weibotype;
			try {
				status = tm.getUserTimelineByUid(sinauser, new Paging(page++, 100), 0, 0);
			} catch (RuntimeException e) {
				myJdbc.insertUnusedUsers("sinaunuseduids", sinauser);
				return -1;
			}
			long pagecount = (status.getTotalNumber() >= 2000) ? 20 : status.getTotalNumber() / 100 + 1;

			while (page <= pagecount) {
				for (Status s : status.getStatuses()) {
					/******************把微博信息写入数据库********************/
					s.getText();
					if (s.getRetweetedStatus() != null) {
						isoriginal = false;
					} else {
						isoriginal = true;
					}
					weibotype = VISIBLE[s.getVisible().getType()];
					try {
						myJdbc.insertUserWbInfo("sinauserweiboinfo", s.getId(), s.getUser().getId(), s.getUser()
								.getScreenName(), s.getCreatedAt().getTime() / 1000, s.getText(), s.getSource()
								.getName(), s.getRepostsCount(), s.getCommentsCount(), s.isFavorited(),
								s.isTruncated(), s.getOriginalPic(), weibotype, isoriginal);
					} catch (SQLException e) {
						logger.info(s.getText());
					}
				}
				try {
					status = tm.getUserTimelineByUid(sinauser, new Paging(page++, 100), 0, 0);
				} catch (RuntimeException e) {
					myJdbc.insertUnusedUsers("sinaunuseduids", sinauser);
				}
			}
		} catch (WeiboException e) {
			myJdbc.insertUnusedUsers("sinaunuseduids", sinauser);
		}

		return 0;
	}

}
