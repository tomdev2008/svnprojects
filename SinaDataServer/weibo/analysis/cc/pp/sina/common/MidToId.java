package cc.pp.sina.common;

/**
 * Title: 微博设备来源分析相关数据
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class MidToId {

	/**
	 * @param args
	 */
	public String dict = new String("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
	
	public String mid2id(String url) {
		String temp = new String();
		String result = new String();
		int start1 = url.indexOf("?");
		if (start1 == -1) {
			int start = url.lastIndexOf("/");
			if (start == -1) {
				temp = url;
			} else {
				temp = url.substring(start + 1);
			}
		} else {
			temp = url.substring(start1 - 9, start1);
		}		
		char a = temp.charAt(0);
		int t = dict.indexOf(a);

		result = String.valueOf(t)
				 + convert62to10(temp.substring(1, 5)) 
				 + convert62to10(temp.substring(5, 9));

		return result;
	}
	
	public String convert62to10(String str62) {
		long total = 0;
		char[] words = str62.toCharArray();
		int len = str62.length();
		for (int i = 0; i < len; i++) {
			total += (long) Math.pow(62, len - i - 1)*dict.indexOf(words[i]);
		}
		String temp = String.valueOf(total);
		String result = new String("");
		int len1 = temp.length();
		if (len1 > 7) {
//			System.out.println("Error Message");
			return "0";
		} else {
			for (int i = 0; i < 7 - len1; i++) {
				result += "0";
			}
			for (int j = 7 - len1; j < 7; j++) {
				result += temp.charAt(j + len1 - 7);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MidToId t = new MidToId();
//		String test = new String("z47uVsZrr");
		String test = new String("http://weibo.com/1644395354/ztkfl7xaf");
////		String test = new String("http://e.weibo.com/2652996131/z47uVsZrr?ref=http%3A%2F%2Fweibo.com%2Fu%2F1862087393%3Fwvr%3D3.6%26lf%3Dreg");
		String wid = t.mid2id(test);
		System.out.println(wid);

	}

}




