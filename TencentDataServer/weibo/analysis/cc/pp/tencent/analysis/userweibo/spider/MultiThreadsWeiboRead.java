package cc.pp.tencent.analysis.userweibo.spider;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.tencent.jdbc.LocalJDBC;

public class MultiThreadsWeiboRead {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsWeiboRead.class);

	/**
	 * 主函数
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

		//		LocalJDBC myJdbc = new LocalJDBC(Nettool.getServerLocalIp());
		LocalJDBC myJdbc = new LocalJDBC();
		if (myJdbc.mysqlStatus()) {

			List<UserInfo> users = myJdbc.getWeiboyiTencents("weiboyiinfo", "腾讯微博", 100000);

			System.out.println(users.size());

			int count = 0;
			while (count < users.size() && !pool.isShutdown()) {
				try {
					pool.execute(new WeiboSpider(myJdbc, users.get(count), "tencentprice"));
				} catch (Exception e) {
					logger.error("Thread exception: " + Thread.currentThread().getName(), e);
					continue;
				}
				count++;
			}
			//			myJdbc.sqlClose();
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
