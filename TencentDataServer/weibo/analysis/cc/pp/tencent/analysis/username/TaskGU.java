package cc.pp.tencent.analysis.username;

import java.util.HashMap;
import java.util.TimerTask;

import cc.pp.tencent.jdbc.UidJDBC;

public class TaskGU extends TimerTask {

	@Override
	public void run() {
		try {
			String ip = "192.168.1.171";
			GetUsername getusername = new GetUsername(ip);
			UidJDBC uidjdbc = new UidJDBC(ip);
			if (uidjdbc.mysqlStatus()) {
				String tablename = getusername.getTablename();
				HashMap<Integer,String> uids = uidjdbc.getAllUids("tencentusername", 100);
				for (int i = 0; i < uids.size(); i++) {
					getusername.getFansUids(tablename, uids.get(i));
					uidjdbc.deleteUids("tencentusername", uids.get(i));
				}
				uidjdbc.sqlClose();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
