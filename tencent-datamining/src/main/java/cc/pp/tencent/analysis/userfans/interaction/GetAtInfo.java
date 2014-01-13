package cc.pp.tencent.analysis.userfans.interaction;

import org.apache.commons.httpclient.URIException;
import org.codehaus.jackson.JsonNode;
import com.tencent.weibo.api.StatusesAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

import cc.pp.tencent.common.Emotion;
import cc.pp.tencent.utils.JsonUtils;

public class GetAtInfo {

	public GetAtInfo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		GetAtInfo gai = new GetAtInfo();
		String[] token = {"780c1d0dc0884f8d81d29d348cc69ae5","8e18b74b1d78317c4cac5637eb665247"};
		int[] result = gai.atInfoAnalysis(token);
		System.out.println(result[0]);
		System.out.println(result[1]);
		System.out.println(result[2]);
		System.out.println(result[3]);
	}
	
	/**
	 * AT(@)交互信息分析
	 * @throws Exception 
	 * @throws WeiboException
	 * @throws URIException 
	 */
	public int[] atInfoAnalysis(String[] token) throws Exception {
		/* 初始化结果值
		 * result[0]----负面情感数
		 * result[1]----正面情感数
		 * result[2]----中性情感数
		 * result[3]----@数量
		 */
		int[] result = new int[4];
		OAuthV1 oauth = new OAuthV1();
		InteractionsAnalysis.oauthInit(oauth, token[0], token[1]);
		StatusesAPI atinfo = new StatusesAPI(oauth.getOauthVersion());
		String ats = atinfo.mentionsTimeline(oauth, "json", "0", "0", "100", "0", "3", "0");
		JsonNode atsdata = JsonUtils.getJsonNode(ats);
		String lasttime = null;  // 第一页最后一条记录的时间
		String lastid = null;  // 第一页最后一条记录的id
		boolean flag = true;
		long time = 0;
		long t = System.currentTimeMillis()/1000;
		try {
			for (int i = 0; i < atsdata.get("data").get("info").size(); i++) {
				time = Integer.parseInt(atsdata.get("data").get("info").get(i).get("timestamp").toString());
				if (time < t - 86400) {
					flag = false;
					break;
				} else {
					result[3]++;
					result[this.getEmotions(atsdata.get("data").get("info").get(i).get("text").toString())]++;
				}
				if (i == atsdata.get("data").get("info").size() - 1) {
					lasttime = atsdata.get("data").get("info").get(i).get("timestamp").toString();
					lastid = atsdata.get("data").get("info").get(i).get("id").toString();
				}
			}
			
			if (Integer.parseInt(atsdata.get("data").get("totalnum").toString()) <= 70) {
				flag = false;
			}

			while (flag) {
				ats = atinfo.mentionsTimeline(oauth, "json", "1", lasttime, "100", lastid, "3", "0");
				atsdata = JsonUtils.getJsonNode(ats);
				for (int i = 0; i < atsdata.get("data").get("info").size(); i++) {
					time = Integer.parseInt(atsdata.get("data").get("info").get(i).get("timestamp").toString());
					if (time < t - 86400) {
						flag = false;
						break;
					} else {
						result[3]++;
						result[this.getEmotions(atsdata.get("data").get("info").get(i).get("text").toString())]++;
					}
					if (i == atsdata.get("data").get("info").size() - 1) {
						lasttime = atsdata.get("data").get("info").get(i).get("timestamp").toString();
						lastid = atsdata.get("data").get("info").get(i).get("id").toString();
					}
				}
			}
		} catch (RuntimeException e) {
			return result;
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
		jsondata = JsonUtils.getJsonNode(data);
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

}
