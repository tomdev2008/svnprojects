package com.tencent.examples.user;

import java.io.EOFException;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.OtherInfo;
import com.tencent.weibo.oauthv1.OAuthV1;

public class GetOtherInfo {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		OAuthV1 oauth = new OAuthV1();
		String[] tokens = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
		OauthInit.oauthInit(oauth, tokens[0], tokens[1]);
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String uid = "cxpolice";
		ObjectMapper mapper = new ObjectMapper();
		String userinfo = user.otherInfo(oauth, "json", uid, "");
		try {
			OtherInfo otherinfo = mapper.readValue(userinfo, OtherInfo.class);
			System.out.println(otherinfo.getData().getName());
		} catch (UnrecognizedPropertyException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		}

	}

}
