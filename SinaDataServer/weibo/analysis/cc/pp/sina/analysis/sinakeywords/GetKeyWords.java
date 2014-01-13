package cc.pp.sina.analysis.sinakeywords;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.constant.KeyWordsConstant;
import cc.pp.sina.jdbc.KeyWordsJDBC;
import cc.pp.sina.utils.HttpUtils;
import cc.pp.sina.utils.URLCode;

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
	
	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public GetKeyWords(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws HttpException, IOException, SQLException {
		// TODO Auto-generated method stub
		GetKeyWords gkd = new GetKeyWords("192.168.1.151");
		gkd.insertResult();
		System.out.println("OK");
	}
	
	/**
	 * 输出homepage和all关键词
	 * @throws HttpException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void insertResult() throws HttpException, IOException, SQLException {
		
		String[] types = {"homepage","all","event","films","finance","person","sports"};
		String homepage = this.getKeywordAndTimes(this.getURL(types[0]));
		String all = this.getKeywordAndTimes(this.getURL(types[1]));
		String event = this.getKeywordAndTimes(this.getURL(types[2]));
		String films = this.getKeywordAndTimes(this.getURL(types[3]));
		String finance = this.getKeywordAndTimes(this.getURL(types[4]));
		String person = this.getKeywordAndTimes(this.getURL(types[5]));
		String sports = this.getKeywordAndTimes(this.getURL(types[6]));
		homepage = homepage.replaceAll("\"", "\\\\\"");
		all = all.replaceAll("\"", "\\\\\"");
		event = event.replaceAll("\"", "\\\\\"");
		films = films.replaceAll("\"", "\\\\\"");
		finance = finance.replaceAll("\"", "\\\\\"");
		person = person.replaceAll("\"", "\\\\\"");
		sports = sports.replaceAll("\"", "\\\\\"");

        KeyWordsJDBC jdbc = new KeyWordsJDBC(ip);
		if (jdbc.mysqlStatus()) {
			jdbc.insertResult(homepage, all, event, films, finance, person, sports);
			jdbc.sqlClose();
		}
	}
	
	/**
	 * 获取关键词和次数
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public String getKeywordAndTimes(String url) throws HttpException, IOException {
		
		HashMap<String,String> result = new HashMap<String,String>();
		HashMap<String,Integer> keywords = new HashMap<String,Integer>();
		HttpUtils httpUtils = new HttpUtils();
		String html = httpUtils.getKeywordsHtml(url);
        html = html.replaceAll("%25", "%");
		html = URLCode.Utf8URLdecode(html);
		String name = null;
		int times = 0;
		int start1 = 0;
		int end1 = 0;
		int start2 = 0;
		int end2 = 0;
		
		while (html != null) 
		{
			start1 = html.indexOf("\\/weibo\\/");
			end1 = html.indexOf("&refer=top");
			start2 = html.indexOf("><span>");
			if (start2 == -1) {
				break;
			}
			name = html.substring(start1 + 9, end1);
			html = html.substring(start2 + 7);
			end2 = html.indexOf("<\\/span>");
			times = Integer.parseInt(html.substring(0, end2));
			
			html = html.substring(end2 + 7);
			if (keywords.get(name) == null) {
				keywords.put(name, times);
			}
		}
		
		List<Map.Entry<String,Integer>> resulttemp = new ArrayList<Map.Entry<String,Integer>>(keywords.entrySet());
		
		Collections.sort(resulttemp, new Comparator<Map.Entry<String,Integer>>(){
			@Override
			public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		String temp = null;
		for (int k = 0; k < keywords.size(); k++) {
			temp = resulttemp.get(k).toString();
			result.put(Integer.toString(k), "{\"" + temp.substring(0,temp.indexOf("=")) + "\":" + temp.substring(temp.indexOf("=")+1) +"}");
		}
		
		JSONArray jsonresult = JSONArray.fromObject(result);
		
		return jsonresult.toString();
	}
	
	/**
	 * 获取URL
	 * @param type
	 * @return
	 */
	public String getURL(String type) {
		//type = homepage,all,event,films,finance,person,sports
		//热搜榜首页：http://s.weibo.com/top/summary?cate=homepage
		//综合：           http://s.weibo.com/top/summary?cate=total&key=all
		//实事：           http://s.weibo.com/top/summary?cate=total&key=event
		//影视：           http://s.weibo.com/top/summary?cate=total&key=films
		//财经：           http://s.weibo.com/top/summary?cate=total&key=finance
		//名人：           http://s.weibo.com/top/summary?cate=total&key=person  
		//体育：           http://s.weibo.com/top/summary?cate=total&key=sports
		String url = KeyWordsConstant.SINA_TOPIC_URL + "cate=total&key=" + type;
		if (type.equals("homepage")) {
			url = KeyWordsConstant.SINA_TOPIC_URL + "cate=" + type;
		}
		
		return url;
	}
}
