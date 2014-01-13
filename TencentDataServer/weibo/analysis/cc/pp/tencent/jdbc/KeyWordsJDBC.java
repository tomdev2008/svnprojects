package cc.pp.tencent.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import cc.pp.tencent.constant.DataBaseConstant;

/**
 * Title: 关键词提取的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class KeyWordsJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;
	
	/**
	 * @construct
	 * @param ip
	 */
	public KeyWordsJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public KeyWordsJDBC(String ip, String user, String password) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
		this.user = user;
		this.password = password;
	}
	
	/**
	 * @ 判断连接状态
	 * @return
	 */
	public boolean mysqlStatus() {
		
		boolean status = true;
		try {
			Class.forName(this.driver);
			this.conn = DriverManager.getConnection(this.url, this.user, this.password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			status = false;
		} catch (SQLException e) {
			e.printStackTrace();
			status = false;
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}
		
		return status;
	}
	
	/**
	 * 插入关键词到数据表中
	 * @param homepage
	 * @param all
	 * @param event
	 * @param films
	 * @param finance
	 * @param person
	 * @param sports
	 * @throws SQLException
	 */
	public void insertResult(String homepage, String all, String event, String films,
			String finance, String person, String sports) throws SQLException {
		
		long lasttime = System.currentTimeMillis()/1000;
		SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd HH");
		String time = fo.format(new Date(lasttime*1000l));

		String sql = new String("INSERT INTO `keywords` (`time`,`homepage`,`all`,`event`,`films`," +
				"`finance`,`person`,`sports`,`lasttime`) VALUES (\"" + time + "\",\"" + homepage + "\",\"" 
				+ all + "\",\"" + event + "\",\"" + films + "\",\"" + finance + "\",\"" +
				person + "\",\"" + sports + "\"," + lasttime + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}
	
    /**
     * @ 关闭数据库
     * @throws SQLException
     */
	public void sqlClose() throws SQLException {
		this.conn.close();
	}

}


