package cc.pp.sina.analysis.user.baseinfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import cc.pp.sina.jdbc.UidJDBC;
import cc.pp.sina.utils.Nettool;

public class UidToDb1 {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {

		UidToDb1 uidToDb = new UidToDb1();
		uidToDb.create32Tables();

	}

	public void create32Tables() throws SQLException {

		UidJDBC uidJDBC = new UidJDBC(Nettool.getServerLocalIp());
		if (uidJDBC.mysqlStatus()) {
			for (int i = 1; i < 32; i++) {
				//				uidJDBC.createUserBaseinfoTable("sinaunuseduids_" + i);
				uidJDBC.createTable("sinaunuseduids_" + i);
				//				uidJDBC.insertTablename("tablename_sinaunuseduids", "sinaunuseduids_" + i);
				//				uidJDBC.insertTablename("tablename_sinausers", "sinausers_" + i);
				//				uidJDBC.insertTablename("tablename_sinauserbaseinfo", "sinauserbaseinfo_" + i);
			}
			uidJDBC.sqlClose();
		}
	}

	public void fromTxtToTxtx() throws IOException {

		int index = 0;
		String uid = "";
		BufferedReader br = new BufferedReader(new FileReader(new File("sina_uid")));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("sina_uid_0.txt")));
		while ((uid = br.readLine()) != null) {
			index++;
			if (index % 20000000 == 0) {
				bw.close();
				bw = new BufferedWriter(new FileWriter(new File("sina_uid_" + index / 20000000 + ".txt")));
			}
			bw.append(uid);
			bw.newLine();
		}
		bw.close();
		br.close();
	}

	@SuppressWarnings("resource")
	public void fromTxtToDb() throws IOException, SQLException {

		UidJDBC uidJDBC = new UidJDBC(Nettool.getServerLocalIp());
		if (uidJDBC.mysqlStatus()) {
			BufferedReader br = new BufferedReader(new FileReader(new File("sina_uid")));
			String uid = "";
			while ((uid = br.readLine()) != null) {
				try {
					uidJDBC.inserUids("sinausers", uid);
				} catch (Exception e) {
					throw new RuntimeException("uid: " + uid, e);
				}

			}
			br.close();
			uidJDBC.sqlClose();
		}
	}

}
