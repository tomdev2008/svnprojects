package cc.pp.sina.analysis.user.baseinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.jdbc.UbiaJDBC;

import com.sina.weibo.json.JSONException;
import com.sina.weibo.model.WeiboException;

/**
 * Title: 起始工作
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class InitialTablesData {
	
	private String ip = "";

	public InitialTablesData(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws JSONException 
	 * @throws WeiboException 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws SQLException, HttpException, IOException, WeiboException, JSONException {

		InitialTablesData bw = new InitialTablesData("192.168.1.192");
		bw.init();
	}
	
	/**
	 * 第一次结果记录
	 * @throws SQLException
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws JSONException 
	 * @throws WeiboException 
	 */
	public void init() throws SQLException, HttpException, IOException, WeiboException, JSONException {
		
		UbiaJDBC jdbc = new UbiaJDBC(this.ip);
		if (jdbc.mysqlStatus()) 
		{
			/*******************获取T2授权用户*********************/
			GetAddOauthUsers gaau = new GetAddOauthUsers();
			HashMap<Integer,String> sinausers = gaau.getAddUsersByDay("sina", "0");
			/*******************存储起始分析数据***********************/
			BasedInfoGet big = new BasedInfoGet(this.ip);
			big.insertInitinfo(sinausers);
			
			jdbc.sqlClose();
		}
	}

}
