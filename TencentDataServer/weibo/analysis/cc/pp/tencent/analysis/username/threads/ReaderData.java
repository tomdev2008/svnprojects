package cc.pp.tencent.analysis.username.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cc.pp.tencent.jdbc.UsernameJDBC;
import cc.pp.tencent.utils.Nettool;

public class ReaderData {

	private static final int[] IPS = { 26, 28, 29, 30, 31, 32, 50, 34, 51, //
			36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 47 };

	/**
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws IOException, SQLException {

		ReaderData readerData = new ReaderData();
		//		readerData.createTables();
		readerData.disUidsToDBs();
		//				readerData.dumpFromDbToTxt();
		//		readerData.dropTencentuidsTables();

	}

	public void dropTencentuidsTables() throws SQLException {

		for (int ip = 0; ip < IPS.length; ip++) {

			System.out.println("Drop at : " + ip);

			UsernameJDBC jdbc = new UsernameJDBC("192.168.1." + IPS[ip]);
			if (jdbc.mysqlStatus()) {
				//				List<String> tablenames = jdbc.getUidTTablenames("tencenttablenames");
				//				for (String tablename : tablenames) {
				//					jdbc.deleteTable(tablename);
				//				}
				//				jdbc.deleteTable("tencentusers");
				jdbc.deleteTable("tencenttablenames");
				jdbc.sqlClose();
			}
		}
	}

	public void createTables() throws SQLException {

		for (int ip = 0; ip < IPS.length; ip++) {
			System.out.println("ip is: 192.168.1." + IPS[ip]);
			UsernameJDBC jdbc = new UsernameJDBC("192.168.1." + IPS[ip]);
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
				//				jdbc.createUsersTable("tencentunuseduids");
				// tencentfriends
				//				jdbc.createFriendsTable("tencentfriends");
				// tencentuserbaseinfo
				//				jdbc.createBaseInfoTable("tencentuserbaseinfo");
				// tencenttablenames
				jdbc.createTablenames("tencenttablenames");

				jdbc.sqlClose();
			}
		}
	}

	public void disUidsToDBs() throws SQLException, IOException {

		BufferedReader br = new BufferedReader(new FileReader(new File("tencentuids18")));
		List<String> uids = new ArrayList<String>();
		String uid = "";
		int i = 0, index = 0;
		UsernameJDBC myJdbc = new UsernameJDBC("192.168.1." + IPS[index]);
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
			if ((i % 900_0000 == 0) && (i < 1_7100_0010)) {
				System.out.println("ip is: 192.168.1." + IPS[index]);
				myJdbc.sqlClose();
				myJdbc = new UsernameJDBC("192.168.1." + IPS[index]);
				myJdbc.mysqlStatus();
				index++;
			}
			i++;
		}
		myJdbc.insertUidsBatch("tencentusers", uids);
		br.close();
		myJdbc.sqlClose();
	}

	/**
	 * 将数据库中数据导出为txt文件
	 * @throws SQLException
	 * @throws IOException
	 */
	public void dumpFromDbToTxt() throws SQLException, IOException {

		for (int ip = 0; ip < IPS.length; ip++) {

			System.out.println("ip is: 192.168.1." + IPS[ip]);

			UsernameJDBC jdbc = new UsernameJDBC("192.168.1." + IPS[ip]);
			if (jdbc.mysqlStatus()) {
				// 获取所有表名
				List<String> tablenames = jdbc.getUidTTablenames("tencenttablenames");
				// 循环读取每个数据表，并存储为响应的txt文件
				for (String tablename : tablenames) {

					System.out.println("              " + tablename);

					List<String> uids = jdbc.getUids(tablename);
					BufferedWriter bw = new BufferedWriter(new FileWriter( //
							new File("usernames/" + IPS[ip] + "_" + tablename + ".txt")));
					for (String uid : uids) {
						bw.append(uid);
						bw.newLine();
					}
					bw.close();
				}

				jdbc.sqlClose();
			}
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

}
