package cc.pp.picture.download;

import java.io.File;

public class FileDir {

	/**
	 * 创建文件夹
	 * @param filedir
	 */
	public static void createFileDir(String filedir) {
		
		File file = new File(filedir);
		file.mkdirs();
	}
	
	/**
	 * 判断文件夹是否存在
	 * @param filedir
	 * @return
	 */
	public static boolean isFileDirExist(String filedir) {
		
		File file = new File(filedir);
		if (file.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		FileDir.createFileDir(new String("pricepic"));
		System.out.println(FileDir.isFileDirExist(new String("photo")));
	}

}