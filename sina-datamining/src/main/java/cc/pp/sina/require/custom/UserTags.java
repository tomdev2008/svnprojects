package cc.pp.sina.require.custom;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import cc.pp.sina.algorithms.PPSort;

import com.sina.weibo.api.Tags;
import com.sina.weibo.model.Tag;
import com.sina.weibo.model.TagWapper;
import com.sina.weibo.model.WeiboException;

public class UserTags {

	/**
	 * 获取用户标签信息
	 * @param uid
	 * @param accesstoken
	 * @return
	 */
	public static String userTags(String uid, String accesstoken) {

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

	public static HashMap<String, HashMap<String, String>> getFansTags(String accesstoken, String[] top40fans)
			throws WeiboException {

		HashMap<String, Integer> tagsandsum = new HashMap<String, Integer>();
		String uids1 = "", uids2 = "";
		for (int i = 0; i < top40fans.length; i++) {
			if (top40fans[i].length() > 4) {
				if (i < 20)
					uids1 = uids1 + top40fans[i].substring(0, top40fans[i].indexOf("=")) + ",";
				else
					uids2 = uids2 + top40fans[i].substring(0, top40fans[i].indexOf("=")) + ",";
			} else {
				break;
			}
		}
		uids1 = uids1.substring(0, uids1.length() - 1);
		Tags tm = new Tags();
		tm.client.setToken(accesstoken);
		if (uids1.length() > 2) {
			transTags(tm, tagsandsum, uids1);
		}
		if (top40fans[20] != null) {
			uids2 = uids2.substring(0, uids2.length() - 1);
			transTags(tm, tagsandsum, uids2);
		}
		HashMap<String, HashMap<String, String>> result = PPSort.sortedToDoubleMap(tagsandsum, "tag", "weight", 20);

		return result;
	}

	public static void transTags(Tags tm, HashMap<String, Integer> tagsandsum, String uids) throws WeiboException {

		List<TagWapper> tags = tm.getTagsBatch(uids);
		String value, weight;
		for (int j = 0; j < tags.size(); j++) {
			for (int i = 0; i < tags.get(j).getTags().size(); i++) {
				value = tags.get(j).getTags().get(i).getValue();
				weight = tags.get(j).getTags().get(i).getWeight();
				if (tagsandsum.get(value) == null) {
					tagsandsum.put(value, Integer.parseInt(weight));
				} else {
					tagsandsum.put(value, tagsandsum.get(value) + Integer.parseInt(weight));
				}
			}
		}
	}

}
