package cc.pp.sina.analysis.userfans.interaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimerTask;

import org.apache.commons.httpclient.HttpException;

import com.sina.weibo.model.WeiboException;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskUFI extends TimerTask {

	@Override
	public void run() {
		try {
			InteractionsAnalysis ia = new InteractionsAnalysis("192.168.1.27");
			ia.analysis();
			ia.moveResult();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
