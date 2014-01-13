package cc.pp.sina.test;

import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.httpclient.URIException;

import com.sina.weibo.model.WeiboException;

import cc.pp.sina.analysis.single.weibo.SimpleWeiboAnalysis;
import cc.pp.sina.jdbc.WeiboJDBC;
import cc.pp.sina.utils.GetWeiboLists;

public class DemoSimpleWeibo {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 * @throws URIException 
	 */
	public static void main(String[] args) throws URIException, WeiboException, SQLException {

		/*********************获取待分析微博列表***********************/
		String ip = "192.168.1.27";
		GetWeiboLists getwb = new GetWeiboLists(ip);
		getwb.getWbLists();
		/*********************循环分析微博并存储***********************/
		SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis(ip);
		GetWeiboLists gw = new GetWeiboLists(ip);
		String uid, url, info;
		int apicount;
		WeiboJDBC weibojdbc = new WeiboJDBC(ip);
		if (weibojdbc.mysqlStatus()) {
			HashMap<Integer,String> wblists = weibojdbc.getWbListsByDB("sinaweibolists");
			for (int i = 0; i < wblists.size(); i++) {
				info = wblists.get(i);
				uid = info.substring(0, info.indexOf("="));
				url = info.substring(info.indexOf("=") + 1);
				// 分析微博并插入结果数据
				apicount = swa.analysis(weibojdbc, url);  
				if (apicount == -1) {   
					// 插入问题微博
					weibojdbc.insertIssueWbs("sina", uid, url);
				}
				// 更新线上微博状态
				gw.getAddWeibos(gw.getUpdateUrl(uid, "sina", url));
			}
			weibojdbc.sqlClose();
		}
	}

}
