package cc.pp.tencent.analysis.tencentkeywords;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import org.apache.commons.httpclient.HttpException;

/**
 * Title: 获取新浪关键词
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class GetKeyWords implements Serializable {
	
	/**
	 * 默认序列号版本
	 */
	private static final long serialVersionUID = 1L;
	
//	private int ip = 0;

	/**
	 * 构造函数
	 * @param ip
	 */
	public GetKeyWords(int ip) {
		// TODO Auto-generated constructor stub
//		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws HttpException, IOException, SQLException {
		// TODO Auto-generated method stub

	}

}
