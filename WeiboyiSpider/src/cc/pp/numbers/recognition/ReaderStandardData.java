package cc.pp.numbers.recognition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.pp.image.dao.NumberDao;
import cc.pp.image.utils.ImageReader;

public class ReaderStandardData {

	private final String imagesDir;

	public ReaderStandardData(String imagesDir) {
		this.imagesDir = imagesDir;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		ReaderStandardData rsd = new ReaderStandardData("standardimages/");
		List<NumberDao> numbers = rsd.getStandardData();
		System.out.println(numbers.size());
		for (int i = 1; i < numbers.size(); i++) {
			System.out.println(numbers.get(i).getName());
		}

	}

	/**
	 * 获取标准图像数据
	 * @return
	 * @throws IOException
	 */
	public List<NumberDao> getStandardData() throws IOException {

		List<NumberDao> numbers = new ArrayList<NumberDao>();
		File files = new File(imagesDir);
		File[] listfiles = files.listFiles();
		for (File file : listfiles) {
			NumberDao temp = new NumberDao();
			String name = file.getName();
			temp.setName(name.substring(0, name.indexOf(".")));
			temp.setData(ImageReader.grayToMatrix(ImageReader.readerIm(imagesDir + name)));
			numbers.add(temp);
		}

		return numbers;
	}

}
