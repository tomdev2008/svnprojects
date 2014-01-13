package cc.pp.sina.analysis.userclassfy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.TimerTask;

import cc.pp.sina.jdbc.UserClassfyJDBC;

import com.sina.weibo.model.WeiboException;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskUC extends TimerTask {

	@Override
	public void run() {
		try {
			UsersLibrary ui = new UsersLibrary();
			UserClassfyJDBC ucjdbc = new UserClassfyJDBC("192.168.1.44");
			if (ucjdbc.mysqlStatus()) {
				int i = 0;
				while (i++ < 100) {
					HashMap<Integer, String> username = ucjdbc.getUsernames("sinausers", 1);
					ucjdbc.deleteUsernames("sinausers", username.get(0));
					try {
						ui.userAllParams(ucjdbc, "sinalibrary", username.get(0));
					} catch (SQLException e) {
						continue;
					}
				}
				ucjdbc.sqlClose();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
