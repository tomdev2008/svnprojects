package cc.pp.tencent.analysis.username.threads;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.FansInfo;
import com.tencent.weibo.dao.impl.OtherInfo;
import com.tencent.weibo.dao.impl.UserFansList;
import com.tencent.weibo.dao.impl.UserIdolList;
import com.tencent.weibo.dao.impl.UserIdolListInfo;
import com.tencent.weibo.oauthv1.OAuthV1;

public class UsernameDaoImpl implements UsernameDao {

	private static final Logger logger = LoggerFactory.getLogger(UsernameDaoImpl.class);

	private final List<String> tokens;
	private final Random random = new Random();
	private static int apicount = 0;

	OAuthV1 oauth = new OAuthV1();
	ObjectMapper mapper = new ObjectMapper();

	public UsernameDaoImpl(List<String> tokens) {
		this.tokens = tokens;
	}

	/**
	 * 获取用户基础信息
	 */
	public OtherInfo getUserBaseInfo(String uid) {

		String token = getRandomToken();
		OauthInit.oauthInit(oauth, token.substring(0, token.indexOf(",")), token.substring(token.indexOf(",") + 1));
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		ObjectMapper mapper = new ObjectMapper();
		try {
			String userinfo = user.otherInfo(oauth, "json", uid, "");
			apicount++;
			OtherInfo otherinfo = mapper.readValue(userinfo, OtherInfo.class);
			return otherinfo;
		} catch (UnrecognizedPropertyException e) {
			return null;
		} catch (JsonMappingException e) {
			return null;
		} catch (EOFException e) {
			return null;
		} catch (RuntimeException e) {
			return null;
		} catch (JsonParseException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (Exception e) {
			logger.info("Error uid: " + uid);
			return null;
		}
	}

	/**
	 * 获取用户的粉丝信息
	 *    ***最多拉取10000个粉丝信息***
	 */
	public List<String> getUserFansInfo(String uid, int fansnum) {

		String token = getRandomToken();
		OauthInit.oauthInit(oauth, token.substring(0, token.indexOf(",")), token.substring(token.indexOf(",") + 1));
		FriendsAPI fa = new FriendsAPI(oauth.getOauthVersion());
		try {
			List<String> uids = new ArrayList<String>();
			String fansinfo;
			UserFansList userFansList;
			int pagecount = (fansnum / 30 + 1 > 300) ? 300 : fansnum / 30 + 1;
			for (int page = 1; page < pagecount; page++) {
				fansinfo = fa.userFanslist(oauth, "json", "30", Integer.toString((page - 1) * 30), uid, "", "1", "0");
				apicount++;
				userFansList = mapper.readValue(fansinfo, UserFansList.class);
				List<FansInfo> fansinfos = userFansList.getData().getInfo();
				for (FansInfo info : fansinfos) {
					uids.add(info.getName());
				}
			}
			return uids;
		} catch (UnrecognizedPropertyException e) {
			return null;
		} catch (JsonMappingException e) {
			return null;
		} catch (EOFException e) {
			return null;
		} catch (RuntimeException e) {
			return null;
		} catch (JsonParseException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取用户的关注信息
	 *    ***最多拉取10000个关注信息***
	 */
	public List<String> getUserFriendsInfo(String uid, int idolnum) {

		String token = getRandomToken();
		OauthInit.oauthInit(oauth, token.substring(0, token.indexOf(",")), token.substring(token.indexOf(",") + 1));
		FriendsAPI fa = new FriendsAPI(oauth.getOauthVersion());
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<String> uids = new ArrayList<String>();
			String friendsinfo;
			UserIdolList userIdolList;
			int pagecount = (idolnum / 30 + 1 > 300) ? 300 : idolnum / 30 + 1;
			for (int page = 1; page < pagecount; page++) {
				friendsinfo = fa.userIdollist(oauth, "json", "30", //
						Integer.toString((page - 1) * 30), uid, "", "0", "1");
				apicount++;
				userIdolList = mapper.readValue(friendsinfo, UserIdolList.class);
				List<UserIdolListInfo> idolinfos = userIdolList.getData().getInfo();
				for (UserIdolListInfo info : idolinfos) {
					uids.add(info.getName());
				}
			}
			return uids;
		} catch (UnrecognizedPropertyException e) {
			return null;
		} catch (JsonMappingException e) {
			return null;
		} catch (EOFException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 随机获取token
	 * @return
	 */
	public String getRandomToken() {
		return tokens.get(random.nextInt(tokens.size()));
	}

	public int getApicount() {
		return apicount;
	}

}
