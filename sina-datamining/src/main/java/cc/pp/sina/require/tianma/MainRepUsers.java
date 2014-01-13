package cc.pp.sina.require.tianma;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cc.pp.sina.jdbc.LocalJDBC;

public class MainRepUsers {

	private final List<String> cates = new ArrayList<String>();
	private final ReaderClassAndKeys reader;

	public MainRepUsers(String filepath) throws IOException {
		reader = new ReaderClassAndKeys(filepath);
		for (List<String> cate : reader.getClassandkeys()) {
			cates.add(cate.get(0));
		}
	}

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SQLException, IOException {

		MainRepUsers repusers = new MainRepUsers("testdata/classandkeys.txt");
		repusers.dumpDistinctUsersToDb();

	}

	/**
	 * 提取不重复的username
	 * @throws SQLException
	 */
	public void dumpDistinctUsersToDb() throws SQLException {

		LocalJDBC jdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (jdbc.mysqlStatus()) {
			for (String cate : cates) {
				jdbc.createUsersTable("sinausers_" + cate);
			}
			for (String cate : cates) {

				System.out.println(cate);

				HashMap<String, String> sinausers = jdbc.getSinaUsers("wbusersinfo_" + cate);
				for (Entry<String, String> temp : sinausers.entrySet()) {
					jdbc.insertSinaUser("sinausers_" + cate, temp.getKey());
				}
			}
		}

	}

	public List<String> getCates() {
		return cates;
	}

	public int getCatesSize() {
		return cates.size();
	}

}
