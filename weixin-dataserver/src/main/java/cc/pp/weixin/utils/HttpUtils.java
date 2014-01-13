package cc.pp.weixin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Title: Http解析工具
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class HttpUtils {
	
	//	private static Logger logger = Logger.getLogger(HttpUtils.class);
	
	/**
	 * 主函数
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws HttpException, IOException {

		HttpUtils httpUtils = new HttpUtils();
		String url = "http://www.anyv.net/index.php/category-19";
		String html = httpUtils.retriveHtml(url, "gbk");
		System.out.println(html);
	}

	public String retriveHtml(String url, String charset) throws HttpException, IOException {

		String result = new String();
		HttpClient httpClient = new HttpClient();
		GetMethod method = new GetMethod(url);

		method.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		method.getParams().setParameter("http.socket.timeout", new Integer(5000));
		method.getParams().setParameter("http.connection.timeout", new Integer(5000));
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		httpClient.executeMethod(method);
		int state = method.getStatusCode();

		if (state == HttpStatus.SC_OK) {
			java.io.InputStream in = method.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
			String s = "";
			while ((s = br.readLine()) != null) {
				result = result + s + "\n";
			}
			method.releaseConnection();
		} else {
			method.releaseConnection();
			return "err";
		}

		return result;
	}

	/** 
     * 执行一个HTTP GET请求，返回请求响应的HTML 
     * 
     * @param url                请求的URL地址 
     * @param queryString		   请求的查询参数,可以为null 
     * @param charset         	   字符集 
     * @param pretty             是否美化 
     * @return 					
     *                           返回请求响应的HTML 
     */ 
    public String doGet(String url, String queryString, String charset, boolean pretty) { 

        StringBuffer response = new StringBuffer(); 
        HttpClient client = new HttpClient(); 
        HttpMethod method = new GetMethod(url); 
        try { 
    		// 对get请求参数做了http请求默认编码，好像没有任何问题，汉字编码后，就成为%式样的字符串
            if (StringUtils.isNotBlank(queryString)){
            	method.setQueryString(URIUtil.encodeQuery(queryString)); 
            }
            client.executeMethod(method); 
            if (method.getStatusCode() == HttpStatus.SC_OK) { 
                BufferedReader reader = new BufferedReader(
                		new InputStreamReader(method.getResponseBodyAsStream(), charset)
                		); 
                String line; 
                while ((line = reader.readLine()) != null) { 
                    if (pretty){
                    	response.append(line).append(System.getProperty("line.separator")); 
                    } else {
                    	response.append(line); 
                    }
                } 
                reader.close(); 
            } 
        } catch (URIException e) { 
			//        	logger.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！", e); 
			//			logger.error("执行HTTP Get请求时，编码查询字符串“" + queryString + "”发生异常！");
			return "err";
        } catch (IOException e) { 
			//        	logger.error("执行HTTP Get请求" + url + "时，发生异常！", e); 
			//			logger.error("执行HTTP Get请求" + url + "时，发生异常！");
			return "err";
        } finally { 
            method.releaseConnection(); 
        } 
        return response.toString(); 
    } 
    
    /**
     * 重载函数
     * @param url
     * @param charset
     * @return
     */
    public String doGet(String url,String charset) { 
    	return this.doGet(url, "", charset, Boolean.TRUE);
    }

}
