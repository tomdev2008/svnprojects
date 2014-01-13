package cc.pp.tencent.analysis.userfans.interaction;

import java.io.Serializable;
import java.util.TimerTask;

/**
 * Title: 定时任务执行类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskUFI extends TimerTask implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		try {
			String ip = "192.168.1.27";
			InteractionsAnalysis ia = new InteractionsAnalysis(ip);
			ia.analysis();
			UfiUtils ufiaUtils = new UfiUtils();
			ufiaUtils.moveResult(ip);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
