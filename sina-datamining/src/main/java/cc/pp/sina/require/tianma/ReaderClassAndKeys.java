package cc.pp.sina.require.tianma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReaderClassAndKeys {

	private final List<List<String>> classandkeys = new ArrayList<List<String>>();

	public List<List<String>> getClassandkeys() {
		return classandkeys;
	}

	/**
	 * 构造函数
	 * @throws IOException
	 */
	public ReaderClassAndKeys(String filepath) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
		String str = "";
		while ((str = br.readLine()) != null) {
			List<String> temp = new ArrayList<String>();
			String[] strs = str.split(" ");
			for (String s : strs) {
				temp.add(s.trim());
			}
			classandkeys.add(temp);
		}
		br.close();
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		ReaderClassAndKeys reader = new ReaderClassAndKeys("testdata/classandkeys.txt");
		System.out.println(reader.classandkeys.size());

	}

}
