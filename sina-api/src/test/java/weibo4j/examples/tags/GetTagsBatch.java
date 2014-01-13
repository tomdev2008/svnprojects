package weibo4j.examples.tags;

import java.util.List;

import com.sina.weibo.api.Tags;
import com.sina.weibo.model.TagWapper;
import com.sina.weibo.model.WeiboException;

public class GetTagsBatch {

	public static void main(String[] args) {

		String access_token = "2.00mPMnoBdcZIJC679f008102axtYCB";
		Tags tm = new Tags();
		tm.client.setToken(access_token);
		List<TagWapper> tags = null;
		String uids = "1427995647,1649155730,1862087393";
		try {
			tags = tm.getTagsBatch(uids);
			System.out.println(tags);
			for (int j = 0; j < tags.size(); j++) {
				for (int i = 0; i < tags.get(j).getTags().size(); i++) {
					System.out.println(tags.get(j).getTags().get(i).getValue());
				}
			}

		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
