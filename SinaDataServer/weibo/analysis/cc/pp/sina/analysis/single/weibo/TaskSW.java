package cc.pp.sina.analysis.single.weibo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.TimerTask;

import javax.management.RuntimeErrorException;

import net.sf.json.JSONArray;
import cc.pp.sina.common.MidToId;
import cc.pp.sina.jdbc.WeiboJDBC;
import cc.pp.sina.result.SimpleWeiboResult;
import cc.pp.sina.utils.GetWeiboLists;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskSW extends TimerTask {

	@Override
	public void run() {
		//
		try {
			/*********************获取待分析微博列表***********************/
			String ip = "192.168.1.27";
			GetWeiboLists getwb = new GetWeiboLists(ip);
			getwb.getWbLists();
			/*********************循环分析微博并存储***********************/
			String uid, url, info;
			HashMap<Integer, String> wblists = null;
			WeiboJDBC weibojdbc = new WeiboJDBC(ip);
			if (weibojdbc.mysqlStatus()) {
				wblists = weibojdbc.getWbListsByDB("sinaweibolists");
				weibojdbc.sqlClose();
			}

			for (int i = 0; i < wblists.size(); i++) {

				info = wblists.get(i);
				uid = info.substring(0, info.indexOf("="));
				url = info.substring(info.indexOf("=") + 1);
				// 更新线上微博状态
				GetWeiboLists gw = new GetWeiboLists(ip);
				gw.getAddWeibos(gw.getUpdateUrl(uid, "sina", url));
				// 分析微博并插入结果数据
				SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis(ip);
				int apicount = swa.analysis(url);
				swa.closeMysql();
				if (apicount == -1) {
					// 插入问题微博
					//						weibojdbc.insertIssueWbs("sina", uid, url);
					MidToId midtoid = new MidToId();
					WeiboJDBC mysql = new WeiboJDBC("192.168.1.154");
					if (mysql.mysqlStatus()) {
						SimpleWeiboResult data = new SimpleWeiboResult();
						JSONArray jsondataArray = JSONArray.fromObject(data);
						mysql.insertWeiboResult(midtoid.mid2id(url), url, jsondataArray.toString());
						mysql.sqlClose();
					}
				}
			}

			weibojdbc = new WeiboJDBC(ip);
			if (weibojdbc.mysqlStatus()) {
				weibojdbc.truncateTables("sinaweibolists");
				weibojdbc.sqlClose();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeErrorException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
