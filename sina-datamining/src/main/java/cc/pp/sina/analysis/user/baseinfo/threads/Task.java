package cc.pp.sina.analysis.user.baseinfo.threads;

import java.sql.SQLException;
import java.util.TimerTask;

public class Task extends TimerTask {

	@Override
	public void run() {
		try {
			MultiThreadsUserBaseinfo multiThreadsUserBaseinfo = new MultiThreadsUserBaseinfo();
			multiThreadsUserBaseinfo.multiThreads();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
