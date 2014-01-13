package cc.pp.tencent.analysis.userfans.interaction;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cc.pp.tencent.jdbc.UfiJDBC;

public class UfiUtils {
	
	/**
	 * 转移数据
	 * @throws SQLException
	 */
	public void moveResult(String ip) throws SQLException {
		
		UfiJDBC myjdbc = new UfiJDBC(ip);
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		UfiUtils ufiUtils = new UfiUtils();
		if (myjdbc.mysqlStatus()) {
			result = myjdbc.getIteractions(ufiUtils.getTodayDate());
			myjdbc.sqlClose();
		}
		/******************插入数据***********************/
		String info = new String();
		String usernameday = new String();
		String allcount = new String();
		String emotionratio = new String();
		
		UfiJDBC jdbc1 = new UfiJDBC("192.168.1.154");
		if (jdbc1.mysqlStatus()) {
			for (int i = 0; i < result.size(); i++) {				
				info = result.get(i);
				emotionratio = info.substring(info.lastIndexOf("=") + 1);
				info = info.substring(0, info.lastIndexOf("="));
				allcount = info.substring(info.lastIndexOf("=") + 1);
				usernameday = info.substring(0, info.lastIndexOf("="));
			
				jdbc1.insertUserIteractions1(usernameday, usernameday.substring(0,usernameday.length()-8), 
						usernameday.substring(usernameday.length()-8), Integer.parseInt(allcount), emotionratio);
			}
			jdbc1.sqlClose();
		}
	}
	
	/**
	 * 转移结果数据
	 * @throws SQLException
	 */
	public void moveResultAll(String ip) throws SQLException {

		UfiJDBC myjdbc = new UfiJDBC(ip);
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		if (myjdbc.mysqlStatus()) {
			result = myjdbc.getAllIteractions();
			myjdbc.sqlClose();
		}
		/******************插入数据***********************/
		String info = new String();
		String usernameday = new String();
		String allcount = new String();
		String emotionratio = new String();

		UfiJDBC jdbc1 = new UfiJDBC("192.168.1.154"); // 转移服务器
		if (jdbc1.mysqlStatus()) {
			for (int i = 0; i < result.size(); i++) {				
				info = result.get(i);
				emotionratio = info.substring(info.lastIndexOf("=") + 1);
				info = info.substring(0, info.lastIndexOf("="));
				allcount = info.substring(info.lastIndexOf("=") + 1);
				usernameday = info.substring(0, info.lastIndexOf("="));
				jdbc1.insertUserIteractions1(usernameday, usernameday.substring(0,usernameday.length()-8), 
						usernameday.substring(usernameday.length()-8), Integer.parseInt(allcount), emotionratio);
			}
			jdbc1.sqlClose();
		}
	}
	
	/**
	 * @获取accesstoken
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessToken(String ip) throws SQLException {

		UfiJDBC jdbc = new UfiJDBC(ip);
		String[] token = null;
		if (jdbc.mysqlStatus()) {
			token = jdbc.getAccessToken();
			jdbc.sqlClose();
		}

		return token;
	}
	
	/**
	 * 获取今天日期
	 * @return
	 */
	public String getTodayDate() {
		
		long time = System.currentTimeMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time*1000l));
		date = date.replaceAll("-", "");
		
		return date;
	}
	
	/**
	 * 获取昨天日期
	 * @return
	 */
	public String getYesteydayDate() {
		
		long time = System.currentTimeMillis()/1000 - 86400;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time*1000l));
		date = date.replaceAll("-", "");
		
		return date;
	}

	/**
	 * 数据转换
	 * @param emotions
	 * @return
	 */
	public int[] transData(int[] emotions) {
		
		int[] result = new int[emotions.length];
		int sum = emotions[0] + emotions[1] + emotions[2];
		float negative = (float) emotions[0] / sum;
		float positive = (float) emotions[1] / sum;
		float neutral = (float) emotions[2] / sum;
		if (negative > 0.5) {
			result[0] =  (int) Math.round(Math.random()*emotions[0]);
			result[1] = emotions[0] - result[0] + emotions[1];
			result[2] = emotions[2];
			return result;
		}
		if (positive > 0.95) {
			result[1] =  (int) Math.round(Math.random()*emotions[1]);
			result[2] = emotions[1] - result[1] + emotions[2];
			result[0] = emotions[0];
			return result;
		}
		if (neutral > 0.95) {
			result[2] =  (int) Math.round(Math.random()*emotions[2]);
			result[1] = emotions[2] - result[2] + emotions[1];
			result[0] = emotions[0];
			return result;
		}
		result = emotions;
		
		return result;
	}
	
	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		UfiUtils ufiUtils = new UfiUtils();
		int[] emotions = {2, 0, 0};
		int[] result = ufiUtils.transData(emotions);
		System.out.println(result[0]);
		System.out.println(result[1]);
		System.out.println(result[2]);
	}
}
