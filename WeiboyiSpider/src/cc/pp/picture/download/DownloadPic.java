package cc.pp.picture.download;

import java.util.concurrent.atomic.AtomicInteger;

public class DownloadPic implements Runnable {

	private final String priceUrl;
	private final String picName;
	private final String filePath;

	private static AtomicInteger count = new AtomicInteger(0);

	public DownloadPic(String priceUrl, String picName, String filePath) {
		super();
		this.priceUrl = priceUrl;
		this.picName = picName;
		this.filePath = filePath;
	}

	@Override
	public void run() {

		System.out.println(count.addAndGet(1));

		try {
			PictureGet.createImage(priceUrl, picName + ".bmp", filePath);
		} catch (Exception e) {
			//			e.printStackTrace();
		}
	}

}
