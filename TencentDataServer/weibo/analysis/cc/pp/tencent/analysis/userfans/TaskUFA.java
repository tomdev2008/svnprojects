package cc.pp.tencent.analysis.userfans;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TimerTask;

import cc.pp.tencent.jdbc.UserJDBC;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskUFA extends TimerTask {

	@Override
	public void run() {
		/*********************获取新增授权用户***********************/
		try {
			String ip = "192.168.1.27";
			UserJDBC mysql = new UserJDBC(ip);
			if (mysql.mysqlStatus()) {
				FansAnalysis ua = new FansAnalysis(ip);
				HashMap<String, String> uids = mysql.getUnAnalyzedUsers();
				if (uids != null) {
					for (Entry<String, String> temp : uids.entrySet()) {
						int index = ua.analysis(temp.getKey());
						if (index > 0) {
							mysql.updateTencentUser(temp.getKey());
						}
					}
				}
				mysql.sqlClose();
			}
			/**
			 * 更新数据，主要是粉丝量、活跃粉丝量、粉丝增长曲线
			 */
			UpdateFansResult ufr = new UpdateFansResult("192.168.1.154", ip);
			ufr.updateResult();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
