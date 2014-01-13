package cc.pp.sina.analysis.user.baseinfo.threads;

import java.io.Serializable;
import java.util.Timer;

public class TimerTask implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Timer timer = new Timer();
		/**
		 * 第一个参数：所执行的任务
		 * 第二个参数：开始执行的时间（毫秒）
		 * 第三个参数：定时执行的时间（毫秒）
		 */
		timer.schedule(new Task(), 0, 1000 * 86400);

	}

}
