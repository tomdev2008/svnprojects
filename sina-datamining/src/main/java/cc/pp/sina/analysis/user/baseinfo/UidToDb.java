package cc.pp.sina.analysis.user.baseinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import cc.pp.sina.jdbc.UidJDBC;
import cc.pp.sina.utils.Nettool;

public class UidToDb implements Runnable {

	private final String filename;

	private static AtomicInteger count = new AtomicInteger(0);

	public UidToDb(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {
		try {
			System.out.println(count.addAndGet(1));
			UidJDBC uidJDBC = new UidJDBC(Nettool.getServerLocalIp());
			if (uidJDBC.mysqlStatus()) {
				BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
				String uid = "";
				while ((uid = br.readLine()) != null) {
					if (uid.length() > 10) {
						uidJDBC.inserUids("sinausers_long", uid);
					} else {
						uidJDBC.inserUids("sinausers_" + Long.parseLong(uid) % 32, uid);
					}
				}
				br.close();
				uidJDBC.sqlClose();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
