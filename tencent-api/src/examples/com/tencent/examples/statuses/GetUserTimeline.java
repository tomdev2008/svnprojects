package com.tencent.examples.statuses;

import java.io.EOFException;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.UserTimeline;
import com.tencent.weibo.oauthv1.OAuthV1;

public class GetUserTimeline {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		OAuthV1 oauth = new OAuthV1();
		String[] tokens = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
		OauthInit.oauthInit(oauth, tokens[0], tokens[1]);
		StatusesAPI sa = new StatusesAPI(oauth.getOauthVersion());
		String uid = "cxpolice";
		ObjectMapper mapper = new ObjectMapper();
		String statusesinfo = sa.userTimeline(oauth, "json", "0", "0", "70", "0", uid, "", "0", "0");
		//		System.out.println(statusesinfo);
		try {
			UserTimeline userTimeline = mapper.readValue(statusesinfo, UserTimeline.class);
			System.out.println(userTimeline.getData().getTotalnum());
		} catch (UnrecognizedPropertyException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		}

	}

}
