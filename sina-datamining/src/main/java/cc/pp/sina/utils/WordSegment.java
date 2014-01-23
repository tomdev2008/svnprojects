package cc.pp.sina.utils;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.jackson.JsonNode;

/**
 * Title: 分词工具
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class WordSegment implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String BASEURL = "http://60.169.74.147:3305/ExtractKeyWord/MyServlet?";
	private static String TOKEN = "mnvrjierrdqdiefxanjp";
	private static String wdcount = "50";

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws HttpException, IOException {
		// TODO Auto-generated method stub
		WordSegment wordsseg = new WordSegment();
		String result = wordsseg.getWordsSeg("初二因为什么事情不记得");
		JsonNode jsondata = JsonUtil.getJsonNode(result);
		System.out.println(jsondata.get(0).get("0").toString().replaceAll("\"", ""));
	}
	
	/**
	 * 获取情感信息
	 * @param content
	 * @return
	 * @throws URIException
	 */
	public String getWordsSeg(String words) throws URIException {
		// token信息
		String queryString = "token=" + TOKEN;
		// 分词个数
		queryString = queryString + "&wdcount=" + wdcount;
		// 词性
		queryString = queryString + "&character=" + "";
		//对内容进行编码
		String contentEncode = URIUtil.encodeAll(words, "utf-8");
		queryString = queryString + "&words=" + contentEncode;
		
		String url = BASEURL + queryString;
		//调用分词系统
		HttpUtils httpUtils = new HttpUtils();
		String  response = httpUtils.doGet(url, "utf-8");
		
		return response;
	}
	
}
