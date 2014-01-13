package cc.pp.sina.analysis.userclassfy;

import java.io.Serializable;
import java.util.HashMap;

import cc.pp.sina.analysis.userclassfy.dao.UserServalWeibosData;
import cc.pp.sina.analysis.userclassfy.dao.UserSingleWeiboData;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

/**
 * 多条微博分析
 * 调用接口次数：10*2 = 20
 * @author Administrator
 *
 */
@SuppressWarnings("static-access")
public class UserServalWeibosParams implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		UserServalWeibosParams uswp = new UserServalWeibosParams();
		String accesstoken = "2.00vyKmPBdcZIJCab7e4cdb505dUSLC";
		HashMap<Integer,String> lastweiboids = new HashMap<Integer,String>();
		lastweiboids.put(0, "3596657608114610");
		lastweiboids.put(1, "3558788248346126");
		lastweiboids.put(2, "3558646426202996");
		lastweiboids.put(3, "3517828470825211");
		UserServalWeibosData result = uswp.getServalAnalysis(lastweiboids, accesstoken);
		System.out.println(result.getAvereposterquality());
		System.out.println(result.getAveexposionsum());
	}

	/**
	 * 多条微博分析
	 * @param lastweiboids
	 * @param accesstoken
	 * @return
	 */
	public UserServalWeibosData getServalAnalysis(HashMap<Integer, String> lastweiboids, String accesstoken) {

		UserServalWeibosData result = new UserServalWeibosData();
		int[] fansquality = new int[2];
		long allexposion = 0;
		for (int i = 0; i < lastweiboids.size(); i++) {
			UserSingleWeiboData temp = this.analysis(lastweiboids.get(i), accesstoken);
			if (temp != null) {
				fansquality[0] += temp.getReposterquality()[0];
				fansquality[1] += temp.getReposterquality()[1];
				allexposion += temp.getExposionsum();
			}
		}
		if (fansquality[0] + fansquality[1] == 0) {
			result.setAvereposterquality(0.0f);
		} else {
			result.setAvereposterquality((float) fansquality[0] / (fansquality[0] + fansquality[1]));
		}
		result.setAveexposionsum(allexposion);

		return result;
	}

	/**
	 * 单条微博分析函数
	 */
	public UserSingleWeiboData analysis(String wid, String accesstoken) {

		try {
			Timeline tm = new Timeline();
			tm.client.setToken(accesstoken);
			/*************变量初始化****************/
			int[] reposterquality = new int[2]; // 2、转发用户质量（水军比例）
			long exposionsum = 0; // 3、总曝光量
			int existwb = 0, wbsum = 0, fanssum, weibosum, cursor = 1;
			boolean addv;
			/***************************************采集转发信息***********************************************/
			Paging page = new Paging(cursor, 200);
			StatusWapper status = null;
			try {
				status = tm.getRepostTimeline(wid, page);
			} catch (RuntimeException e) {
				return null;
			} catch (WeiboException e) {
				return null;
			}
			wbsum = (int) status.getTotalNumber();
			if (wbsum == 0) {
				return null;
			}
			/************开始循环计算***************/
			while (cursor * 200 < wbsum + 200)
			{
				for (Status reposter : status.getStatuses()) 
				{
					if (reposter.getId() == null) {
						continue;
					} else {
						existwb++;
					}
					fanssum = reposter.getUser().getFollowersCount();
					weibosum = reposter.getUser().getStatusesCount();
					addv = reposter.getUser().isVerified();
					/***************11、水军比例****************/
					reposterquality[UCUtils.checkQuality(addv, fanssum, weibosum)]++;
					/***************12、总曝光量**************/
					exposionsum += fanssum;
				}
				if (cursor >= 2) { // 限制两次
					break;
				}
				try {
					cursor++;
					page = new Paging(cursor, 200);
					status = tm.getRepostTimeline(wid, page);
				} catch (RuntimeException e) {
					cursor++;
				}
			}
			/**********************整理数据结果************************/
			/***************11、水军比例****************/
			/***************12、总曝光量***************/
			if (existwb != 0) {
				exposionsum = exposionsum * wbsum / existwb;
			}
			/***************数据转换与存储**************/
			UserSingleWeiboData result = new UserSingleWeiboData();
			result.setReposterquality(reposterquality);
			result.setExposionsum(exposionsum);

			return result;

		} catch (WeiboException e) {
			e.printStackTrace();
			return null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}
	}
}
