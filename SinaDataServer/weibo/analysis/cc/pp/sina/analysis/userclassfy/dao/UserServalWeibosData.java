package cc.pp.sina.analysis.userclassfy.dao;


public class UserServalWeibosData {

	private static float avereposterquality; // 真实比例
	private static long aveexposionsum; // 不含该用户的粉丝量

	public static float getAvereposterquality() {
		return avereposterquality;
	}

	public static void setAvereposterquality(float avereposterquality) {
		UserServalWeibosData.avereposterquality = avereposterquality;
	}

	public static long getAveexposionsum() {
		return aveexposionsum;
	}

	public static void setAveexposionsum(long aveexposionsum) {
		UserServalWeibosData.aveexposionsum = aveexposionsum;
	}

}
