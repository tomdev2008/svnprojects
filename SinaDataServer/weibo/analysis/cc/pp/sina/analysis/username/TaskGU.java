package cc.pp.sina.analysis.username;

import java.util.TimerTask;

public class TaskGU extends TimerTask {

	@Override
	public void run() {
		try {
			GetUsername gu = new GetUsername("192.168.1.191");
			gu.analysis();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
