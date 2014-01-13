package cc.pp.tencent.analysis.topic;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;

import cc.pp.tencent.jdbc.TopicJDBC;
import cc.pp.tencent.utils.HttpUtils;
import cc.pp.tencent.utils.URLCode;

public class TopicUtils implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @获取accesstoken
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessToken(int nums, String ip) throws SQLException {

		TopicJDBC jdbc = new TopicJDBC(ip);
		String[] token = null;
		if (jdbc.mysqlStatus()) {
			token = jdbc.getAccessTokens(nums);
			jdbc.sqlClose();
		}

		return token;
	}
	
    /**
     * @获取wid
     * @param keywords
     * @param pages
     * @param starttime：起始时间，YYYYMMDD,如20130422
     * @param endtime：终止时间
     * @return
     * @throws HttpException
     * @throws IOException
     */
	public String[] getWids(String keywords, int pages) throws HttpException, IOException {

		String[] result = null;
		String html;
		String url;
		Set<String> wids = null;
		HashMap<String,Integer> temp = new HashMap<String,Integer>();
		int index;
		String wid = null;

		int maxpage = (pages <= 30) ? pages : 30;  //每小时超过39IP被封
		HttpUtils httpUtils = new HttpUtils();
		
		for (int i = 1; i <= maxpage; i++)
		{
			url = this.getURL(keywords, i);
			html = httpUtils.getTopicHtml(url);
			//			System.out.println(html);
			index = html.indexOf("omid=");
			if (index != -1) {
				wid = html.substring(index + 5, index + 20);
				html = html.substring(index + 20);
			} else {
				break;
			}
			temp.put(wid, 1);
			while (html != null) 
			{
				index = html.indexOf("omid=");
				if (index != -1) {
					wid = html.substring(index + 5, index + 20);
					html = html.substring(index + 20);
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
	 * @获取url
	 * @param keywords
	 * @param pages
	 * @return
	 */
	public String getURL(String keywords, int pages) {
		
		String url = null;
		URLCode urlcode = new URLCode();
		String encodedkey = urlcode.Utf8URLencode(keywords);
//		if (starttime != null) {
//			start = starttime;
//		}
		String end = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		end = format.format(new Date((System.currentTimeMillis()/1000)*1000l));
		end = end.substring(0,4) + end.substring(5,7) + end.substring(8);
		//position=179,181,211
		//		url = "http://search.t.qq.com/index.php?k=" + encodedkey + "&pos=211&p=" + pages;  //pc端
		//		url = "http://ti.3g.qq.com/g/s?sid=AQw6lQ3IZHjLZmI37Wtovs5e&aid=sm&keyword=" + encodedkey + "&pid=" + pages;//手机端

		url = "http://ti.3g.qq.com/g/s?sid=AQw6lQ3IZHjLZmI37Wtovs5e&aid=sm&keyword=" + encodedkey + 
				"&rw=0&start=" + "&end=" + end + "&hw=0&sss=32&eid=431&cate=0&psize=10&pid=" + pages;
		//		System.out.println(url);
		return url;
	}

}
