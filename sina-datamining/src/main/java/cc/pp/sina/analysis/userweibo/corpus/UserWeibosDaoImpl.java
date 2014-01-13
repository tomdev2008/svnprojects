package cc.pp.sina.analysis.userweibo.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sina.weibo.api.Timeline;
import com.sina.weibo.model.Paging;
import com.sina.weibo.model.Status;
import com.sina.weibo.model.StatusWapper;
import com.sina.weibo.model.WeiboException;

public class UserWeibosDaoImpl implements UserWeibosDao {

	private static final Logger logger = LoggerFactory.getLogger(UserWeibosDaoImpl.class);

	private final List<String> tokens;
	private final Random random = new Random();

	Timeline tm = new Timeline();

	public UserWeibosDaoImpl(List<String> tokens) {
		this.tokens = tokens;
	}

	@Override
	public List<Status> getUserWeibo(String uid) {

		String token = getRandomToken();
		tm.client.setToken(token);
		int page = 1;
		List<Status> result = new ArrayList<Status>();
		try {
			StatusWapper weiboinfos = tm.getUserTimelineByUid(uid, new Paging(page++, 100), 0, 0);
			long pagecount = (weiboinfos.getTotalNumber() >= 2000) ? 20 : weiboinfos.getTotalNumber() / 100 + 1;
			while (page <= pagecount) {
				for (Status s : weiboinfos.getStatuses()) {
					result.add(s);
				}
				weiboinfos = tm.getUserTimelineByUid(uid, new Paging(page++, 100), 0, 0);
			}
		} catch (WeiboException e) {
			logger.info("Error uid is: " + uid);
			if ("10022".equals(e.getErrorCode())) {
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} else {
				throw new RuntimeException(String.format("WeiboException: %d\t%s\t%s", e.getErrorCode(), uid, token), e);
			}
		} catch (RuntimeException e) {
			logger.info("Error uid is: " + uid);
		}

		return result;
	}

	/**
	 * 随机获取token
	 * @return
	 */
	public String getRandomToken() {
		return tokens.get(random.nextInt(tokens.size()));
	}

}
