package cc.pp.sina.analysis.userfans;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.pp.sina.common.SourceType;
import cc.pp.sina.jdbc.UserWeiboJDBC;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class SWeiboAnalysis implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private String ip = "";
	public int[] wbsource = new int[6];  // 粉丝使用的终端设备来源分布
	public int sum = 0;

	/**
	 * 构造函数
	 * @param ip
	 */
	public SWeiboAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException, RuntimeException {
		//
		SWeiboAnalysis swa = new SWeiboAnalysis("192.168.1.27");
		String[] uids = {"3128226573"};
		int[] result = swa.analysis(uids);
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public int[] analysis(String[] uids) throws WeiboException, SQLException {

		UserWeiboJDBC usermysql = new UserWeiboJDBC(this.ip);
		if (usermysql.mysqlStatus()) 
		{
			/********获取accesstoken和用户username*********/
			String accesstoken = usermysql.getAccessToken();
			/*****************变量初始化********************/
			int[] wbfenbubyhour = new int[24]; // 24小时内的微博发布分布		
			/****************采集用户微博信息****************/
			long createdat = 0;     // 微博创建时间
			SimpleDateFormat fo = null;
			String hour, source;

			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			 
			try {
				for (int i = 0; i < uids.length; i++) {
					if (uids[i] == null) {
						break;
					}
					StatusWapper status = tm.getUserTimelineByUid(uids[i], new Paging(1, 100), 0, 0);
					for (Status s : status.getStatuses())
					{			 
						createdat = s.getCreatedAt().getTime()/1000;
						fo = new SimpleDateFormat("HH");
						hour = fo.format(new Date(createdat*1000l));
						if (hour.substring(0,1).equals("0")) {
							hour = hour.substring(1);
						} 
						source = s.getSource().getName();
						/***************24小时内的微博发布分布*******************/
						wbfenbubyhour[Integer.parseInt(hour)]++; 
						/********************微博来源分布**********************/
						sum++;
						wbsource[SourceType.getCategory(source)]++;
					}
				}
			} catch (RuntimeException e) {
				//
			}

			usermysql.sqlClose();
			return wbfenbubyhour;
		} else {
			return null;
		}
	}
}
