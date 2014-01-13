package cc.pp.sina.analysis.topic;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.jdbc.TopicJDBC;
import cc.pp.sina.utils.HttpUtils;

/**
 * Title: 话题分析工具类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TopicUtils implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @获取accesstoken
	 * @param ip
	 * @param nums
	 * @return
	 * @throws SQLException
	 */
	public static String[] getAccessTokens(int nums, String ip) throws SQLException {
		
		String[] tokens = new String[nums];
		TopicJDBC jdbc = new TopicJDBC(ip);
		if (jdbc.mysqlStatus()) {
			tokens = jdbc.getAccessToken(nums);
			jdbc.sqlClose();
		}
		
		return tokens;
	}
	
	/**
     * @提取wid
     * @param url
     * @return
     * @throws HttpException
     * @throws IOException
     */
	public static String[] getWids(String originalurl, int pages) throws HttpException, IOException {
		
		String[] result = null;
		String html;
		String url;
		Set<String> wids = null;
		HashMap<String,Integer> temp = new HashMap<String,Integer>();
		int index;
		String wid = null;
//		String keyword = originalurl.substring(25, originalurl.indexOf("topnav")); //Refer
		
		int maxpage = (pages <= 30) ? pages : 30;  //每小时超过39IP被封
		for (int i = 1; i <= maxpage; i++)
		{
			//			System.out.println("Page：" + i);

			url = TopicUtils.getUrl(originalurl, i);
			HttpUtils httpUtils = new HttpUtils();
			html = httpUtils.getTopicHtml(url);
			//			System.out.println(html);
			index = html.indexOf("mid=");
			if (index != -1) {
				if (html.substring(index + 4, index + 5).equals("\\")) {
					wid = html.substring(index + 6, index + 22);
					html = html.substring(index + 20);
				} else {
					wid = html.substring(index + 4, index + 20);
					html = html.substring(index + 20);
				}
			} else {
				break;
			}
			temp.put(wid, 1);
			while (html != null) 
			{
				index = html.indexOf("mid=");
				if (index != -1) {
					if (html.substring(index + 4, index + 5).equals("\\")) {
						wid = html.substring(index + 6, index + 22);
						html = html.substring(index + 20);
					} else {
						wid = html.substring(index + 4, index + 20);
						html = html.substring(index + 20);
					}
					if (temp.get(wid) == null) {
						temp.put(wid, 1);
					}
				} else {
					break;
				}
			}
		}
		result = new String[temp.size()];
		wids = temp.keySet();
		Iterator<String> iterator = wids.iterator();
		int j = 0;
        while(iterator.hasNext()){
        	result[j++] = iterator.next();
        }
		
		return result;
	}

	/**
	 * @获取URL
	 * @param originalurl
	 * @param page
	 * @return
	 */
	public static String getUrl(String originalurl, int page) {
		
		int index = originalurl.indexOf("topnav");
		if (index == -1) {
			index = originalurl.indexOf("Refer");
		}
		String url = originalurl.substring(0, index);
        url = url + "page=" + page;
        
        return url;
	}

}
