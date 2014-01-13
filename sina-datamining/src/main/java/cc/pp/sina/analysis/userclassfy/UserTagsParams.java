package cc.pp.sina.analysis.userclassfy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import com.sina.weibo.api.Tags;
import com.sina.weibo.model.Tag;
import com.sina.weibo.model.WeiboException;

public class UserTagsParams implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 测试函数
	 * @param args
	 * @throws WeiboException 
	 */
	public static void main(String[] args) {

		UserTagsParams utp = new UserTagsParams();
		String accesstoken = "2.002214mBdcZIJC85be2bb2d9teRmOE";
		String result = utp.analysis("2981827167", accesstoken);
		System.out.println(result);
	}

	/**
	 * 分析函数
	 * @param uid
	 * @param accesstoken
	 * @return
	 * @throws WeiboException
	 */
	public String analysis(String uid, String accesstoken) {

		try {
			Tags tm = new Tags();
			tm.client.setToken(accesstoken);
			List<Tag> tags = tm.getTags(uid);
			HashMap<String, String> tagmaps = new HashMap<String, String>();
			int max = (tags.size() > 10) ? 10 : tags.size(); // 限制10个标签信息
			for (int i = 0; i < max; i++) {
				tagmaps.put(Integer.toString(i), tags.get(i).getValue());
			}
			JSONArray result = JSONArray.fromObject(tagmaps);
			return result.toString();
		} catch (RuntimeException e) {
			return null;
		} catch (WeiboException e) {
			return null;
		}
	}

}
