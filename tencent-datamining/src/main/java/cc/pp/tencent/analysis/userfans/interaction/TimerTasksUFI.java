package cc.pp.tencent.analysis.userfans.interaction;

import java.util.Timer;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TimerTasksUFI {

	public TimerTasksUFI() {
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
		timer.schedule(new TaskUFI(), 0, 1000*86400);  // 从当前时刻开始，每隔1秒执行一次UpdateDataTask任务

	}
}
