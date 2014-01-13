package cc.pp.sina.require.ppdongli.single.weibo;

import java.sql.SQLException;

import net.sf.json.JSONArray;
import cc.pp.sina.analysis.common.Analysis;
import cc.pp.sina.common.MidToId;
import cc.pp.sina.jdbc.WeiboJDBC;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.WeiboException;

public class SingleWeiboAnalysis extends Analysis {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = -4980658229991475129L;

	public SingleWeiboAnalysis(String ip) {
		super(ip);
	}

	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {

		SingleWeiboAnalysis bwa = new SingleWeiboAnalysis("192.168.1.151");
		String url = new String("http://www.weibo.com/1862087393/zCdP36YGU");
		MidToId midtoid = new MidToId();
		String wid = midtoid.mid2id(url);
		String[] resultString = bwa.analysis(wid);
		System.out.println(resultString[0]);
		System.out.println(resultString[1]);
	}

	/**
	 * 分析函数
	 */
	@Override
	public String[] analysis(String wid) {

		try {
			int apicount = 0;
			/**************************************/
			String[] tworesult = new String[2];
			JSONArray jsonresult = null;
			WeiboUtils weiboUtils = new WeiboUtils();
			WeiboJDBC weibomysql = new WeiboJDBC(this.ip);
			if (weibomysql.mysqlStatus()) {

				PPDLData result = new PPDLData();
				/**********提取accesstoken*************/
				String accesstoken;
				accesstoken = weibomysql.getAccessToken();
				//			String accesstoken = "2.00FSwG5BdcZIJCe2bb561e86br_VZB";
				Timeline tm = new Timeline();
				tm.client.setToken(accesstoken);
				Status wbinfo = tm.showStatus(wid);
				apicount++;
				if (wbinfo.getUser() == null) { // 错误信息
					tworesult[0] = "20003";
					tworesult[1] = "0";
					return tworesult;
				}
				/*************获取用户和微博信息****************/
				weiboUtils.oriArrange(result, wbinfo);
				/***************数据转换与存储**************/
				jsonresult = JSONArray.fromObject(result);

				weibomysql.sqlClose();
			}
			if (jsonresult.toString() == null) { // 错误信息
				tworesult[0] = "20003";
			} else {
				tworesult[0] = jsonresult.toString();
			}
			tworesult[1] = Integer.toString(apicount);

			return tworesult;
		} catch (RuntimeException e) {
			e.printStackTrace();
			String[] tworesult = new String[2];
			tworesult[0] = "20003";
			tworesult[1] = "0";
			return tworesult;
		} catch (SQLException e) {
			e.printStackTrace();
			String[] tworesult = new String[2];
			tworesult[0] = "20003";
			tworesult[1] = "0";
			return tworesult;
		} catch (WeiboException e) {
			e.printStackTrace();
			String[] tworesult = new String[2];
			tworesult[0] = "20003";
			tworesult[1] = "0";
			return tworesult;
		}
	}

}
