package cc.pp.tencent.analysis.single.weibo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.TimerTask;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.URIException;

import cc.pp.tencent.jdbc.WeiboJDBC;
import cc.pp.tencent.result.SimpleWeiboResult;
import cc.pp.tencent.utils.GetWeiboLists;

/**
 * Title: 定时任务
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TaskSW extends TimerTask {

	@Override
	public void run() {
		try {
			String ip = "192.168.1.27";
			/*********************循环分析微博并存储***********************/
			String uid, url, info;
			int apicount;
			HashMap<Integer, String> wblists = null;
			WeiboJDBC weibojdbc = new WeiboJDBC(ip);
			if (weibojdbc.mysqlStatus()) {
				wblists = weibojdbc.getWbListsByDB("tencentweibolists");
				weibojdbc.sqlClose();
			}

			for (int i = 0; i < wblists.size(); i++) {
				info = wblists.get(i);
				uid = info.substring(0, info.indexOf("="));
				url = info.substring(info.indexOf("=") + 1);
				// 更新线上微博状态
				GetWeiboLists gw = new GetWeiboLists(ip);
				gw.getAddWeibos(gw.getUpdateUrl(uid, "tencent", url));
				// 分析微博并插入结果数据
				SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis(ip);
				apicount = swa.analysis(url);
				if (apicount == -1) {
					// 插入问题微博
					//					weibojdbc.insertIssueWbs("tencent", uid, url);
					SWeiboUtils weiboUtils = new SWeiboUtils();
					WeiboJDBC mysql = new WeiboJDBC("192.168.1.154");
					if (mysql.mysqlStatus()) {
						SimpleWeiboResult data = new SimpleWeiboResult();
						JSONArray jsondataArray = JSONArray.fromObject(data);
						mysql.insertWeiboResult(weiboUtils.getWid(url), url, jsondataArray.toString());
						mysql.sqlClose();
					}
				}
			}

			weibojdbc = new WeiboJDBC(ip);
			if (weibojdbc.mysqlStatus()) {
				weibojdbc.truncateTables("tencentweibolists");
				weibojdbc.sqlClose();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (URIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
