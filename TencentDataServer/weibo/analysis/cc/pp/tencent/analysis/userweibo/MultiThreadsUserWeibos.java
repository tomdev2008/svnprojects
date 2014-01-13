package cc.pp.tencent.analysis.userweibo;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.tencent.jdbc.UserWeiboJDBC;
import cc.pp.tencent.utils.Nettool;

public class MultiThreadsUserWeibos {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsUserWeibos.class);
	private final static Random random = new Random();

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws SQLException, InterruptedException {

		final ThreadPoolExecutor pool = getThreadPoolExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				pool.shutdown();
			}
		}));

		UserWeiboJDBC myJdbc = new UserWeiboJDBC(Nettool.getServerLocalIp());
		//		UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");
		List<String> tokens = null;
		List<String> tencentusers = null;
		if (myJdbc.mysqlStatus()) {
			//			myJdbc.createUserWeiboTable("tencentuserweiboinfo");
			tokens = myJdbc.getAllTokens();
			tencentusers = myJdbc.getTencentUsers("tencentusers", 200000);
			//			myJdbc.deleteBatch("tencentusers", tencentusers);
			myJdbc.sqlClose();
		}

		int count = 0;
		while (count < tencentusers.size() && !pool.isShutdown()) {
			try {
				pool.execute(new GetUserWeibos(tokens.get(random.nextInt(tokens.size())), tencentusers.get(count)));
			} catch (Exception e) {
				//				myJdbc.insertUnusedUid("tencentunuseduids", tencentusers.get(count));
				logger.error("Thread exception: " + Thread.currentThread().getName(), e);
				break;
			}
			count++;
		}

		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.SECONDS);
	}

	/**
	 * 申请线程池
	 * @return
	 */
	private static ThreadPoolExecutor getThreadPoolExecutor() {
		final ThreadPoolExecutor result = new ThreadPoolExecutor(64, 64, 0L, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(128), new ThreadPoolExecutor.CallerRunsPolicy());
		result.setThreadFactory(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
						//						e.printStackTrace();
						logger.error("Thread exception: " + t.getName(), e);
						result.shutdown();
					}
				});
				return t;
			}
		});
		return result;
	}

}
