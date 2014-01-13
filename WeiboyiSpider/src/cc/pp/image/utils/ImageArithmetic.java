package cc.pp.image.utils;


/**
 * 图像基本操作
 * @author Administrator
 *
 */
public class ImageArithmetic {

	/**
	 * 二值图像刻度为1
	 */
	private final static int SCALE = 1;

	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {

		int[][] t = { { 1, 2, 3 }, { 4, 5, 6 } };
		System.out.println(t[0].length);

	}
	
	/**
	 * 取出行列边缘部分的空白部分
	 * 注意：矩阵的大小会改变
	 * @param im
	 * @return
	 */
	public static int[][] rmWhitesRowAndCol(int[][] im) {

		int rowmax = im.length;
		int colmax = im[0].length;
		int top = 0, bottom = 0, left = 0, right = 0;
		/************进行行处理*************/
		int[] rowsum = rowProjection(im);
		for (int i = 0; i < rowsum.length; i++) {
			if (rowsum[i] < colmax * SCALE) {
				top = i;
				break;
			}
		}
		for (int i = rowsum.length - 1; i >= 0; i--) {
			if (rowsum[i] < colmax * SCALE) {
				bottom = i;
				break;
			}
		}
		/************进行列处理*************/
		int[] colsum = colProjection(im);
		for (int i = 0; i < colsum.length; i++) {
			if (colsum[i] < rowmax * SCALE) {
				left = i;
				break;
			}
		}
		for (int i = colsum.length - 1; i >= 0; i--) {
			if (colsum[i] < rowmax * SCALE) {
				right = i;
				break;
			}
		}
		/************进行重定位*************/
		int[][] tim = new int[bottom-top+1][right-left+1];
		
		for (int i = 0; i < bottom-top+1; i++) {
			for (int j = 0; j < right-left+1; j++) {
				tim[i][j] = im[i + top][j + left];
			}
		}

		return tim;
	}

	/**
	 * 对行投影
	 * @param im
	 * @return
	 */
	public static int[] rowProjection(int[][] im) {

		int colmax = im[0].length;
		int rowmax = im.length;
		int[] rowsum = new int[rowmax];
		for (int i = 0; i < rowmax; i++) {
			for (int j = 0; j < colmax; j++) {
				rowsum[i] = rowsum[i] + im[i][j];
			}
		}

		return rowsum;
	}

	/**
	 * 对列投影
	 * @param im
	 * @return
	 */
	public static int[] colProjection(int[][] im) {

		int colmax = im[0].length;
		int rowmax = im.length;
		int[] colsum = new int[colmax];
		for (int j = 0; j < colmax; j++) {
			for (int i = 0; i < rowmax; i++) {
				colsum[j] = colsum[j] + im[i][j];
			}
		}

		return colsum;
	}

}
