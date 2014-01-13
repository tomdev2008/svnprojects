package cc.pp.sina.analysis.ppusers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cc.pp.sina.jdbc.PPUJDBC;

public class PPAnalysis {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		PPAnalysis ppsa = new PPAnalysis();
		ppsa.sinaAnalysis();
	}

	public void sinaDumpToDB() throws SQLException {

		List<PPSinaUser> ppusers = this.getSinaUsers();
		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			for (PPSinaUser user : ppusers) {
				if (user.getVerifytype() == 2 || user.getVerifytype() == 5 || user.getVerifytype() == 7) {
					myjdbc.insertUsersInfo("sinacompanys", user.getUsername(), user.getNickname(),
							user.getDescription(), user.getGender(), user.getProvince(), user.getFanscount(),
							user.getFriendscount(), user.getWeibocount(), user.getVerify(), user.getVerifytype(),
							user.getVerifiedreason(), user.getLastwbcreated());
				}
			}
			myjdbc.sqlClose();
		}
	}

	public void tencentDumpToDB() throws SQLException {

		List<PPTencentUser> ppusers = this.getTencentUsers();
		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			for (PPTencentUser user : ppusers) {
				if (user.getIsent() == 1) {
					myjdbc.insertTencentUsersInfo("tencentcompanys", user.getUsername(), user.getNickname(),
							user.getDescription(), user.getIsvip(), user.getFanscount(), user.getFriendscount(),
							user.getWeibocount(), user.getExp(), user.getIsrealname(), user.getLevel(), user.getSex(),
							user.getIndustrycode(), user.getIsent(), user.getProvince(), user.getLastwbcreated(),
							user.getVerifiedreason());
				}
			}
			myjdbc.sqlClose();
		}
	}

	public void sinaAnalysis() throws SQLException {

		List<PPSinaUser> ppusers = this.getSinaUsers();
		int sum = ppusers.size();
		int[] gender = new int[2];
		int company = 0;
		int activecompany = 0;
		int daren = 0;
		int hasverifiedreason = 0;
		int active7 = 0;
		int active3 = 0;
		long time = System.currentTimeMillis() / 1000;
		for (PPSinaUser user : ppusers) {
			gender[user.getGender()]++;
			if (user.getVerifytype() == 2 || user.getVerifytype() == 5 || user.getVerifytype() == 7) {
				company++;
				if (time - user.getLastwbcreated() < 3 * 86400) {
					activecompany++;
				}
			}
			if (user.getVerifytype() == 200 || user.getVerifytype() == 220) {
				daren++;
			}
			if (user.getVerifiedreason().length() > 0) {
				hasverifiedreason++;
			}
			if (time - user.getLastwbcreated() < 7 * 86400) {
				active7++;
			}
			if (time - user.getLastwbcreated() < 3 * 86400) {
				active3++;
			}
		}
		float companyratio = (float) company / sum;
		float activecompanyratio = (float) activecompany / company;
		float darenratio = (float) daren / sum;
		float hasverifiedreasonratio = (float) hasverifiedreason / sum;
		float active7ratio = (float) active7 / sum;
		float active3ratio = (float) active3 / sum;
		System.out.println("Sum: " + sum);
		System.out.println("company: " + company + ", companyratio: " + companyratio);
		System.out.println("activecompany: " + activecompany + ", activecompanyratio: " + activecompanyratio);
		System.out.println("daren: " + daren + ", darenratio: " + darenratio);
		System.out.println("hasverifiedreason: " + hasverifiedreason + ", hasverifiedreasonratio: "
				+ hasverifiedreasonratio);
		System.out.println("active7: " + active7 + ", active7ratio: " + active7ratio);
		System.out.println("active3: " + active3 + ", active3ratio: " + active3ratio);

	}

	public List<PPSinaUser> getSinaUsers() throws SQLException {

		List<PPSinaUser> ppusers = new ArrayList<PPSinaUser>();
		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			ppusers = myjdbc.getResult("sinauserinfo");
			myjdbc.sqlClose();
		}
		return ppusers;
	}

	public void tencentAnalysis() throws SQLException {

		List<PPTencentUser> ppusers = this.getTencentUsers();
		int sum = ppusers.size();
		//		int[] gender = new int[3];
		int company = 0;
		int activecompany = 0;
		int isrealname = 0;
		int isvip = 0;
		int hasverifiedreason = 0;
		int active3 = 0;
		long time = System.currentTimeMillis() / 1000;
		for (PPTencentUser user : ppusers) {
			//			gender[user.getSex()]++;
			if (user.getIsent() == 1) {
				company++;
				if (time - user.getLastwbcreated() < 3 * 86400) {
					activecompany++;
				}
			}
			if (user.getIsrealname() == 1) {
				isrealname++;
			}
			if (user.getIsvip() == 1) {
				isvip++;
			}
			if (user.getVerifiedreason().length() > 0) {
				hasverifiedreason++;
			}
			if (time - user.getLastwbcreated() < 3 * 86400) {
				active3++;
			}
		}
		float companyratio = (float) company / sum;
		float activecompanyratio = (float) activecompany / company;
		float isrealnameratio = (float) isrealname / sum;
		float isvipratio = (float) isvip / sum;
		float hasverifiedreasonratio = (float) hasverifiedreason / sum;
		float active3ratio = (float) active3 / sum;
		System.out.println("Sum: " + sum);
		System.out.println("company: " + company + ", companyratio: " + companyratio);
		System.out.println("activecompany: " + activecompany + ", activecompanyratio: " + activecompanyratio);
		System.out.println("isrealname: " + isrealname + ", darenratio: " + isrealnameratio);
		System.out.println("isvip" + isvip + ", isvip: " + isvipratio);
		System.out.println("hasverifiedreason: " + hasverifiedreason + ", hasverifiedreasonratio: "
				+ hasverifiedreasonratio);
		System.out.println("active3: " + active3 + ", active3ratio: " + active3ratio);
	}

	public List<PPTencentUser> getTencentUsers() throws SQLException {

		List<PPTencentUser> ppusers = new ArrayList<PPTencentUser>();
		PPUJDBC myjdbc = new PPUJDBC("192.168.1.151");
		if (myjdbc.mysqlStatus()) {
			ppusers = myjdbc.getTencentResult("tencentuserinfo");
			myjdbc.sqlClose();
		}

		return ppusers;
	}


}
