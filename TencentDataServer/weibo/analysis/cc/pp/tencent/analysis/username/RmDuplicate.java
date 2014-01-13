package cc.pp.tencent.analysis.username;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class RmDuplicate {

	/**
	 * 主函数
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		RmDuplicate.transData();

	}

	public static void transData() throws IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("sinauids")));
		BufferedReader br = new BufferedReader(new FileReader(new File("part-r-00000")));
		String[] info;
		String str;
		int i = 1;
		while ((str = br.readLine()) != null) {
			info = str.split("\t");
			if (Integer.parseInt(info[1]) == 1) {
				bw.append(info[0]);
				bw.newLine();
			}
			if (++i % 100_0000 == 0) {
				System.out.println("Write at: " + i);
			}
		}
		br.close();
		bw.close();
	}

	public static void doRmReplicate() throws IOException {

		HashMap<String, Integer> oldUids = getOldUids("tencentuid03");
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("tencentuids_new")));
		BufferedReader br = new BufferedReader(new FileReader(new File("part-r-00000")));
		String uid = "";
		int i = 1;
		while ((uid = br.readLine()) != null) {
			uid = uid.substring(0, uid.indexOf("\t"));
			if (oldUids.get(uid) != 1) {
				bw.append(uid);
				bw.newLine();
			}
			if (++i % 100_0000 == 0) {
				System.out.println("Write at: " + i);
			}
		}
		br.close();
		bw.close();
	}

	/**
	 * 读取旧用户名
	 */
	public static HashMap<String, Integer> getOldUids(String fileName) throws IOException {

		HashMap<String,Integer> result = new HashMap<>();
		BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
		String uid = "";
		int i = 0;
		while ((uid = br.readLine()) != null) {
			result.put(uid, 1);
			if (++i % 10_0000 == 0) {
				System.out.println("Read at: " + i);
			}
		}
		br.close();

		return result;
	}

}
