package cc.pp.sina.analysis.update.tokens;

import java.util.TimerTask;

import cc.pp.sina.jdbc.WeiboJDBC;

public class TaskUT extends TimerTask {

	@Override
	public void run() {
		try {
			WeiboJDBC weibojdbc = new WeiboJDBC("192.168.1.27");
			if (weibojdbc.mysqlStatus()) {
				weibojdbc.deleteToken();
				weibojdbc.sqlClose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
