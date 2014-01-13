package cc.pp.sina.analysis.userclassfy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import cc.pp.sina.jdbc.UserClassfyJDBC;

/**
 * 用户分类工具类
 * @author Administrator
 *
 */
public class UCUtils {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		UCUtils ucUtils = new UCUtils();
		HashMap<String, String> allusers = ucUtils.getUsers("192.168.1.181");
		for (int ip = 180; ip < 190; ip++) {
			ucUtils.tackleErrorData(allusers, "192.168.1.181", "192.168.1." + ip);
		}

	}
	
	/**
	 * 水军判断
	 */
	public static int checkQuality(boolean addv, int fanssum, int weibosum) {

		if (addv == false) {
			if (fanssum < 50 || weibosum < 20) {
				return 0;
			} else if ((fanssum * 3 < weibosum) && (fanssum < 150)) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}

	/**
	 * 加载数据
	 * @throws SQLException 
	 */
	public HashMap<String, String> getUsers(String ip) throws SQLException {
		
		UserClassfyJDBC ucjdbc = new UserClassfyJDBC(ip);
		if (ucjdbc.mysqlStatus()) {
			HashMap<String,String> originalusers = ucjdbc.getOriginalUsers("sinausers_original");
			ucjdbc.sqlClose();
			return originalusers;
		} else {
			return null;
		}
	}
	
	/**
	 * 处理错误数据
	 * @param ip1
	 * @param ip2
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	public void tackleErrorData(HashMap<String, String> originalusers, String ip1, String ip2) throws SQLException {
		
		UserClassfyJDBC ucjdbc2 = new UserClassfyJDBC(ip2);
		HashMap<String,String> users = null;
		if (ucjdbc2.mysqlStatus()) {
			users = ucjdbc2.getUsersParams("sinauserslibrary");
			ucjdbc2.sqlClose();
		}
		UserClassfyJDBC ucjdbc1 = new UserClassfyJDBC(ip1);
		if (ucjdbc1.mysqlStatus()) {
			String info, username, fanscount, weibocount, averagewbs;
			String influence, activation, activecount, addvratio, headratio;
			String activeratio, maleratio, createdtime, fansexistedratio, description;
			int fanssum, acount, infl;
			float avratio, existedratio, inf;
			for (Entry<String,String> temp : users.entrySet()) {
				username = originalusers.get(temp.getKey());
				info = temp.getValue();
				fanscount = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				weibocount = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				averagewbs = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				influence = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				activation = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				activecount = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				addvratio = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				headratio = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				activeratio = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				maleratio = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				createdtime = info.substring(0, info.indexOf("="));
				info = info.substring(info.indexOf("=") + 1);
				fansexistedratio = info.substring(0, info.indexOf("="));
				description = info.substring(info.indexOf("=") + 1);
				// 计算影响力
				fanssum = Integer.parseInt(fanscount);
				acount = Integer.parseInt(activecount);
				acount = fanssum - acount; // 活跃粉丝数
				avratio = Float.parseFloat(addvratio);
				existedratio = Float.parseFloat(fansexistedratio);
				inf = (float) (0.8*(Math.log(acount)/Math.log(1.5)) + 
						0.1*Math.abs(Math.log(existedratio)/Math.log(2)) + 
						0.1*Math.abs(Math.log(avratio)/Math.log(2)));
				if (inf > 100) {
					inf = 10;
				}
				influence = Integer.toString(Math.round(inf*100/30));
				//				ucjdbc1.insertUsersParams("sinauserslibraryall", username, temp.getKey(),
				//						description.replaceAll("\"", "\\\\\""), fanscount, weibocount, -1, averagewbs, influence,
				//						activation, Integer.toString(acount), addvratio, activeratio, maleratio, createdtime,
				//						fansexistedratio);
			}
			
			ucjdbc1.sqlClose();
		}
	}
	
	/**
	 * 转移数据
	 * @throws SQLException
	 */
	public void transUserData(String ip) throws SQLException {
		
		UserClassfyJDBC ucjdbc = new UserClassfyJDBC(ip);
		if (ucjdbc.mysqlStatus()) {
			
			HashMap<Integer,String> sinausers = ucjdbc.getSinaUsers("sinausers_original");
			String info = new String();
			String username = new String();
			String nickname = new String();
			for (int i = 0; i < sinausers.size(); i++) {
				info = sinausers.get(i);
				username = info.substring(0, info.indexOf(","));
				nickname = info.substring(info.indexOf(",") + 1);
				ucjdbc.insertSinaUsers("sinauser", username, nickname);
			}
			ucjdbc.sqlClose();
		}
	}
	
	public void disUserData(String ip) throws SQLException {
		
		UserClassfyJDBC ucjdbc = new UserClassfyJDBC(ip);
		HashMap<Integer,String> sinausers = null;
		if (ucjdbc.mysqlStatus()) {	
			sinausers = ucjdbc.getSinaUsers("sinausers_original");
			ucjdbc.sqlClose();
		}
		for (int i = 0; i < 11; i++) {
			int ips = i + 180;
			UserClassfyJDBC jdbc = new UserClassfyJDBC("192.168.1." + ips);
			if (jdbc.mysqlStatus()) {	
				for (int j = i*160000; j < (i+1)*160000; j++) {
					jdbc.insertUnusedUsers("sinausers", sinausers.get(j));
				}
				jdbc.sqlClose();
			}
		}
	}

}
