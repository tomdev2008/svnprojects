package cc.pp.sina.analysis.username;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.pp.sina.jdbc.UidJDBC;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.model.WeiboException;

public class GetUsername implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	public int api = 0;
	private String ip = "";

	public GetUsername(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, WeiboException {

		GetUsername gu = new GetUsername("192.168.1.191");
		gu.analysis();

	}

	public void analysis() throws SQLException, WeiboException {

		try {
			UidJDBC uidjdbc = new UidJDBC(this.ip);
			if (uidjdbc.mysqlStatus()) {
				String tablename = this.getTablename();
				String lasttablename = uidjdbc.getLastTablename();
				if (lasttablename == null) {
					uidjdbc.createTable(tablename);
					uidjdbc.insertTablename(tablename);
				} else if (!lasttablename.equals(tablename)) {
					uidjdbc.createTable(tablename);
					uidjdbc.insertTablename(tablename);
				}
				String uid = uidjdbc.getSinaUid("sinausers");
				uidjdbc.deleteUid("sinausers", uid);
				this.getFansUids(uidjdbc, tablename, uid);
				this.getFriendsUids(uidjdbc, tablename, uid);
				
				uidjdbc.sqlClose();
			}
		} catch (RuntimeException e) {
			//
		}
	}

	/**
	 * 采集用户的粉丝uid
	 * @param tablename
	 * @param uid
	 * @throws SQLException
	 * @throws WeiboException 
	 */
	public void getFansUids(UidJDBC uidjdbc, String tablename, String uid) throws SQLException, WeiboException {

		/********获取accesstoken和用户username*********/
		String accesstoken = uidjdbc.getAccessToken();
		Friendships fm = new Friendships();
		fm.client.setToken(accesstoken);
		String[] ids = fm.getFollowersIdsById(uid, 5000, 0);
		List<String> uids = new ArrayList<String>();
		for (int i = 0; i < ids.length; i++) {
			uids.add(ids[i]);
		}
		// 批量插入数据
		uidjdbc.insertUidsBatch(tablename, uids);
	}

	/**
	 * 采集用户的关注uid
	 * @param tablename
	 * @param uid
	 * @throws SQLException
	 * @throws WeiboException 
	 */
	public void getFriendsUids(UidJDBC uidjdbc, String tablename, String uid) throws SQLException, WeiboException {

		/********获取accesstoken和用户username*********/
		String accesstoken = uidjdbc.getAccessToken();
		Friendships fm = new Friendships();
		fm.client.setToken(accesstoken);
		String[] ids = fm.getFriendsIdsByUid(uid, 5000, 0);
		List<String> uids = new ArrayList<String>();
		for (int i = 0; i < ids.length; i++) {
			uids.add(ids[i]);
		}
		// 批量插入数据
		uidjdbc.insertUidsBatch(tablename, uids);
	}

	/**
	 * 获取表格名
	 * @return
	 */
	public String getTablename() {

		long time = System.currentTimeMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		String date = sdf.format(new Date(time*1000l));
		int t = Integer.parseInt(date)/12;
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		String tablename = sdf.format(new Date(time*1000l));
		tablename = "sinausers" + tablename.replaceAll("-", "") + t;

		return tablename;
	}

}
