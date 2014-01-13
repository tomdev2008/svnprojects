package cc.pp.sina.analysis.userclassfy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import cc.pp.sina.jdbc.LocalJDBC;
import cc.pp.sina.jdbc.UserClassfyJDBC;

public class TxtReader {
	
	private String ip = "";

	public TxtReader(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, SQLException {

		TxtReader tr = new TxtReader("192.168.1.191");
		tr.importDataToDb();

	}
	
	public void importDataToDb() throws IOException, SQLException {

		HashMap<Integer, String> sinanickusername = TxtReader.readerData("sina", "username");
		LocalJDBC lJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (lJdbc.mysqlStatus()) {
			for (int i = 0; i < sinanickusername.size(); i++) {
				lJdbc.insertUnusedUsers("sinausers", sinanickusername.get(i));
			}
			lJdbc.sqlClose();
		}
	}

	public void writeData() throws IOException, SQLException {
		
		HashMap<Integer,String> sinanickusername = TxtReader.readerData("sina", "username");
//		System.out.println(sinanickusername.size());
		UserClassfyJDBC jdbc = new UserClassfyJDBC(this.ip);
		if (jdbc.mysqlStatus()) {
			for (int i = 0; i < sinanickusername.size(); i++) {
				jdbc.insertUnusedUsers("sinausers", sinanickusername.get(i));
			}
			jdbc.sqlClose();
		}
		
	}

	@SuppressWarnings("resource")
	public static HashMap<Integer,String> readerData(String type, String name) throws IOException {
		
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String filename = "testdata/" + type + name + ".txt";
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		String nickname = new String();
		int i = 0;
		while ((nickname = br.readLine()) != null) {
			System.out.println(nickname);
			//			result.put(i++, nickname);
			if (name.equals("username")) {
				if (type.equals("sina")) {
					result.put(i++, nickname.substring(17));
				} else {
					result.put(i++, nickname.substring(16));
				}
			} else {
				result.put(i++, nickname);
			}
		}
		
		return result;
	}
	
	public void unionUids() throws SQLException {
		
		UserClassfyJDBC ucjdbc = new UserClassfyJDBC("192.168.1.151");
		if (ucjdbc.mysqlStatus()) {
			HashMap<Integer, String> uids = this.getUids("192.168.1.181", "sinausers_original", 2000000);
			for (int i = 0; i < uids.size(); i++) {
				ucjdbc.insertSinaUsers("sinausers", uids.get(i));
			}
			ucjdbc.sqlClose();
		}
	}
	
	public HashMap<Integer, String> getUids(String ip, String tablename, int num) throws SQLException {
		
		UserClassfyJDBC ucjdbc = new UserClassfyJDBC(ip);
		if (ucjdbc.mysqlStatus()) {
			HashMap<Integer,String> uids = ucjdbc.getUsernames(tablename, num);
			ucjdbc.sqlClose();
			return uids;
		} else {
			return null;
		}
	}

	public void distributeData() throws SQLException {
		
		HashMap<Integer, String> sinausers = this.getUids("192.168.1.151", "sinausers", 1800000);
		for (int ip = 0; ip < 10; ip++) {
			UserClassfyJDBC ucJdbc = new UserClassfyJDBC("192.168.1." + ip + 180);
			if (ucJdbc.mysqlStatus()) {
				if (ip == 9) {
					for (int i = 9 * 170000; i < sinausers.size(); i++) {
						ucJdbc.insertSinaUsers("sinausers", sinausers.get(i));
					}
				} else {
					for (int i = ip * 170000; i < (ip + 1) * 170000; i++) {
						ucJdbc.insertSinaUsers("sinausers", sinausers.get(i));
					}
				}
				ucJdbc.sqlClose();
			}
		}
	}

}
