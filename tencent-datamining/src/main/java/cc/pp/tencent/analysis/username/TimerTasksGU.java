package cc.pp.tencent.analysis.username;

import java.util.Timer;
public class TimerTasksGU {
	
	/**
	 * 构造函数
	 */
	public TimerTasksGU() {
		//
	}

	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		// 
		Timer timer = new Timer();
		// 使得UpdateDataTask的run方法从当前开始，每隔一小时执行一次
		// 第一个参数：所执行的任务
		// 第二个参数：开始执行的时间（毫秒）
		// 第三个参数：定时执行的时间（毫秒）
		// 从当前时刻开始，每隔1秒执行一次UpdateDataTask任务
		timer.schedule(new TaskGU(), 0, 1000*60*10);  
	}

}
