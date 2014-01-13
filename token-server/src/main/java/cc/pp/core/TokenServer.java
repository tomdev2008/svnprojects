package cc.pp.core;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import cc.pp.sql.TokenJDBC;

public class TokenServer {

	private List<String> tokens;
	private final Random random = new Random();

	/**
	 * 线上环境
	 */
	public TokenServer(String type) throws SQLException {
		TokenJDBC myjdbc = new TokenJDBC();
		if (myjdbc.mysqlStatus()) {
			tokens = myjdbc.getAllTokens(type);
			myjdbc.sqlClose();
		}
	}

	/**
	 * 本地环境
	 */
	public TokenServer(String ip, String user, String password, String type) throws SQLException {
		TokenJDBC myjdbc = new TokenJDBC(ip, user, password);
		if (myjdbc.mysqlStatus()) {
			tokens = myjdbc.getAllTokens(type);
			myjdbc.sqlClose();
		}
	}

	public String getSinaRandomToken() {
		return getRandomToken();
	}

	public String[] getTencentRandomToken() {
		String token = getRandomToken();
		String[] result = new String[2];
		result[0] = token.substring(0, token.indexOf(","));
		result[1] = token.substring(token.indexOf(",") + 1);
		return result;
	}

	private String getRandomToken() {
		return tokens.get(random.nextInt(getTokensSize()));
	}

	public int getTokensSize() {
		return tokens.size();
	}
}
