package cc.pp.tencent.analysis.userweibo;

import java.io.EOFException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.tencent.jdbc.UserWeiboJDBC;
import cc.pp.tencent.utils.Nettool;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.UserTimeline;
import com.tencent.weibo.dao.impl.UserTimelineInfo;
import com.tencent.weibo.oauthv1.OAuthV1;

public class GetUserWeibos implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(GetUserWeibos.class);
	private String tencentuser;
	private String tokens;

	private static AtomicInteger count = new AtomicInteger(0);

	public void setUid(String tencentuser) {
		this.tencentuser = tencentuser;
	}

	public void setTokens(String tokens) {
		this.tokens = tokens;
	}

	public GetUserWeibos(String tokens, String tencentuser) {
		this.tokens = tokens;
		this.tencentuser = tencentuser;
	}

	public void run() {
		try {
			//			UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");
			UserWeiboJDBC myJdbc = new UserWeiboJDBC(Nettool.getServerLocalIp());
			System.out.println(count.addAndGet(1));
			if (myJdbc.mysqlStatus()) {
				dumpWeibosToDB(myJdbc);
				myJdbc.sqlClose();
			}
		} catch (Exception e) {
			logger.info("Exception: " + e + ", uid: " + tencentuser);
			throw new RuntimeException("uid: " + tencentuser, e);
		}
	}

	public void disSinaUsers() throws SQLException {

		//
	}

	/**
	 * 采集用户的历史微博信息
	 * @param uid
	 * @param accesstoken
	 * @return
	 * @throws Exception 
	 * @throws WeiboException
	 */
	public int dumpWeibosToDB(UserWeiboJDBC myJdbc) throws Exception {

		OAuthV1 oauth = new OAuthV1();
		String accesstoken = tokens.substring(0, tokens.indexOf(","));
		String tokensecret = tokens.substring(tokens.indexOf(",") + 1);
		OauthInit.oauthInit(oauth, accesstoken, tokensecret);
		StatusesAPI sa = new StatusesAPI(oauth.getOauthVersion());
		ObjectMapper mapper = new ObjectMapper();
		try {
			String statusesinfo = sa.userTimeline(oauth, "json", "0", "0",
					"70", "0", tencentuser, "", "0", "0");
			UserTimeline userTimeline = mapper.readValue(statusesinfo, UserTimeline.class);
			int pagesum = userTimeline.getData().getTotalnum() / 70;
			int page = 0;
			long lastpagetime;
			boolean isoriginal;
			String lastid;
			List<UserTimelineInfo> infos;
			while (page++ <= pagesum) {
				infos = userTimeline.getData().getInfo();
				lastpagetime = infos.get(infos.size() - 1).getTimestamp();
				lastid = infos.get(infos.size() - 1).getId();
				for (UserTimelineInfo info : infos) {
					if (info.getSource() == null) {
						isoriginal = true;
					} else {
						isoriginal = false;
					}
					try {
						myJdbc.insertUserWeibosInfo("tencentuserweiboinfo", info.getName(), info.getNick(), //
								info.getOpenid(), info.getId(), info.getText(), info.getOrigtext(), //
								info.getCount(), info.getMcount(), info.getFrom(), info.getFromurl(), //
								info.getProvince_code(), info.getCity_code(), info.getEmotiontype(), //
								info.getGeo(), info.getIsrealname(), info.getIsvip(), info.getLocation(), //
								info.getJing(), info.getSelf(), isoriginal, info.getStatus(), //
								info.getType(), info.getWei(), info.getTimestamp());
					} catch (SQLException e) {
						continue;
					}
				}
				if (page > pagesum) {
					break;
				}
				/**
				 * 采集所有微博的时候会出现采集中断，所以部分用户微博获取不全
				 */
				statusesinfo = sa.userTimeline(oauth, "json", "0", Long.toString(lastpagetime), //
						"70", lastid, tencentuser, "", "0", "0");
			}

			return 0;
		} catch (UnrecognizedPropertyException e) {
			myJdbc.insertUnusedUid("tencentunuseduids", tencentuser);
			return -1;
		} catch (JsonMappingException e) {
			myJdbc.insertUnusedUid("tencentunuseduids", tencentuser);
			return -1;
		} catch (EOFException e) {
			myJdbc.insertUnusedUid("tencentunuseduids", tencentuser);
			return -1;
		} catch (RuntimeException e) {
			myJdbc.insertUnusedUid("tencentunuseduids", tencentuser);
			return -1;
		}
	}

}
