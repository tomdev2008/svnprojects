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

public class GetUserWeibos1 {

	private static Logger logger = LoggerFactory.getLogger(GetUserWeibos.class);

	private final String[] VISIBLE = { "普通微博", "私密微博", "指定分组微博", "密友微博" };

	/**
	 * 主函数
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, WeiboException {

		GetUserWeibos1 getUserWeibos = new GetUserWeibos1();
		getUserWeibos.dumpUserWeibos();

	}

	public void dumpUserWeibos() throws SQLException, WeiboException {

		/****************线上环境******************/
		UserWeiboJDBC myJdbc = new UserWeiboJDBC(Nettool.getServerLocalIp());
		if (myJdbc.mysqlStatus()) {
			//			myJdbc.createUserWeiboTable("sinauserweiboinfo");
			int count = 0, num = 50;
			while (count++ < 200) {
				System.out.println(count);
				List<String> sinausers = myJdbc.getSinaUsers("sinausers", num);
				String[] accesstoken = myJdbc.getAccessToken(num);

				for (int i = 0; i < sinausers.size(); i++) {
					this.dumpWeibosToDB(myJdbc, sinausers.get(i), accesstoken[i]);
				}

				myJdbc.deleteBatch("sinausers", sinausers);
			}
			myJdbc.sqlClose();
		}
	}

	public void disSinaUsers() throws SQLException {

		UserWeiboJDBC myJdbc = new UserWeiboJDBC(Nettool.getServerLocalIp());
		if (myJdbc.mysqlStatus()) {
			List<String> sinausers = myJdbc.getSinaUsers("sinausers", 100000);
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
			myJdbc.sqlClose();
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
	public int dumpWeibosToDB(UserWeiboJDBC myJdbc, String uid, String accesstoken) throws WeiboException, SQLException {

		Timeline tm = new Timeline();
		tm.client.setToken(accesstoken);
		StatusWapper status = null;
		int page = 1;
		boolean isoriginal;
		String weibotype;
		try {
			status = tm.getUserTimelineByUid(uid, new Paging(page++, 100), 0, 0);
		} catch (RuntimeException e) {
			return -1;
		}
		long pagecount = (status.getTotalNumber() >= 2000) ? 20 : status.getTotalNumber() / 100 + 1;

		while (page++ <= pagecount) {
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
							.getScreenName(), s.getCreatedAt().getTime() / 1000, s.getText(), s.getSource().getName(),
							s.getRepostsCount(), s.getCommentsCount(), s.isFavorited(), s.isTruncated(), s
									.getOriginalPic(), weibotype, isoriginal);
				} catch (SQLException e) {
					logger.info(s.getText());
					continue;
					//					System.out.println(s.getText());
				}
			}
			try {
				status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
			} catch (RuntimeException e) {
				break; // 采集微博中断，不继续采集，后面可以再做修正，如果中断可急需采集
				//				throw new RuntimeException("uid: " + uid + ", staus: " + status, e);
			}
		}

		return 0;
	}

}

