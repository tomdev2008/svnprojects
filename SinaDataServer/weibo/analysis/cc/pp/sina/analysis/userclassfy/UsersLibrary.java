package cc.pp.sina.analysis.userclassfy;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;
import cc.pp.sina.analysis.userclassfy.dao.UserAllWeibosData;
import cc.pp.sina.analysis.userclassfy.dao.UserAndFansData;
import cc.pp.sina.analysis.userclassfy.dao.UserServalWeibosData;
import cc.pp.sina.jdbc.UserClassfyJDBC;

import com.sina.weibo.model.WeiboException;

/**
 * 微博主参数分析
 * 接口调用次数：<= 41
 * @author Administrator
 *
 */
@SuppressWarnings("static-access")
public class UsersLibrary implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		UsersLibrary ui = new UsersLibrary();
		UserClassfyJDBC ucjdbc = new UserClassfyJDBC("127.0.0.1", "root", "root");
		if (ucjdbc.mysqlStatus()) {
			int i = 0;
			while (i++ < 100) {
				System.out.println(i);
				HashMap<Integer, String> username = ucjdbc.getUsernames("sinausers", 1);
				ucjdbc.deleteUsernames("sinausers", username.get(0));
				try {
					ui.userAllParams(ucjdbc, "sinalibrary", username.get(0));
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				} catch (WeiboException e) {
					e.printStackTrace();
					continue;
				}
			}
			ucjdbc.sqlClose();
		}
	}

	/**
	 * 用户所有参数分析
	 * @param tablename
	 * @param uid
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public void userAllParams(UserClassfyJDBC ucjdbc, String tablename, String uid) throws WeiboException, SQLException {

		/*******************获取有效的TOken**************************/
		String[] accesstoken = ucjdbc.getAccessToken(4);
		/*******************用户及其粉丝分析**************************/
		UserAndFansParams uafp = new UserAndFansParams();
		UserAndFansData userfans = uafp.analysis(uid, accesstoken[0]);
		if (userfans == null) {
			ucjdbc.insertUnusedUsers("unusedusers", uid);
			return;
		}
		//		System.out.println("ok1");
		/*******************用户标签信息分析**************************/
		UserTagsParams utp = new UserTagsParams();
		String usertags = utp.analysis(uid, accesstoken[1]);
		if (usertags == null) {
			usertags = "0";
		}
		//		System.out.println("ok2");
		/*******************用户所有微博分析**************************/
		UserAllWeibosParams uawp = new UserAllWeibosParams();
		UserAllWeibosData userweibos = uawp.analysis(uid, accesstoken[2]);
		if (userweibos == null) {
			ucjdbc.insertUnusedUsers("unusedusers", uid);
			return;
		}
		//		System.out.println("ok3");
		// 平均有效转评量(按周、按月)
		float validrepcombyweek = userweibos.getAverepcombyweek() * userfans.getActiveratio();
		float validrepcombymonth = userweibos.getAverepcombymonth() * userfans.getActiveratio();
		/*******************用户多条微博分析**************************/
		UserServalWeibosParams uswp = new UserServalWeibosParams();
		HashMap<Integer, String> rand20lastweiboids = this.randWeibos(userweibos.getLastweiboids());
		UserServalWeibosData weibosparams = uswp.getServalAnalysis(rand20lastweiboids, accesstoken[3]);
		//		System.out.println("ok4");
		/*******************插入博主参数数据**************************/
		JSONArray getTop5provinces = JSONArray.fromObject(userfans.getTop5provinces());
		JSONArray wbsource = JSONArray.fromObject(userweibos.getWbsource());

		ucjdbc.insertUsersParams(tablename, uid, userfans.getNickname(), userfans.getDescription(),
				userfans.getFanscount(), userfans.getWeibocount(), userfans.getAveragewbs(), userfans.getInfluence(),
				userfans.getActivation(), userfans.getActivecount(), userfans.getAddvratio(), userfans.getAddvratio(),
				userfans.getMaleratio(), userfans.getFansexistedratio(), userfans.getVerify(),
				userfans.getAllfanscount(), userfans.getAllactivefanscount(), getTop5provinces.toString(),
				userweibos.getOriratio(), userweibos.getAveorirepcom(), userweibos.getAverepcom(), wbsource.toString(),
				userweibos.getAverepcombyweek(), userweibos.getAverepcombymonth(),
				weibosparams.getAvereposterquality(), weibosparams.getAveexposionsum() + userfans.getFanscount(),
				validrepcombyweek, validrepcombymonth, usertags);

	}

	/**
	 * 从一堆微博中随机选出20条
	 * @param lastweiboids
	 * @return
	 */
	public HashMap<Integer, String> randWeibos(HashMap<Integer, String> lastweiboids) {

		if (lastweiboids.size() < 11) {
			return lastweiboids;
		} else {
			HashMap<Integer, String> result = new HashMap<Integer, String>();
			int index = 0;
			for (int i = 0; i < lastweiboids.size(); i++) {
				if (index < 10) {
					if (Math.random() < 0.5) {
						result.put(index++, lastweiboids.get(i));
					}
				} else {
					break;
				}
			}
			return result;
		}
	}
}
