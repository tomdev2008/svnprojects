package cc.pp.sina.analysis.userbaseinfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TxtWriterTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		/**
		 * 往已有的txt末尾添加数据
		 */
		File f = new File("sina.txt");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true)));
		bw.append("hello");
		bw.newLine();
		bw.close();

	}

}
