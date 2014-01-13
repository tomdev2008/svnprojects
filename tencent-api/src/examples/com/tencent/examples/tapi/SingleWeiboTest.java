package com.tencent.examples.tapi;

import java.io.EOFException;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.Show;
import com.tencent.weibo.oauthv1.OAuthV1;

public class SingleWeiboTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String[] token = { "2c656d405a1d4587b7b99d143102d002", "b36e96d43ed75485258f0fdf0cefe143" };
		OAuthV1 oauth = new OAuthV1();
		OauthInit.oauthInit(oauth, token[0], token[1]);
		TAPI weibo = new TAPI(oauth.getOauthVersion());
		ObjectMapper mapper = new ObjectMapper();
		try {
			String weiboinfo = weibo.show(oauth, "json", "304345080623903");
			System.out.println(weiboinfo);
			Show show = mapper.readValue(weiboinfo, Show.class); // 有问题，待解决
			System.out.println(show.getData().getName());
		} catch (UnrecognizedPropertyException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			e.printStackTrace();
		}
	}

}
