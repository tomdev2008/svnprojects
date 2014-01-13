package cc.pp.sina.analysis.userweibo;

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

import cc.pp.sina.jdbc.UserWeiboJDBC;

public class MultiThreadsUserWeibos {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsUserWeibos.class);
	private final static Random random = new Random();

	/**
	 * 主函数
	 * @param args
	 * @throws SQLException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws SQLException, InterruptedException {

		final ThreadPoolExecutor pool = getThreadPoolExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				pool.shutdown();
			}
		}));

		//		UserWeiboJDBC myJdbc = new UserWeiboJDBC(Nettool.getServerLocalIp());
		UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");
		List<String> tokens = null;
		List<String> sinausers = null;
		if (myJdbc.mysqlStatus()) {
			myJdbc.createUserWeiboTable("sinauserweiboinfo");
			tokens = myJdbc.getAllTokens();
			sinausers = myJdbc.getSinaUsers("sinausers", 5);
			//			myJdbc.deleteBatch("sinausers", sinausers);
			myJdbc.sqlClose();
		}

		int count = 0;
		while (count < sinausers.size() && !pool.isShutdown()) {
			//					System.out.println("Page: " + max + " Count: " + count);
			try {
				pool.execute(new GetUserWeibos(tokens.get(random.nextInt(tokens.size())), sinausers.get(count)));
			} catch (Exception e) {
				myJdbc.insertUnusedUsers("sinaunuseduids", sinausers.get(count));
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
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						e.printStackTrace();
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
