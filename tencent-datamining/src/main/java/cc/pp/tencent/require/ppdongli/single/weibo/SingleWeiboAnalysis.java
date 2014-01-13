package cc.pp.tencent.require.ppdongli.single.weibo;

import java.sql.SQLException;

import net.sf.json.JSONArray;
import cc.pp.tencent.analysis.common.Analysis;
import cc.pp.tencent.jdbc.WeiboJDBC;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

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
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		SingleWeiboAnalysis bwa = new SingleWeiboAnalysis("192.168.1.27");
		String url = new String("http://t.qq.com/p/t/307054065146563");
		String wid = WeiboUtils.getWid(url);
		String[] resultString = bwa.analysis(wid);
		System.out.println(resultString[0]);
		System.out.println(resultString[1]);
	}

	/**
	 * 分析函数
	 * @throws Exception 
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
				String[] token = WeiboUtils.getAccessToken(this.ip);
				//			String[] token = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
				OAuthV1 oauth = new OAuthV1();
				weiboUtils.oauthInit(oauth, token[0], token[1]);
				TAPI tapi = new TAPI(oauth.getOauthVersion());
				String wbinfo = tapi.show(oauth, "json", wid);
				apicount++;
				/*************获取微博信息****************/
				String username = null;
				try {
					username = weiboUtils.oriWbArrange(result, wbinfo);
				} catch (RuntimeException e) {
					tworesult[0] = "20003";
					tworesult[1] = "1";
					return tworesult;
				}
				/*************获取用户信息****************/
				UserAPI user = new UserAPI(oauth.getOauthVersion());
				String userinfo = user.otherInfo(oauth, "json", username, "");
				weiboUtils.oriUserArrange(result, userinfo);
				/***************数据转换与存储**************/
				jsonresult = JSONArray.fromObject(result);

				weibomysql.sqlClose();
			}
			if (jsonresult.toString() == null) {
				tworesult[0] = "20003";
			} else {
				tworesult[0] = jsonresult.toString();
			}
			tworesult[1] = Integer.toString(apicount);

			return tworesult;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
