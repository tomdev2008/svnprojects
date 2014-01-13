package cc.pp.sina.require.custom;

import java.sql.SQLException;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class WeiboInfo {

	private final String[] VISIBLE = { "普通微博", "私密微博", "指定分组微博", "密友微博" };

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		WeiboInfo weiboInfo = new WeiboInfo();
		String accesstoken = "2.00eEQZzBdcZIJC9afb135d07Rbw7NE";
		String uid = "2342318502"; // 2144684673、2342318502
		int index = weiboInfo.lastWeiboInfos(uid, accesstoken, 86400 * 30);
		System.out.println(index);
	}

	/**
	 * 用户最近某段时间的微博数据
	 * @param uid
	 * @param accesstoken
	 * @param timestamp：时段，秒
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public int lastWeiboInfos(String uid, String accesstoken, long timestamp) throws WeiboException, SQLException {

		/****************采集用户微博信息****************/
		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			int page = 1;
			boolean isoriginal;
			String weibotype;
			long time = System.currentTimeMillis() / 1000;
			StatusWapper status = null;
			try {
				status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
			} catch (RuntimeException e) {
				return -1;
			}
			long weibosum = status.getTotalNumber();
			if (weibosum == 0) {
				return -1;
			}
			while (page * 100 < weibosum + 100) {

				System.out.println(page);

				for (Status s : status.getStatuses()) {
					// 插入微博数据
					if (s.getCreatedAt().getTime() / 1000 < time - timestamp) {
						page = 20;
						break;
					}
					if (s.getRetweetedStatus() != null) {
						isoriginal = false;
					} else {
						isoriginal = true;
					}
					weibotype = VISIBLE[s.getVisible().getType()];
					myjdbc.insertUserWbInfo("userweiboinfo", s.getId(), s.getUser().getId(), s.getUser()
							.getScreenName(), s.getCreatedAt().getTime() / 1000, s.getText(), s.getSource().getName(),
							s.getRepostsCount(), s.getCommentsCount(), s.isFavorited(), s.isTruncated(), s
									.getOriginalPic(), weibotype, isoriginal);
				}
				if (page > 19) { // 限制20次
					break;
				}
				page++;
				try {
					status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
				} catch (RuntimeException e) {
					break; // 出现错误时终止
				}
			}
			myjdbc.sqlClose();
			return 0;
		} else {
			return -1;
		}

	}

	/**
	 * 最近一段时间内的所有微博内容
	 * @param uid
	 * @param accesstoken
	 * @param timestamp
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public String lastWeiboTexts(String uid, String accesstoken, long timestamp) throws WeiboException, SQLException {

		/****************采集用户微博信息****************/
		Timeline tm = new Timeline();
		tm.client.setToken(accesstoken);
		int page = 1;
		String texts = "";
		long time = System.currentTimeMillis() / 1000;
		StatusWapper status = null;
		try {
			status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
		} catch (RuntimeException e) {
			return null;
		}
		long weibosum = status.getTotalNumber();
		if (weibosum == 0) {
			return null;
		}
		while (page * 100 < weibosum + 100) {

			//			System.out.println(page);

			for (Status s : status.getStatuses()) {
				if (s.getCreatedAt().getTime() / 1000 < time - timestamp) {
					page = 20;
					break;
				}
				if (s.getRetweetedStatus() == null) { // 如果是原创，则提取微博内容
					texts = texts + s.getText();
				}
			}
			if (page > 19) { // 限制20次
				break;
			}
			page++;
			try {
				status = tm.getUserTimelineByUid(uid, new Paging(page, 100), 0, 0);
			} catch (RuntimeException e) {
				break; // 出现错误时终止
			}
		}

		return texts;
	}

}
