package cc.pp.spider.test;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String str = "background:url(http://img.weiboyi.com/data2images/11/59704/532266b2aef034ca2c319b0194dcec55.png) no-repeat center -27px;";
		System.out.println(str.substring(str.indexOf("(") + 1, str.indexOf(")")));

		String str1 = "文贤贤蛋 北京,崇文区";
		System.out.println(str1.substring(0, str1.indexOf(" ")));

	}

}
