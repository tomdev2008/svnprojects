package cc.pp.sina.analysis.userfans.interaction;

import java.util.TimerTask;

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
			ia.closeMysql();
		} catch (final Exception e) {
			//			throw new RuntimeException("Error", e);
		}
	}
}
