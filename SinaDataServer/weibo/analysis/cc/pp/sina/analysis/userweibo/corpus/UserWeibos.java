package cc.pp.sina.analysis.userweibo.corpus;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.sina.common.EmotionType;
import cc.pp.sina.jdbc.UserWeiboJDBC;

import com.sina.weibo.model.Status;

public class UserWeibos implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(UserWeibos.class);

	private final String uid;
	private final UserWeibosDao userWeibosDao;
	private final UserWeiboJDBC myJdbc;
	private final String tablename;

	private final static EmotionType emotionType = new EmotionType();

	private static AtomicInteger count = new AtomicInteger(0);

	public UserWeibos(String uid, UserWeibosDao userWeibosDao, //
			UserWeiboJDBC myJdbc, String tablename) {
		this.uid = uid;
		this.userWeibosDao = userWeibosDao;
		this.myJdbc = myJdbc;
		this.tablename = tablename;
	}

	@Override
	public void run() {
		try {

			System.out.println(count.addAndGet(1));

			List<Status> weibos = userWeibosDao.getUserWeibo(uid);
			for (Status weibo : weibos) {
				try {
					if (getEmotionType(weibo.getText()) != null) {
						myJdbc.insertCorpusWeiboText(tablename, getEmotionType(weibo.getText()), weibo.getText());
					} else {
						myJdbc.insertCorpusWeiboText(tablename, "no", weibo.getText());
					}
				} catch (SQLException e) {
					continue;
				}
			}
		} catch (Exception e) {
			logger.info("Error uid: " + uid);
			e.printStackTrace();
		}
	}

	public static String getEmotionType(String text) {

		String[] strs = null;
		for (Entry<String, String> et : emotionType.getEmotions().entrySet()) {
			strs = et.getValue().split(",");
			for (String str : strs) {
				if (text.contains(str)) {
					return et.getKey();
				}
			}
		}

		return null;
	}

}
