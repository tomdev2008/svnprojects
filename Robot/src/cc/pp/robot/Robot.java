package cc.pp.robot;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;


public class Robot {

	public static void main(String[] args) throws IOException {

		Robot robot = new Robot();
		System.out.println(robot.getAnswers("你好啊"));
	}

	public String getAnswers(String msg) throws IOException {

		String msgcoded = URLEncoder.encode("那当然！", "UTF-8");
		HttpClient client = new HttpClient();
		HttpMethod method = new GetMethod(String.format("http://www.simsimi.com/func/req?msg=%s&lc=ch", msgcoded));
		method.setRequestHeader("Host", "www.simsimi.com");
		method.setRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:23.0) Gecko/20100101 Firefox/23.0");
		method.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		method.setRequestHeader("Accept-Language", "en-US,en;q=0.5");
		method.setRequestHeader("Accept-Encoding", "gzip, deflate");
		method.setRequestHeader("Content-Type", "application/json; charset=utf-8");
		method.setRequestHeader("X-Requested-With", "XMLHttpRequest");
		method.setRequestHeader("Referer", "http://www.simsimi.com/talk.htm?lc=ch");
		method.setRequestHeader(
				"Cookie",
				"JSESSIONID=E8A6D99704D7DDA6B4B3A7F0784682F0; AWSELB=15E16D030EBAAAB8ACF4BD9BB7E0CA8FB501388662640BCEC6E9C54E70B150AA8514D30E844A0F6781F3C00BEC43069730243F418119D4A1660F073D105DD873991975B881; __utma=119922954.729866939.1376372813.1376372813.1376372813.1; __utmb=119922954.6.9.1376373485598; __utmc=119922954; __utmz=119922954.1376372813.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); sagree=true");
		method.setRequestHeader("Connection", "keep-alive");

		int status = client.executeMethod(method);
		if (status == 200) {
			return method.getResponseBodyAsString();
		} else {
			return "error";
		}
	}

}
