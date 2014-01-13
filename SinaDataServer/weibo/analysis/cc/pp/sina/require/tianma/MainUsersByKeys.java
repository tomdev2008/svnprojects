package cc.pp.sina.require.tianma;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.analysis.topic.TopicUtils;
import cc.pp.sina.common.IdToMid;
import cc.pp.sina.jdbc.LocalJDBC;
import cc.pp.sina.utils.URLCode;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.json.JSONException;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class MainUsersByKeys {

	private final List<List<String>> urls = new ArrayList<List<String>>();
	private final List<String> cates = new ArrayList<String>();
	private final ReaderClassAndKeys reader;
	private int PAGE_COUNT = 0;

	public MainUsersByKeys(String filepath, int pagecount) throws IOException {
		reader = new ReaderClassAndKeys(filepath);
		for (List<String> cate : reader.getClassandkeys()) {
			List<String> keytourl = new ArrayList<String>();
			for (int i = 1; i < cate.size(); i++) {
				keytourl.add(getCodedUrl(cate.get(i)));
			}
			cates.add(cate.get(0));
			urls.add(keytourl);
		}
		PAGE_COUNT = pagecount;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public static void main(String[] args) throws IOException, WeiboException, SQLException, JSONException {

		MainUsersByKeys mainuser = new MainUsersByKeys("testdata/classandkeys.txt", 3);
		int count = mainuser.getCatesSize();
		System.out.println(count);
		mainuser.dumpUserinfoToDb(0);

	}

	/**
	 * 获取相关微博信息，并存入数据库
	 */
	public void dumpUserinfoToDb(int index) throws HttpException, WeiboException, //
			SQLException, IOException, JSONException {

		String cate = getCates().get(index);
		String tablename = "wbusersinfo_" + cate;
		LocalJDBC jdbc = new LocalJDBC("127.0.0.1", "root", "root");
		if (jdbc.mysqlStatus()) {
			//			jdbc.createWbUsersInfoTable(tablename);
		}

		List<String> urls = getUrls().get(index);
		System.out.println(cate + " : ");
		for (String url : urls) {
			getAnalysisResult(url, tablename);
		}

	}

	/**
	 * @获取分析结果
	 */
	public void getAnalysisResult(String originalurl, String tablename) throws WeiboException, SQLException,
			HttpException,
			IOException, JSONException {

		String[] wids = TopicUtils.getWids(originalurl, PAGE_COUNT);
		System.out.println("originalurl: " + wids.length);
		if (wids != null) {
			this.getAllReposterResult(wids, tablename);
		}
	}

	/**
	 * 根据关键词返回编码后的url
	 * @param keywords
	 * @return
	 */
	public String getCodedUrl(String keywords) {

		String encodedKeyword = URLCode.Utf8URLencode(keywords);
		//		String url = "http://s.weibo.com/weibo/" + encodedKeyword + "?topnav=1&wvr=5&topsug=1";
		String url = "http://s.weibo.com/weibo/" + encodedKeyword + "&Refer=index";
		return url;
	}

	/**
	 * @分析所有微博
	 * @param wids
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public void getAllReposterResult(String[] wids, String tablename) throws WeiboException, SQLException,
			JSONException {

		/********************提取accesstoken***********************/
		IdToMid idtomid = new IdToMid();
		LocalJDBC jdbc = new LocalJDBC("127.0.0.1", "root", "root");
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
					jdbc.insertWbUsersInfo(tablename, wbinfo.getUser().getId(), wbinfo.getUser().getName(), wbinfo
							.getUser().getVerifiedReason(), wburl, wbinfo.getUser().getGender(), wbinfo.getUser()
							.getFollowersCount(), wbinfo.getUser().getStatusesCount(), wbinfo.getUser().getProvince(),
							wbinfo.getRepostsCount(), wbinfo.getCommentsCount(), isoriginal, addv, wbinfo.getUser()
									.getVerifiedType(), verify, createtime);
				} catch (SQLException e) {
					jdbc.insertWbUsersInfo(tablename, wbinfo.getUser().getId(), wbinfo.getUser().getName(), "", wburl,
							wbinfo.getUser().getGender(), wbinfo.getUser().getFollowersCount(), wbinfo.getUser()
									.getStatusesCount(), wbinfo.getUser().getProvince(), wbinfo.getRepostsCount(),
							wbinfo.getCommentsCount(), isoriginal, addv, wbinfo.getUser().getVerifiedType(), verify,
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
				} catch (WeiboException e) {
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
							jdbc.insertWbUsersInfo(tablename, reposter.getUser().getId(), reposter.getUser().getName(),
									reposter
									.getUser().getVerifiedReason(), repwburl, reposter.getUser().getGender(), reposter
									.getUser().getFollowersCount(), reposter.getUser().getStatusesCount(), reposter
									.getUser().getProvince(), reposter.getRepostsCount(), reposter.getCommentsCount(),
									"转发微博", raddv, reposter.getUser().getVerifiedType(), rverify, createtime);
						} catch (SQLException e) {
							jdbc.insertWbUsersInfo(tablename, reposter.getUser().getId(), reposter.getUser().getName(),
									"",
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

	public List<List<String>> getUrls() {
		return urls;
	}

	public List<String> getCates() {
		return cates;
	}

	public int getCatesSize() {
		return cates.size();
	}

}
