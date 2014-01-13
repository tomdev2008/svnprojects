package cc.pp.sina.analysis.user.baseinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimerTask;

import org.apache.commons.httpclient.HttpException;

import com.sina.weibo.json.JSONException;
import com.sina.weibo.model.WeiboException;


/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskUBIA extends TimerTask {

	@Override
	public void run() {
		/*********************获取新增授权用户***********************/
		try {
			/*********************分析数据***********************/
			BaseInfoAnalysis ubia = new BaseInfoAnalysis("192.168.1.27");
			ubia.analysis();
			/*********************转移数据***********************/
			MoveResult mr = new MoveResult("192.168.1.27");
			mr.moveResult();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
