package cc.pp.sina.analysis.topic;


public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//		long time = System.currentTimeMillis() / 1000;
		//		SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
		//		String date = fomat.format(new Date(time * 1000l));
		//		System.out.println(date);
		//		System.out.println(time);
		String str = "\\xFD\\xCE\\xB9\\xA4\\xD7\\xF7...";
		System.out.println(str.replaceAll("x", ""));
	}

}
