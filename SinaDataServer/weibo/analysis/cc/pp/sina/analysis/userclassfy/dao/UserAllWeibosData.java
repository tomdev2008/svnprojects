package cc.pp.sina.analysis.userclassfy.dao;

import java.util.HashMap;

public class UserAllWeibosData {

	private static long weibosum;
	private static float oriratio;
	private static float aveorirepcom;
	private static float averepcom;
	private static HashMap<String, Integer> wbsource;
	private static float averepcombyweek;
	private static float averepcombymonth;
	private static HashMap<Integer, String> lastweiboids;

	public static long getWeibosum() {
		return weibosum;
	}

	public static void setWeibosum(long weibosum) {
		UserAllWeibosData.weibosum = weibosum;
	}

	public static float getOriratio() {
		return oriratio;
	}

	public static void setOriratio(float oriratio) {
		UserAllWeibosData.oriratio = oriratio;
	}

	public static float getAveorirepcom() {
		return aveorirepcom;
	}

	public static void setAveorirepcom(float aveorirepcom) {
		UserAllWeibosData.aveorirepcom = aveorirepcom;
	}

	public static float getAverepcom() {
		return averepcom;
	}

	public static void setAverepcom(float averepcom) {
		UserAllWeibosData.averepcom = averepcom;
	}

	public static HashMap<String, Integer> getWbsource() {
		return wbsource;
	}

	public static void setWbsource(HashMap<String, Integer> wbsource) {
		UserAllWeibosData.wbsource = wbsource;
	}

	public static float getAverepcombyweek() {
		return averepcombyweek;
	}

	public static void setAverepcombyweek(float averepcombyweek) {
		UserAllWeibosData.averepcombyweek = averepcombyweek;
	}

	public static float getAverepcombymonth() {
		return averepcombymonth;
	}

	public static void setAverepcombymonth(float averepcombymonth) {
		UserAllWeibosData.averepcombymonth = averepcombymonth;
	}

	public static HashMap<Integer, String> getLastweiboids() {
		return lastweiboids;
	}

	public static void setLastweiboids(HashMap<Integer, String> lastweiboids) {
		UserAllWeibosData.lastweiboids = lastweiboids;
	}

}
