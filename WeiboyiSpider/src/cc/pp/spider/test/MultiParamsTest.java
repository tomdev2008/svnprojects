package cc.pp.spider.test;

public class MultiParamsTest {

	private void noParams() {
		System.out.println("test");
	}

	private void multiParams(String... strings) {
		for (String str : strings) {
			System.out.print(str + ", ");
		}
		System.out.println();
	}

	//	private void multiParams(String[] strings) {
	//		System.out.println(3);
	//	}

	public static void main(String[] args) {

		MultiParamsTest mpt = new MultiParamsTest();
		mpt.noParams();
		mpt.multiParams(new String[] { "aaa", "bbb" });
		mpt.multiParams("ccc");
	}

}

