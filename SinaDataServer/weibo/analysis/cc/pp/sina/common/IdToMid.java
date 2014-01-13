package cc.pp.sina.common;

public class IdToMid {

	public String dict = new String("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

	/**
	 * 微博ID转换成微博ID字符串
	 * @param id
	 * @return
	 */
	public String id2mid(String id) {

		int len = id.length();
		String str1 = convert10to62(id.substring(len - 7));
		String str2 = convert10to62(id.substring(len - 14, len - 7));
		String str3 = convert10to62(id.substring(0, len - 14));
		String str = str3 + str2 + str1;
		char temp = str.charAt(0);
		while ('0' == temp) {
			str = str.substring(1);
			temp = str.charAt(0);
		}

		return str;
	}

	/**
	 * 10进制转62进制
	 * @param str10
	 * @return
	 */
	public String convert10to62(String str10) {

		String total = "";
		int str = Integer.parseInt(str10);
		int key;
		while (str > 0) {
			key = str % 62;
			str = str / 62;
			total = dict.charAt(key) + total;
		}
		for (int i = total.length(); i < 4; i++) {
			total = "0" + total;
		}

		return total;
	}

	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		//
		IdToMid idtomid = new IdToMid();
		System.out.println(idtomid.id2mid("3569893431795783"));
	}

}
