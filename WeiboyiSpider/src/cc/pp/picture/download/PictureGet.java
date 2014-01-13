package cc.pp.picture.download;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 图片采集并存储
 */
@SuppressWarnings("restriction")
public class PictureGet {

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		PictureGet.createImage("http://img.weiboyi.com/data2images/0/898/92e8a1c4da8c7a4ac86aff813b3e7049.png", //
				"111.bmp", "pricepic/");

	}

	/**
	 * 下载图片
	 */
	public static void createImage(String urlAdd, String fileName, //
			String uploadDir) throws Exception {

		URL url = null;
		try {
			url = new URL(urlAdd);
		} catch (MalformedURLException e) {
			return;
		}
		Image src = javax.imageio.ImageIO.read(url); // 构造Image对象
		int wideth = src.getWidth(null); // 得到源图宽
		int height = src.getHeight(null); // 得到源图长
		BufferedImage tag = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
		tag.getGraphics().drawImage(src, 0, 0, wideth, height, null); // 绘制缩小后的图
		FileOutputStream out = new FileOutputStream(uploadDir.concat(fileName)); // 输出到文件流
		//		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		//		encoder.encode(tag); // 近JPEG编码
		out.close();
	}

}
