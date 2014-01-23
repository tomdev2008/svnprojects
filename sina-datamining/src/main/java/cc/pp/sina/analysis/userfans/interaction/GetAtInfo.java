package cc.pp.sina.analysis.userfans.interaction;

import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.httpclient.URIException;
import org.codehaus.jackson.JsonNode;

import cc.pp.sina.common.Emotion;
import cc.pp.sina.jdbc.UfiJDBC;
import cc.pp.sina.utils.JsonUtil;

import com.sina.weibo.api.Comments;
import com.sina.weibo.model.CommentWapper;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.WeiboException;

public class GetAtInfo {
	
	private String ip = "";

	public GetAtInfo(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 * @throws URIException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException, URIException {

		GetAtInfo gai = new GetAtInfo("192.168.1.27");
		String accesstoken = new String("2.00EmX8RBdcZIJCc6f711450c_RKXAB");
		int[] result = gai.atInfoAnalysis(accesstoken);
		System.out.println(result[0]);
		System.out.println(result[1]);
		System.out.println(result[2]);
		System.out.println(result[3]);

	}
	
	/**
	 * AT(@)交互信息分析
	 * @throws WeiboException
	 * @throws URIException 
	 */
	public int[] atInfoAnalysis(String accesstoken) throws WeiboException, URIException {
		/* 初始化结果值
		 * result[0]----负面情感数
		 * result[1]----正面情感数
		 * result[2]----中性情感数
		 * result[3]----@数量
		 */
		int[] result = new int[4];
//		String accesstoken = new String("2.00ujuhzBdcZIJC563af4958bjCGeQB");
		Comments cm = new Comments();
		cm.client.setToken(accesstoken);
		int page = 1;
		boolean flag = true;
		long time = System.currentTimeMillis()/1000;
		while (flag) {
			CommentWapper comments = null;
			try {
				comments = cm.getCommentMentions(new Paging(page++, 200), 0, 0);
			} catch (RuntimeException e) {
				break;
			}
			if (page*200 > comments.getTotalNumber()+200) {
				flag = false;
			}
//			System.out.println(comments.getTotalNumber());
			for (int i = 0; i < comments.getComments().size(); i++) {
				// 时间限制为一天
				if (comments.getComments().get(i).getCreatedAt().getTime()/1000 < time - 86400) {  
					flag = false;
					break;
				}
				result[3]++;
				result[this.getEmotions(comments.getComments().get(i).getText())]++;
			}
		}
		
		return result;
	}
	
	/**
	 * 获取情感值
	 * @param text
	 * @return
	 * @throws URIException 
	 */
	public int getEmotions(String text) throws URIException {
		// 返回值初始化
		int result = 0;
		Emotion emotion = new Emotion();
		JsonNode jsondata;
		String data;
		double label = 0.0;
		double score = 0.0;
		
		if (text.indexOf("//") < 2) {
			text = "OK";
		} else {
			text = text.substring(0, text.indexOf("//"));
		}
		data = emotion.getEmotion(text);
		jsondata = JsonUtil.getJsonNode(data);
		label = jsondata.get("label").getDoubleValue();
		score = jsondata.get("score").getDoubleValue();
		if (label < 0.5) {
			result = 0;   // 0---负面
		} else if (label > 1.5) {
			result = 1;   // 1---正面
		} else {
			if (score > 0.63) {
				result = 1;  // 1---正面
			} else {
				result =2;  // 2---中性
			}
		}
		
		return result;
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	public HashMap<String,String> getT2Tokens() throws SQLException {
		
		UfiJDBC ufijdbc = new UfiJDBC(this.ip);
		if (ufijdbc.mysqlStatus()) {
//			System.out.println(ufijdbc.getMaxBid());
			HashMap<String,String> t2users = ufijdbc.getT2Users();
			ufijdbc.sqlClose();
			return t2users;
		} else {
			return null;
		}
	}
}
