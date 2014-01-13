package cc.pp.sina.common;

import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class Encryption {
	/**
	 * 日志输出
	 */
	private static Logger logger = Logger.getLogger(Encryption.class);
	
	/**
	 * 四个参数： 
	 * uid: 会员ID
	 * type: 微博类型 (sina或tencent) 
	 * url: 微博地址
	 * key: 密钥
	 * 
	 * 加密/校验流程：
	 * 1. 将uid、type、url、key四个参数进行字典序排序
	 * 2. 将四个参数字符串拼接成一个字符串进行sha1加密
	 */
	public static String getSignature(String uid, String type, String url, String key) {

		String tmpStr = uid + type + url + key;
		String signature = Encryption.Encrypt(tmpStr);
		
		return signature;
	}
	
	/**
	 * 将一个字符串进行sha1加密
	 * @param strSrc	原始字符串
	 * @return			进行sha1加密后的字符串
	 */
	public static  String Encrypt(String strSrc)  {                
		String strDes  = null;
		if (StringUtils.isEmpty(strSrc)){
			return strDes; 
		}
		try {
			MessageDigest md = MessageDigest.getInstance("md5"); //SHA-1
			byte[] bt = strSrc.getBytes();
			md.update(bt);
			strDes = bytesToHex(md.digest());
		} catch (Exception e) {
			logger.error("When ParseRequestData Process Encrypt Error", e);
		}
		return strDes; 
	}       
    
	/**
	 * 把字节流转换为 十六进制字符串
	 * @param bts	字节流
	 * @return		十六进制字符串
	 */
	public static String bytesToHex(byte[] bts) {
		String des = "";         
		String tmp=""; 
		if (bts == null){
			return des;
		}
		for (int i=0;i<bts.length;i++) {                    
			tmp = (Integer.toHexString(bts[i] & 0xFF));                    
			if (tmp.length()==1) {                        
				des += "0";                    
			}                    
			des += tmp;                
		}                
		return des;       
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(Encryption.getSignature("54623154", "sina", "http://www.weibo.com/1649155730/zErAfDkKM",
				"mnvrjierrdqdiefxanjp"));
	}
}
