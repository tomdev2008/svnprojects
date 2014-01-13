package cc.pp.sina.analysis.username;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import cc.pp.sina.jdbc.UidJDBC;

public class DataTackle {

	public static void main(String[] args) throws SQLException, IOException {

		DataTackle.emptyTables();
	}

	public static void emptyTables() throws SQLException {

		UidJDBC myJdbc = new UidJDBC("192.168.1.48");
		if (myJdbc.mysqlStatus()) {
			for (int i = 0; i < 32; i++) {
				System.out.println(i);
				myJdbc.truncateTable("sinaunuseduids_" + i);
				myJdbc.truncateTable("sinausers_" + i);
			}
			myJdbc.sqlClose();
		}
	}

	public static void dataDbToTxt() throws IOException, SQLException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("sinauids_old")));
		UidJDBC uidjdbc = new UidJDBC("192.168.1.48");
		if (uidjdbc.mysqlStatus()) {
			for (int i = 0; i < 32; i++) {

				System.out.print("Read at: " + i);

				List<String> sinausers = uidjdbc.getSinaUidById("sinauserbaseinfo_" + i);

				System.out.println(", size is " + sinausers.size());

				for (String uid : sinausers) {
					bw.append(uid);
					bw.newLine();
				}
			}
			uidjdbc.sqlClose();
		}
		bw.close();
	}

}
