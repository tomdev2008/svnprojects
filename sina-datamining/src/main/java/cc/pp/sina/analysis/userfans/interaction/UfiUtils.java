package cc.pp.sina.analysis.userfans.interaction;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UfiUtils {
	
	/**
	 * 获取今天日期
	 * @return
	 */
	public String getTodayDate() {
		
		long time = System.currentTimeMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time*1000l));
		date = date.replaceAll("-", "");
		
		return date;
	}
	
	/**
	 * 获取昨天日期
	 * @return
	 */
	public String getYesteydayDate() {
		
		long time = System.currentTimeMillis()/1000 - 86400;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time*1000l));
		date = date.replaceAll("-", "");
		
		return date;
	}

	/**
	 * 数据转换
	 * @param emotions
	 * @return
	 */
	public int[] transData(int[] emotions) {
		
		int[] result = new int[emotions.length];
		int sum = emotions[0] + emotions[1] + emotions[2];
		float negative = (float) emotions[0] / sum;
		float positive = (float) emotions[1] / sum;
		float neutral = (float) emotions[2] / sum;
		if (negative > 0.5) {
			result[0] =  (int) Math.round(Math.random()*(float)emotions[0]);
			result[1] = emotions[0] - result[0] + emotions[1];
			result[2] = emotions[2];
			return result;
		}
		if (positive > 0.95) {
			result[1] =  (int) Math.round(Math.random()*(float)emotions[1]);
			result[2] = emotions[1] - result[1] + emotions[2];
			result[0] = emotions[0];
			return result;
		}
		if (neutral > 0.95) {
			result[2] =  (int) Math.round(Math.random()*(float)emotions[2]);
			result[1] = emotions[2] - result[2] + emotions[1];
			result[0] = emotions[0];
			return result;
		}
		result = emotions;
		
		return result;
	}
	
	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		UfiUtils ufiUtils = new UfiUtils();
		int[] emotions = {2, 0, 0};
		int[] result = ufiUtils.transData(emotions);
		System.out.println(result[0]);
		System.out.println(result[1]);
		System.out.println(result[2]);
	}
}
