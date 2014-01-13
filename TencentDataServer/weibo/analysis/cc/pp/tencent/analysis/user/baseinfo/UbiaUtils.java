package cc.pp.tencent.analysis.user.baseinfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UbiaUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UbiaUtils ubiaUtils = new UbiaUtils();
		System.out.println(ubiaUtils.getTableName(new String("12450")));
	}
	
	/**
	 * 根据username判断表名
	 * @param username
	 * @return
	 */
	public String getTableName(String username) {
		
		String tablename = new String("tencentuserinforesult");
		long uid = Long.parseLong(username);
		long index = uid % 5;
		tablename = tablename + index;
		
		return tablename;
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

}
