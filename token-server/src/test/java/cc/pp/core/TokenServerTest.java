package cc.pp.core;

import java.sql.SQLException;

import org.junit.Test;

public class TokenServerTest {

	@Test
	public void sinaTokenTest() throws SQLException {
		/***********本地环境***********/
		TokenServer tokenServer = new TokenServer("127.0.0.1", "root", "", "sina");
		/***********线上环境***********/
		//		TokenServer tokenServer = new TokenServer("sina");
		String token = tokenServer.getSinaRandomToken();
		System.out.println(tokenServer.getTokensSize());
		System.out.println(token);
	}

	@Test
	public void tencentTokenTest() throws SQLException {
		/***********本地环境***********/
		TokenServer tokenServer = new TokenServer("127.0.0.1", "root", "", "tencent");
		/***********线上环境***********/
		//		TokenServer tokenServer = new TokenServer("tencent");
		String[] token = tokenServer.getTencentRandomToken();
		System.out.println(tokenServer.getTokensSize());
		System.out.println(token[0] + "," + token[1]);
	}

}
