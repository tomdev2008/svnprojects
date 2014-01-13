package cc.pp.sina.analysis.ppusers;

import java.sql.SQLException;
import java.util.TimerTask;

import javax.management.RuntimeErrorException;

import com.sina.weibo.model.WeiboException;


/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskPP extends TimerTask {

	@Override
	public void run() {
		// 
		try {
			BaseInfoMain baseinfo = new BaseInfoMain("sinauserinfo", "ppsinausers", "sinaunusedusers", "192.168.1.188");
			baseinfo.importDataToDB();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (RuntimeErrorException e) {
			e.printStackTrace();
		}
	}
}
