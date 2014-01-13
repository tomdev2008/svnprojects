package cc.pp.tencent.analysis.username.threads;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.tencent.jdbc.UsernameJDBC;

import com.tencent.weibo.dao.impl.OtherInfo;
import com.tencent.weibo.dao.impl.OtherInfoData;

public class FansAndFriends implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(FansAndFriends.class);

	private final String uid;
	private final UsernameDao usernameDao;
	private final UsernameJDBC myJdbc;

	private static AtomicInteger count = new AtomicInteger(0);

	public FansAndFriends(String uid, UsernameDao usernameDao, UsernameJDBC myJdbc) {
		super();
		this.uid = uid;
		this.usernameDao = usernameDao;
		this.myJdbc = myJdbc;
	}

	public void run() {
		try {
			System.out.println(count.addAndGet(1));
			OtherInfo userinfo = usernameDao.getUserBaseInfo(uid);
			if (userinfo != null) {
				/******************插入用户基础信息********************/
				OtherInfoData data = userinfo.getData();
				myJdbc.inserUserBaseInfo("tencentuserbaseinfo", data.getName(), data.getNick(), //
						data.getOpenid(), data.getFansnum(), data.getFavnum(), data.getIdolnum(), //
						data.getTweetnum(), data.getBirth_day(), data.getBirth_month(), data.getBirth_year(), //
						data.getCity_code(), data.getComp(), data.getCountry_code(), data.getEdu(), //
						data.getEmail(), data.getExp(), data.getHead(), data.getHomecity_code(), //
						data.getHomecountry_code(), data.getHomepage(), data.getHomeprovince_code(), //
						data.getHometown_code(), data.getHttps_head(), data.getIndustry_code(), //
						data.getIntroduction(), data.getIsent(), data.getIsrealname(), data.getIsvip(), //
						data.getLevel(), data.getLocation(), data.getMutual_fans_num(), //
						data.getProvince_code(), data.getRegtime(), data.getSend_private_flag(), //
						data.getSex(), data.getTag(), data.getVerifyinfo());
				/******************插入用户关系信息********************/
				List<String> fansuids = usernameDao.getUserFansInfo(uid, userinfo.getData().getFansnum());
				List<String> friends = usernameDao.getUserFriendsInfo(uid, userinfo.getData().getIdolnum());
				if (fansuids != null && friends != null) {
					fansuids.addAll(friends);
					myJdbc.insertUidsBatch(getTablename(myJdbc), fansuids);
					myJdbc.insertFriendUids("tencentfriends", uid, friends);
				} else {
					myJdbc.insertUnusedUsers("tencentunuseduids", uid);
				}
			} else {
				myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			}
		} catch (Exception e) {
			logger.info("Error uid: " + uid);
			e.printStackTrace();
		}
	}

	/**
	 * 获取表名
	 * @throws SQLException 
	 */
	public String getTablename(UsernameJDBC myJdbc) throws SQLException {

		String tablename = new String();
		String lasttablename = myJdbc.getLastTablename("tablenames");
		if (lasttablename == null) {
			tablename = "usernames" + this.getTodayDate();
			myJdbc.createUidsTable(tablename);
			myJdbc.insertTablename(tablename);
		} else {
			int max = myJdbc.getMaxId(lasttablename);
			if (max > 10000000) {
				tablename = "usernames" + this.getTodayDate();
				myJdbc.createUidsTable(tablename);
				myJdbc.insertTablename(tablename);
			} else {
				tablename = lasttablename;
			}
		}

		return tablename;
	}

	/**
	 * 获取今天日期
	 * @return
	 */
	public String getTodayDate() {

		long time = System.currentTimeMillis() / 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time * 1000l));
		date = date.replaceAll("-", "");

		return date;
	}

}
