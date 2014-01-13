package cc.pp.sina.analysis.topic;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.common.IdToMid;
import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

/**
 * Title: 话题分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TopicAnalysisLocal implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws HttpException, WeiboException, SQLException, IOException {
		// 
		TopicAnalysisLocal wwta = new TopicAnalysisLocal();
		String url = new String(
				"http://s.weibo.com/weibo/%25E5%25B0%258F%25E6%2597%25B6%25E4%25BB%25A3?topnav=1&wvr=5&topsug=1");

		wwta.getAnalysisResult(url, 5); // url+页码
	}

	/**
	 * @获取分析结果
	 * @param originalurl
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 * @throws HttpException
	 * @throws IOException
	 */
	public void getAnalysisResult(String originalurl, int pages) throws WeiboException, SQLException,
			HttpException, IOException {

		String[] wids = TopicUtils.getWids(originalurl, pages);
		System.out.println(wids.length);
		if (wids != null) {
			this.getAllReposterResult(wids);
		}

	}

	/**
	 * @分析所有微博
	 * @param wids
	 * @return
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public void getAllReposterResult(String[] wids) throws WeiboException, SQLException {

		/********************提取accesstoken***********************/
		IdToMid idtomid = new IdToMid();
		LocalJDBC jdbc = new LocalJDBC("127.0.0.1", "root", "");
		Timeline tm = new Timeline();

		if (jdbc.mysqlStatus()) {

			String[] tokens = jdbc.getAccessToken(wids.length);

			for (int nums = 0; nums < tokens.length; nums++) {

				System.out.println("Users: " + wids[nums]);

				tm.client.setToken(tokens[nums]);
				/******************单条微博信息*******************/
				Status wbinfo = tm.showStatus(wids[nums]);
				if (wbinfo.getUser() == null) {
					continue;
				}
				String text = wbinfo.getText();
				String isoriginal = "直发微博";
				if (text.contains("//@") || text.contains("转发微博")) {
					isoriginal = "转发微博";
				}
				String wburl = "http://www.weibo.com/" + wbinfo.getUser().getId() + "/"
						+ idtomid.id2mid(wbinfo.getId());
				String verify = Integer.toString(wbinfo.getUser().getVerifiedType());
				if (wbinfo.getUser().getVerifiedType() == 2 || wbinfo.getUser().getVerifiedType() == 5
						|| wbinfo.getUser().getVerifiedType() == 7) {
					verify = "蓝V";
				}
				if (wbinfo.getUser().getVerifiedType() == 10 || wbinfo.getUser().getVerifiedType() == 200
						|| wbinfo.getUser().getVerifiedType() == 220) {
					verify = "黄V";
				}
				int addv = 0;
				if (wbinfo.getUser().isVerified() == true) {
					addv = 1;
				}
				String createtime = "";
				long time = wbinfo.getCreatedAt().getTime() / 1000;
				SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
				createtime = fomat.format(new Date(time * 1000l));

				try {
					jdbc.insertWbUsersInfo(wbinfo.getUser().getId(), wbinfo.getUser().getName(), wbinfo.getUser()
							.getVerifiedReason(), wburl, wbinfo.getUser().getGender(), wbinfo.getUser()
							.getFollowersCount(), wbinfo.getUser().getStatusesCount(), wbinfo.getUser().getProvince(),
							wbinfo.getRepostsCount(), wbinfo.getCommentsCount(), isoriginal, addv, wbinfo.getUser()
									.getVerifiedType(), verify, createtime);
				} catch (SQLException e) {
					jdbc.insertWbUsersInfo(wbinfo.getUser().getId(), wbinfo.getUser().getName(), "", wburl, wbinfo
							.getUser().getGender(), wbinfo.getUser().getFollowersCount(), wbinfo.getUser()
							.getStatusesCount(), wbinfo.getUser().getProvince(), wbinfo.getRepostsCount(), wbinfo
							.getCommentsCount(), isoriginal, addv, wbinfo.getUser().getVerifiedType(), verify,
							createtime);
				}

				if (wbinfo.getRepostsCount() == 0) {
					continue;
				}
				/******************微博转发信息*******************/
				int cursor = 1;
				Paging page = new Paging(cursor, 200);
				StatusWapper status = null;
				try {
					status = tm.getRepostTimeline(wids[nums], page);
				} catch (RuntimeException e) {
					continue;
				}
				long wbsum = status.getTotalNumber();

				/************开始循环计算***************/
				while ((cursor * 200 < wbsum + 200) && (cursor < 11)) {
					for (Status reposter : status.getStatuses()) {
						if (reposter.getId() == null) {
							continue;
						}
						String repwburl = "http://www.weibo.com/" + reposter.getUser().getId() + "/"
								+ idtomid.id2mid(reposter.getId());
						int raddv = 0;
						if (reposter.getUser().isVerified() == true) {
							raddv = 1;
						}
						String rverify = Integer.toString(reposter.getUser().getVerifiedType());
						if (reposter.getUser().getVerifiedType() == 2 || reposter.getUser().getVerifiedType() == 5
								|| reposter.getUser().getVerifiedType() == 7) {
							rverify = "蓝V";
						}
						if (reposter.getUser().getVerifiedType() == 10 || reposter.getUser().getVerifiedType() == 200
								|| reposter.getUser().getVerifiedType() == 220) {
							rverify = "黄V";
						}
						time = reposter.getCreatedAt().getTime() / 1000;
						fomat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
						createtime = fomat.format(new Date(time * 1000l));
						try {
							jdbc.insertWbUsersInfo(reposter.getUser().getId(), reposter.getUser().getName(), reposter
									.getUser().getVerifiedReason(), repwburl, reposter.getUser().getGender(),
									reposter.getUser().getFollowersCount(), reposter.getUser().getStatusesCount(),
									reposter.getUser().getProvince(), reposter.getRepostsCount(), reposter
											.getCommentsCount(), "转发微博", raddv, reposter.getUser().getVerifiedType(),
									rverify, createtime);
						} catch (SQLException e) {
							jdbc.insertWbUsersInfo(reposter.getUser().getId(), reposter.getUser().getName(), "",
									repwburl, reposter.getUser().getGender(), reposter.getUser().getFollowersCount(),
									reposter.getUser().getStatusesCount(), reposter.getUser().getProvince(), reposter
											.getRepostsCount(), reposter.getCommentsCount(), "转发微博", raddv, reposter
											.getUser().getVerifiedType(), rverify, createtime);
						}

					}
					cursor++;
					page = new Paging(cursor, 200);
					try {
						status = tm.getRepostTimeline(wids[nums], page);
					} catch (WeiboException e) {
						break;
					}
				}
			}
			jdbc.sqlClose();
		}
	}

}

