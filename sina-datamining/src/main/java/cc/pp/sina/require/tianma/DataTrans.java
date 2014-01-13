package cc.pp.sina.require.tianma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cc.pp.sina.constant.SinaConstant;
import cc.pp.sina.jdbc.UidJDBC;

public class DataTrans {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		UidJDBC myjdbc = new UidJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			BufferedReader br = new BufferedReader(new FileReader(new File("users/sinauser.txt")));
			String uid = "";
			while ((uid = br.readLine()) != null) {
				myjdbc.inserUids("sinausers", uid.substring(uid.lastIndexOf("/") + 1));
			}
			br.close();
			myjdbc.sqlClose();
		}

	}

	public void transVerify() throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(new File("testdata/verified.txt")));
		String value = "";
		while ((value = br.readLine()) != null) {
			if (value.contains("#N/A")) {
				System.out.println(SinaConstant.getType(100));
			} else {
				System.out.println(SinaConstant.getType(Integer.parseInt(value)));
			}
		}
		br.close();
	}

}
