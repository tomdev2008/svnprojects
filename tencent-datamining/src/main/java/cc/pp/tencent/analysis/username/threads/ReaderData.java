package cc.pp.tencent.analysis.username.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cc.pp.tencent.jdbc.UsernameJDBC;
import cc.pp.tencent.utils.Nettool;

public class ReaderData {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {

		ReaderData readerData = new ReaderData();
		readerData.disUidsToDBs();

	}

	public void createTables() throws SQLException {

		int[] ips = { 26, 28, 29, 30, 31, 32, 50, 34, 51, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 47 };
		for (int ip = 0; ip < ips.length; ip++) {
			System.out.println("ip is: 192.168.1." + ips[ip]);
			UsernameJDBC jdbc = new UsernameJDBC("192.168.1." + ips[ip]);
			if (jdbc.mysqlStatus()) {
				// 删除所有表格
				//				jdbc.deleteTable("tencentusers");
				//				jdbc.deleteTable("tencentunuseduids");
				//				jdbc.deleteTable("tencentfriends");
				//				jdbc.deleteTable("tencentuserbaseinfo");
				//				jdbc.deleteTable("tencenttablenames");
				// tencentusers
				jdbc.createUsersTable("tencentusers");
				// tencentunuseduids
				jdbc.createUsersTable("tencentunuseduids");
				// tencentfriends
				jdbc.createFriendsTable("tencentfriends");
				// tencentuserbaseinfo
				jdbc.createBaseInfoTable("tencentuserbaseinfo");
				// tencenttablenames
				jdbc.createTablenames("tencenttablenames");

				jdbc.sqlClose();
			}
		}
	}

	public void disUidsToDBs() throws SQLException, IOException {

		int[] ips = { 26, 28, 29, 30, 31, 32, 50, 34, 51, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 47 };
		BufferedReader br = new BufferedReader(new FileReader(new File("part-r-00000")));
		List<String> uids = new ArrayList<String>();
		String uid = "";
		int i = 0, index = 0;
		UsernameJDBC myJdbc = new UsernameJDBC("192.168.1." + ips[index]);
		myJdbc.mysqlStatus();
		while ((uid = br.readLine()) != null) {
			if (i % 10000 == 0) {
				System.out.println(i);
				myJdbc.insertUidsBatch("tencentusers", uids);
				uids = new ArrayList<String>();
				uids.add(uid.substring(0, uid.length() - 1).trim());
			} else {
				uids.add(uid.substring(0, uid.length() - 1).trim());
			}
			if ((i % 1000000 == 0) && (i < 19000009)) {
				System.out.println("ip is: 192.168.1." + ips[index]);
				myJdbc.sqlClose();
				myJdbc = new UsernameJDBC("192.168.1." + ips[index]);
				myJdbc.mysqlStatus();
				index++;
			}
			i++;
		}
		myJdbc.insertUidsBatch("tencentusers", uids);
		br.close();
		myJdbc.sqlClose();
	}

	public void reDuplicate() throws SQLException {

		UsernameJDBC myJdbc = new UsernameJDBC(Nettool.getServerLocalIp());
		if (myJdbc.mysqlStatus()) {
			HashMap<String, String> tencentusers = myJdbc.getTencentUsers("tencentusers");
			int i = 0;
			List<String> uids = new ArrayList<String>();
			for (Entry<String, String> temp : tencentusers.entrySet()) {
				if (i++ % 10000 == 0) {
					myJdbc.insertUidsBatch("tencentusers1", uids);
					uids = new ArrayList<String>();
					uids.add(temp.getKey());
				} else {
					uids.add(temp.getKey());
				}
			}
			myJdbc.sqlClose();
		}
	}

	public void fromTxtToDb() throws IOException, SQLException {

		UsernameJDBC myJdbc = new UsernameJDBC(Nettool.getServerLocalIp());
		if (myJdbc.mysqlStatus()) {
			BufferedReader br = new BufferedReader(new FileReader(new File("part-r-00000")));
			List<String> uids = new ArrayList<String>();
			String uid = "";
			int i = 0;
			while ((uid = br.readLine()) != null) {
				if (i++ % 10000 == 0) {
					myJdbc.insertUidsBatch("tencentusers", uids);
					uids = new ArrayList<String>();
					uids.add(uid.substring(0, uid.length() - 1).trim());
				} else {
					uids.add(uid.substring(0, uid.length() - 1).trim());
				}
				if (i % 100000 == 0) {
					System.out.println(i);
				}
			}
			br.close();
			myJdbc.sqlClose();
		}
	}

}
