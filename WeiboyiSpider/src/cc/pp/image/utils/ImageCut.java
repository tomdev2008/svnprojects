package cc.pp.image.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCut {

	/**
	 * 缩放图像
	 * 
	 * @param srcImageFile
	 *            源图像文件地址
	 * @param result
	 *            缩放后的图像地址
	 * @param scale
	 *            缩放比例
	 * @param flag
	 *            缩放选择:true 放大; false 缩小;
	 */
	public static void scale(String srcImageFile, String result, int scale, boolean flag) {

		try {
			BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件
			int width = src.getWidth(); // 得到源图宽
			int height = src.getHeight(); // 得到源图长
			if (flag) { // 放大
				width = width * scale;
				height = height * scale;
			} else { // 缩小
				width = width / scale;
				height = height / scale;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT); // 返回图像的缩放版本。默认的图像缩放算法
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // 预定义一个图像
			Graphics g = tag.getGraphics(); // 返回Graphics，可用于绘制预定义的图像。
			g.drawImage(image, 0, 0, null); // 用图像的缩放版本去绘制缩放后的图
			g.dispose(); // 释放此图形的上下文以及它使用的所有系统资源。调用 dispose 之后，就不能再使用 Graphics
			// 对象
			ImageIO.write(tag, "JPEG", new File(result)); // 输出到文件流
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ImageCut.scale("D://a.jpg", "D://b.jpg", 4, false);
	}
}
