package cc.pp.tencent.analysis.userweibo;

import java.io.EOFException;
import java.sql.SQLException;
import java.util.List;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.tencent.jdbc.UserWeiboJDBC;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.UserTimeline;
import com.tencent.weibo.dao.impl.UserTimelineInfo;
import com.tencent.weibo.oauthv1.OAuthV1;

public class GetUserWeibos1 {

	private static Logger logger = LoggerFactory.getLogger(GetUserWeibos.class);

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		GetUserWeibos1 getUserWeibos = new GetUserWeibos1();
		String tokens = "2c656d405a1d4587b7b99d143102d002,b36e96d43ed75485258f0fdf0cefe143";
		String uid = "lsk13092899857"; // lsk13092899857、cxpolice
		UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			getUserWeibos.dumpWeibosToDB(myJdbc, uid, tokens);
			myJdbc.sqlClose();
		}

	}

	public void dumpUserWeibos() throws Exception {

		//
	}

	public void disSinaUsers() throws SQLException {

		//
	}

	/**
	 * 采集用户的历史微博信息
	 * 
	 * @param uid
	 * @param accesstoken
	 * @return
	 * @throws Exception
	 * @throws WeiboException
	 */
	public int dumpWeibosToDB(UserWeiboJDBC myJdbc, String uid, String tokens)
			throws Exception {

		OAuthV1 oauth = new OAuthV1();
		String accesstoken = tokens.substring(0, tokens.indexOf(","));
		String tokensecret = tokens.substring(tokens.indexOf(",") + 1);
		OauthInit.oauthInit(oauth, accesstoken, tokensecret);
		StatusesAPI sa = new StatusesAPI(oauth.getOauthVersion());
		ObjectMapper mapper = new ObjectMapper();

		try {
			String statusesinfo = sa.userTimeline(oauth, "json", "0", "0",
					"70", "0", uid, "", "0", "0");
			UserTimeline userTimeline = mapper.readValue(statusesinfo,
					UserTimeline.class);
			int pagesum = userTimeline.getData().getTotalnum() / 70;
			int page = 0;
			long lastpagetime;
			boolean isoriginal;
			String lastid;
			List<UserTimelineInfo> infos;
			while (page++ <= pagesum) {

				System.out.println(page);

				infos = userTimeline.getData().getInfo();
				lastpagetime = infos.get(infos.size() - 1).getTimestamp();
				lastid = infos.get(infos.size() - 1).getId();
				for (UserTimelineInfo info : infos) {
					if (info.getSource() == null) {
						isoriginal = true;
					} else {
						isoriginal = false;
					}
					myJdbc.insertUserWeibosInfo(
							"tencentuserweiboinfo",
							info.getName(),
							info.getNick(), //
							info.getOpenid(),
							info.getId(),
							info.getText(),
							info.getOrigtext(), //
							info.getCount(),
							info.getMcount(),
							info.getFrom(),
							info.getFromurl(), //
							info.getProvince_code(),
							info.getCity_code(),
							info.getEmotiontype(), //
							info.getGeo(), info.getIsrealname(),
							info.getIsvip(),
							info.getLocation(), //
							info.getJing(), info.getSelf(), isoriginal,
							info.getStatus(), //
							info.getType(), info.getWei(), info.getTimestamp());
				}
				if (page > pagesum) {
					break;
				}
				statusesinfo = sa.userTimeline(oauth, "json", "0",
						Long.toString(lastpagetime), //
						"70", lastid, uid, "", "0", "0");
			}

			return 0;
		} catch (UnrecognizedPropertyException e) {
			logger.info("UnrecognizedPropertyException: " + e);
			return -1;
		} catch (JsonMappingException e) {
			logger.info("JsonMappingException: " + e);
			return -1;
		} catch (EOFException e) {
			logger.info("EOFException: " + e);
			return -1;
		}
	}

}

