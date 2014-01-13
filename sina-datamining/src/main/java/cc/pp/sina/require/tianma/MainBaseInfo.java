package cc.pp.sina.require.tianma;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

public class MainBaseInfo {

	private final Random random = new Random();
	private final HashMap<String, String> bozhuku = new HashMap<String, String>();

	public MainBaseInfo() throws Exception {
		BufferedReader brnick = new BufferedReader(new FileReader(new File("users/nickname.txt")));
		BufferedReader bruid = new BufferedReader(new FileReader(new File("users/username.txt")));
		String nickname, username, uid;
		while (((nickname = brnick.readLine()) != null) && ((uid = bruid.readLine()) != null)) {
			username = uid.substring(uid.lastIndexOf("/") + 1);
			bozhuku.put(username, nickname);
		}
		brnick.close();
		bruid.close();
	}

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws WeiboException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws Exception {

		MainBaseInfo baseinfo = new MainBaseInfo();
		//		baseinfo.dumpBaseinfo();
		baseinfo.dumpToTxt();

	}

	public void dumpToTxt() throws WeiboException, NumberFormatException, IOException, SQLException {

		String[] cates = { "bl", "gz", "it", "ly", "nr", "xs", "zf" };
		for (String cate : cates) {
			BufferedReader fr = new BufferedReader(new FileReader(new File("users/" + cate)));
			BufferedWriter fw = new BufferedWriter(new FileWriter(cate + ".txt", true));
			String str = "";
			String uid, usedcount;
			int i = 0;
			while ((str = fr.readLine()) != null) {
				uid = str.split("\t")[0];
				usedcount = str.split("\t")[1];
				if ((Integer.parseInt(usedcount) > 1000) && (uid.length() > 5) && (bozhuku.get(uid) != null)) {

					System.out.println(cate + ": " + i++);

					fw.append(uid);
					fw.newLine();
				}
			}
			fr.close();
			fw.close();
		}
	}

	public void dumpBaseinfo() throws WeiboException, NumberFormatException, IOException, SQLException {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			List<String> tokens = myjdbc.getAllTokens();
			String[] cates = { "bl", "gz", "it", "ly", "nr", "xs", "zf" };
			for (String cate : cates) {
				myjdbc.createSinauserTable("sinauserinfo_" + cate);
				BufferedReader fr = new BufferedReader(new FileReader(new File("users/" + cate)));
				String str = "";
				String uid, usedcount;
				int i = 0;
				while ((str = fr.readLine()) != null) {

					uid = str.split("\t")[0];
					usedcount = str.split("\t")[1];
					if ((Integer.parseInt(usedcount) > 2500) && uid.length() > 5) {

						System.out.println(cate + ": " + i++);

						Users user = new Users();
						user.client.setToken(tokens.get(random.nextInt(tokens.size())));
						User userinfo = user.showUserById(uid);
						try {
							if (userinfo.getId().length() > 5) {
								myjdbc.insertUsersInfo("sinauserinfo_" + cate, userinfo.getId(),
										userinfo.getScreenName(), Integer.parseInt(usedcount),
										userinfo.getDescription(), userinfo.getGender(), userinfo.getProvince(),
										userinfo.getFollowersCount(), userinfo.getFriendsCount(),
										userinfo.getStatusesCount(), userinfo.isVerified(), userinfo.getVerifiedType(),
										userinfo.getVerifiedReason(), 0);
							} else {
								myjdbc.insertUsersInfo("sinauserinfo_" + cate, uid, "", Integer.parseInt(usedcount),
										"", "", 0, 0, 0, 0, false, -1, "", 0);
							}
						} catch (RuntimeException e) {
							myjdbc.insertUsersInfo("sinauserinfo_" + cate, uid, "", Integer.parseInt(usedcount), "",
									"", 0, 0, 0, 0, false, -1, "", 0);
						}
					}
				}
				fr.close();
			}
			myjdbc.sqlClose();
		}
	}

}
