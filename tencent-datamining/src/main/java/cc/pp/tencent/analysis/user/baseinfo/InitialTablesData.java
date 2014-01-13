package cc.pp.tencent.analysis.user.baseinfo;

import java.util.HashMap;

import cc.pp.tencent.jdbc.UbiaJDBC;

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
	 * @throws Exception 
	 * @throws JSONException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws Exception {

		InitialTablesData bw = new InitialTablesData("192.168.1.192");
		bw.init();

	}
	
	/**
	 * 第一次结果记录
	 * @throws Exception 
	 * @throws JSONException 
	 * @throws WeiboException 
	 */
	public void init() throws Exception {
		
		UbiaJDBC jdbc = new UbiaJDBC(this.ip);
		if (jdbc.mysqlStatus()) 
		{
			/*******************获取T2授权用户*********************/
			GetAddOauthUsers gaau = new GetAddOauthUsers();
			HashMap<Integer,String> tencentusers = gaau.getAddUsersByDay("tencent", "0");
			/*******************存储起始分析数据***********************/
			BasedInfoGet big = new BasedInfoGet(this.ip);
			big.insertInitinfo(tencentusers);
			
			jdbc.sqlClose();
		}
	}

}
