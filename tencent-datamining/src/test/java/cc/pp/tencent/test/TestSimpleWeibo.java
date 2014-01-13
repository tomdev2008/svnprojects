package cc.pp.tencent.test;

import java.util.HashMap;

import cc.pp.tencent.analysis.single.weibo.SimpleWeiboAnalysis;
import cc.pp.tencent.jdbc.WeiboJDBC;
import cc.pp.tencent.utils.GetWeiboLists;

public class TestSimpleWeibo {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String ip = "192.168.1.27";
		/*********************循环分析微博并存储***********************/
		SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis(ip);
		GetWeiboLists gw = new GetWeiboLists(ip);
		String uid, url, info;
		int apicount;
		WeiboJDBC weibojdbc = new WeiboJDBC(ip);
		if (weibojdbc.mysqlStatus()) {
			HashMap<Integer, String> wblists = weibojdbc.getWbListsByDB("tencentweibolists");
			for (int i = 0; i < wblists.size(); i++) {
				info = wblists.get(i);
				uid = info.substring(0, info.indexOf("="));
				url = info.substring(info.indexOf("=") + 1);
				// 分析微博并插入结果数据
				apicount = swa.analysis(weibojdbc, url);
				if (apicount == -1) {
					// 插入问题微博
					weibojdbc.insertIssueWbs("tencent", uid, url);
				}
				// 更新线上微博状态
				gw.getAddWeibos(gw.getUpdateUrl(uid, "tencent", url));
			}
			weibojdbc.truncateTables("tencentweibolists");
			weibojdbc.sqlClose();
		}
	}

}
