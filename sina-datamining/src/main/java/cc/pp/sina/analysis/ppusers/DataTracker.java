package cc.pp.sina.analysis.ppusers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.pp.sina.jdbc.PPUJDBC;

public class DataTracker {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		DataTracker datatracker = new DataTracker();
		datatracker.unionSinaData();
	}

	public void unionSinaData() throws SQLException {

		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			for (int ip = 180; ip < 190; ip++) {
				System.out.println(ip);
				List<PPSinaUser> ppusers = new ArrayList<PPSinaUser>();
				PPUJDBC myjdbct = new PPUJDBC("192.168.1" + ip);
				if (myjdbct.mysqlStatus()) {
					ppusers = myjdbct.getResult("sinauserinfo");
					myjdbct.sqlClose();
				}
				for (PPSinaUser ppuser : ppusers) {
					myjdbc.insertUsersInfo("sinauserinfo", ppuser.getUsername(), ppuser.getNickname(), //
							ppuser.getDescription(), ppuser.getGender(), ppuser.getProvince(), //
							ppuser.getFanscount(), ppuser.getFriendscount(), ppuser.getWeibocount(), //
							ppuser.getVerify(), ppuser.getVerifytype(), ppuser.getVerifiedreason(), //
							ppuser.getLastwbcreated());
				}
			}
			myjdbc.sqlClose();
		}
	}

	public void unionTencentData() throws SQLException {

		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			for (int ip = 180; ip < 190; ip++) {
				List<PPTencentUser> ppusers = new ArrayList<PPTencentUser>();
				PPUJDBC myjdbct = new PPUJDBC("192.168.1" + ip);
				if (myjdbct.mysqlStatus()) {
					ppusers = myjdbct.getTencentResult("tencentuserinfo");
					myjdbct.sqlClose();
				}
				for (PPTencentUser ppuser : ppusers) {
					myjdbc.insertTencentUsersInfo("tencentuserinfo", ppuser.getUsername(), ppuser.getNickname(),
							ppuser.getDescription(), ppuser.getIsvip(), ppuser.getFanscount(),
							ppuser.getFriendscount(), ppuser.getWeibocount(), ppuser.getExp(), ppuser.getIsrealname(),
							ppuser.getLevel(), ppuser.getSex(), ppuser.getIndustrycode(), ppuser.getIsent(),
							ppuser.getProvince(), ppuser.getLastwbcreated(), ppuser.getVerifiedreason());
				}
			}
			myjdbc.sqlClose();
		}
	}

	public void disTencentUsers() throws SQLException {

		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			HashMap<Integer, String> sinausers = myjdbc.getPPSinaUsers("pptencentusers");
			System.out.println(sinausers.size());
			for (int i = 0; i <= 9; i++) {
				System.out.println(i);
				if (i != 9) {
					List<String> uids = new ArrayList<String>();
					for (int j = i * 120000; j < (i + 1) * 120000; j++) {
						uids.add(sinausers.get(j));
					}
					PPUJDBC myjdbct = new PPUJDBC("192.168.1." + i + 180);
					if (myjdbct.mysqlStatus()) {
						myjdbct.insertSinausers("pptencentusers", uids);
						myjdbct.sqlClose();
					}
				} else {
					List<String> uids = new ArrayList<String>();
					for (int j = 9 * 120000; j < sinausers.size(); j++) {
						uids.add(sinausers.get(j));
					}
					PPUJDBC myjdbct = new PPUJDBC("192.168.1." + i + 180);
					if (myjdbct.mysqlStatus()) {
						myjdbct.insertSinausers("pptencentusers", uids);
						myjdbct.sqlClose();
					}
				}
			}
			myjdbc.sqlClose();
		}
	}

	public void disSinaUsers() throws SQLException {

		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			HashMap<Integer, String> sinausers = myjdbc.getPPSinaUsers("ppsinausers");
			System.out.println(sinausers.size());
			for (int i = 0; i <= 9; i++) {
				System.out.println(i);
				if (i != 9) {
					List<String> uids = new ArrayList<String>();
					for (int j = i * 500000; j < (i + 1) * 500000; j++) {
						uids.add(sinausers.get(j));
					}
					PPUJDBC myjdbct = new PPUJDBC("192.168.1." + i + 180);
					if (myjdbct.mysqlStatus()) {
						myjdbct.insertSinausers("ppsinausers", uids);
						myjdbct.sqlClose();
					}
				} else {
					List<String> uids = new ArrayList<String>();
					for (int j = 9 * 500000; j < sinausers.size(); j++) {
						uids.add(sinausers.get(j));
					}
					PPUJDBC myjdbct = new PPUJDBC("192.168.1." + i + 180);
					if (myjdbct.mysqlStatus()) {
						myjdbct.insertSinausers("ppsinausers", uids);
						myjdbct.sqlClose();
					}
				}
			}
			myjdbc.sqlClose();
		}
	}

	public void reduplicate() throws SQLException {
		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			//			myjdbc.deleteTable("temp");
			//			List<String> sinausers = myjdbc.getSinaUsers("sinausers", 6000000);
			HashMap<Integer, String> sinausers = myjdbc.getPPSinaUsers("tencentusers");
			System.out.println(sinausers.size());
			int max = sinausers.size() / 100000;
			System.out.println(max);
			for (int i = 0; i <= max; i++) {
				System.out.println(i);
				if (i != max) {
					List<String> uids = new ArrayList<String>();
					for (int j = i * 100000; j < (i + 1) * 100000; j++) {
						uids.add(sinausers.get(j));
					}
					myjdbc.insertSinausers("pptencentusers", uids);
				} else {
					List<String> uids = new ArrayList<String>();
					for (int j = max * 100000; j < sinausers.size(); j++) {
						uids.add(sinausers.get(j));
					}
					myjdbc.insertSinausers("pptencentusers", uids);
				}
			}
			myjdbc.sqlClose();
		}
	}

}
