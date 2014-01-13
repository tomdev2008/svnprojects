package cc.pp.tencent.analysis.username.threads;

import java.util.ArrayList;
import java.util.List;

public class ListsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		list1.add("a");
		list1.add("b");
		list2.add("aa");
		list2.add("bb");
		System.out.println(list1);
		System.out.println(list2);
		list1.addAll(list2);
		System.out.println(list1);
	}

}
