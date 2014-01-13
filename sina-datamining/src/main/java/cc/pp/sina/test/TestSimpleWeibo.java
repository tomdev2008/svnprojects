package cc.pp.sina.test;

import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.URIException;

import cc.pp.sina.analysis.single.weibo.SimpleWeiboAnalysis;
import cc.pp.sina.common.MidToId;
import cc.pp.sina.jdbc.WeiboJDBC;
import cc.pp.sina.result.SimpleWeiboResult;
import cc.pp.sina.utils.GetWeiboLists;

import com.sina.weibo.model.WeiboException;

public class TestSimpleWeibo {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 * @throws URIException 
	 */
	public static void main(String[] args) throws SQLException, URIException, WeiboException {

		/*********************获取待分析微博列表***********************/
		String ip = "192.168.1.27";
		GetWeiboLists getwb = new GetWeiboLists(ip);
		getwb.getWbLists();
		/*********************循环分析微博并存储***********************/
		SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis(ip);
		GetWeiboLists gw = new GetWeiboLists(ip);
		String uid, url, info;
		WeiboJDBC weibojdbc = new WeiboJDBC(ip);
		if (weibojdbc.mysqlStatus()) {
			HashMap<Integer, String> wblists = weibojdbc.getWbListsByDB("sinaweibolists");
			for (int i = 0; i < wblists.size(); i++) {
				info = wblists.get(i);
				uid = info.substring(0, info.indexOf("="));
				url = info.substring(info.indexOf("=") + 1);
				// 更新线上微博状态
				gw.getAddWeibos(gw.getUpdateUrl(uid, "sina", url));
				// 分析微博并插入结果数据
				int apicount = swa.analysis(weibojdbc, url);
				if (apicount == -1) {
					// 插入问题微博
					weibojdbc.insertIssueWbs("sina", uid, url);
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
			weibojdbc.truncateTables("sinaweibolists");
			weibojdbc.sqlClose();
		}
	}

}
