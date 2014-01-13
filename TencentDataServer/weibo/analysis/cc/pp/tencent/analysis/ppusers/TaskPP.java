package cc.pp.tencent.analysis.ppusers;

import java.sql.SQLException;
import java.util.TimerTask;

import javax.management.RuntimeErrorException;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskPP extends TimerTask {

	@Override
	public void run() {
		try {
			BaseInfoMain baseinfo = new BaseInfoMain("tencentuserinfo", "pptencentusers", "tencentunusedusers",
					"192.168.1.189");
			baseinfo.importDataToDB();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
