package cc.pp.tencent.analysis.userweibo.spider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternNumbers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String s = "全部阅读次数112814次";
		Pattern patt = Pattern.compile("[1-9]");
		Matcher matc = patt.matcher(s);
		while (matc.find()) {
			System.out.print(matc.group());
		}

	}

}
