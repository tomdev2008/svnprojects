package cc.pp.image.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class ImageReader {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//		BufferedImage im = ImageReader.readerIm("images/C9.bmp");
		//		int[][][] matrixs = ImageReader.rgbToMatrix(im);
		BufferedImage im = ImageReader.readerIm("standardimages/0.bmp");
		int[][] matrixs = ImageReader.grayToMatrix(im);

		for (int i = 1; i < 32; i++) {
			for (int j = 1; j < 32; j++) {
				System.out.print(matrixs[i][j] + " ");
			}
			System.out.println();
		}

	}

	/**
	 * 灰度图像转换成矩阵形式
	 * @param im
	 * @return
	 */
	public static int[][] grayToMatrix(BufferedImage im) {

		int width = im.getWidth();
		int height = im.getHeight();
		int[][] matrixs = new int[width][height];
		int minx = im.getMinX();
		int miny = im.getMinY();
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				int pixel = im.getRGB(i, j);
				if (pixel == -1) {
					matrixs[i][j] = 1;
				} else {
					matrixs[i][j] = 0;
				}
			}
		}

		return matrixs;
	}

	/**
	 * 彩色图像转换成矩阵形式
	 * @param im
	 * @return
	 */
	public static int[][][] rgbToMatrix(BufferedImage im) {

		int width = im.getWidth();
		int height = im.getHeight();
		int[][][] matrixs = new int[width][height][3];
		int minx = im.getMinX();
		int miny = im.getMinY();
		for (int i = minx; i < width; i++) {
			for (int j = miny; j < height; j++) {
				int pixel = im.getRGB(i, j);
				matrixs[i][j][0] = (pixel & 0xff0000) >> 16;
				matrixs[i][j][1] = (pixel & 0xff00) >> 8;
				matrixs[i][j][2] = (pixel & 0xff);
			}
		}

		return matrixs;
	}

	/**
	 * JDK自带的读写图像类型
	 */
	public static void imageIOTypes() {

		String readFormats[] = ImageIO.getReaderFormatNames();
		String writeFormats[] = ImageIO.getWriterFormatNames();
		System.out.println("Readers:  " + Arrays.asList(readFormats));
		System.out.println("Writers:  " + Arrays.asList(writeFormats));
	}

	/**
	 * 从指定路径读图像
	 * @param imagedir
	 * @return
	 * @throws IOException
	 */
	public static BufferedImage readerIm(String imagedir) throws IOException {

		File file = new File(imagedir);
		BufferedImage im = ImageIO.read(file);

		return im;
	}

}
