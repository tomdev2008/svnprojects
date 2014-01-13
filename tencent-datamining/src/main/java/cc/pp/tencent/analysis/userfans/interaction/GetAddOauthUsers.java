package cc.pp.tencent.analysis.userfans.interaction;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.utils.HttpUtils;
import cc.pp.tencent.utils.JsonUtils;

/**
 * Title: 获取新增用户
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class GetAddOauthUsers implements Serializable {
	
	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	// http://testt2.pp.cc/api/bind/index?type=sina&token=b14d48ede1f7c2f7779585ef3f9102f0
	// http://t2.pp.cc/api/bind/index?type=
	private static final String BASEURL = new String("http://t2.pp.cc/api/bind/index?type=");
	private static final String TOKEN = new String("b14d48ede1f7c2f7779585ef3f9102f0");

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws HttpException, IOException {
		// TODO Auto-generated method stub
		GetAddOauthUsers gaau = new GetAddOauthUsers();
		HashMap<Integer,String> addusers = gaau.getAddUsersByDay("tencent", "0");
		System.out.println(addusers.size());
		for (int i = 0; i < addusers.size(); i++) {
			System.out.println(addusers.get(i));
		}
	}
	
	/**
	 * 获取每天增加的用户
	 * @param type
	 * @param bid
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public HashMap<Integer,String> getAddUsersByDay(String type, String bid) throws HttpException, IOException {
		
		HashMap<Integer,String> addusers = new HashMap<Integer,String>();
		String url = GetAddOauthUsers.getUrl(type, bid);
		HttpUtils httpUtils = new HttpUtils();
		String data = httpUtils.getAddUsers(url);
		JsonNode jsondata = JsonUtils.getJsonNode(data, "list");   // 字段名改下
		String info = new String();
		int k = 0;
		for (int i = 0; i < jsondata.size(); i++) {
			info = jsondata.get(i).get("bind_username").toString().replaceAll("\"", "");
			if (StringUtils.isEmpty(info)) {
				continue;
			}
			addusers.put(k++, info);
		}
		
		return addusers;
	}
	
	/**
	 * 获取url
	 * @param type
	 * @return
	 */
	public static String getUrl(String type, String bid) {
		String url = BASEURL + type + "&bid=" + bid + "&token=" + TOKEN;
		return url;
	}
	
}
