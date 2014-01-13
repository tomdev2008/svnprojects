package cc.pp.sina.analysis.user.baseinfo;

import java.sql.SQLException;

import cc.pp.sina.jdbc.UbiaJDBC;

/**
 * Title: 创建5张数据表，用于存储用户基础信息（粉丝数、关注数、微博数）
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class CreateFiveTables {
	
	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public CreateFiveTables(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		CreateFiveTables cft = new CreateFiveTables("192.168.1.154");
		cft.generateTables();
	}
	
	/**
	 * 创建五张存放新浪用户基础信息分析结果的数据表
	 * @throws SQLException
	 */
	public void generateTables() throws SQLException {
		
		UbiaJDBC ubiajdbc = new UbiaJDBC(this.ip);
		if (ubiajdbc.mysqlStatus()) {
			String tablename = new String("sinauserinforesult");
			for (int i = 0; i < 5; i++) {
				ubiajdbc.createTables(tablename + i);
			}
		}
	}

}
