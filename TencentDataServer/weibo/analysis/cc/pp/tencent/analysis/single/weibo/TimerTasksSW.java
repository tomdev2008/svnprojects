package cc.pp.tencent.analysis.single.weibo;

import java.io.Serializable;
import java.util.Timer;

/**
 * Title: 定时任务执行类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TimerTasksSW implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	public TimerTasksSW() {
		//
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 
		Timer timer = new Timer();
		// 使得UpdateDataTask的run方法从当前开始，每隔一小时执行一次
		// 第一个参数：所执行的任务
		// 第二个参数：开始执行的时间（毫秒）
		// 第三个参数：定时执行的时间（毫秒）
		timer.schedule(new TaskSW(), 0, 1000 * 3600 * 2); // 从当前时刻开始，每隔1天执行一次UpdateDataTask任务
	}
}
