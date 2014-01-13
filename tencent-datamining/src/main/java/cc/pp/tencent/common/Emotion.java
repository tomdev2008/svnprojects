package cc.pp.tencent.common;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

import cc.pp.tencent.utils.HttpUtils;

/**
 * Title: 获取情感信息值
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class Emotion implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String BASEURL = "http://60.169.74.147:3305/EmotionAnalysis/emotion?";
	private static String TOKEN = "mnvrjierrdqdiefxanjp";
	private static String TYPE = "2";

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws HttpException, IOException {
		// TODO Auto-generated method stub
		Emotion emotion = new Emotion();
		String result = emotion.getEmotion("真的好开心开心");
		System.out.println(result);
	}
	
	/**
	 * 获取情感信息
	 * @param content
	 * @return
	 * @throws URIException
	 */
	public String getEmotion(String content) throws URIException{		
		//知道token信息
		String queryString = "token=" + TOKEN;
		//知道类型
		queryString = queryString + "&type=" + TYPE;
		//对内容进行编码
		String contentEncode = URIUtil.encodeAll(content, "utf-8");
		queryString = queryString + "&words=" + contentEncode;
		
		String url = BASEURL + "?" + queryString;
		//调用情感分析
		HttpUtils httpUtils = new HttpUtils();
		String  response = httpUtils.doGet(url, "utf-8");
		
		return response;
	}
	
}
