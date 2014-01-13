package cc.pp.tencent.analysis.user.baseinfo;

import java.util.TimerTask;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskUBIA extends TimerTask {

	/**
	 * run()函数
	 */
	@Override
	public void run() {
		/*********************获取新增授权用户***********************/
		try {
			String ip = "192.168.1.27";
			/*********************分析数据***********************/
			BaseInfoAnalysis ubia = new BaseInfoAnalysis(ip);
			ubia.analysis();
			/*********************转移数据***********************/
			MoveResult mr = new MoveResult(ip);
			mr.moveResult();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
