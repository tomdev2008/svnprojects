package cc.pp.sina.analysis.userclassfy.dao;

public class UserSingleWeiboData {

	private static int[] reposterquality;
	private static long exposionsum; // 不含该用户的粉丝量

	public static int[] getReposterquality() {
		return reposterquality;
	}

	public static void setReposterquality(int[] reposterquality) {
		UserSingleWeiboData.reposterquality = reposterquality;
	}

	public static long getExposionsum() {
		return exposionsum;
	}

	public static void setExposionsum(long exposionsum) {
		UserSingleWeiboData.exposionsum = exposionsum;
	}

}
