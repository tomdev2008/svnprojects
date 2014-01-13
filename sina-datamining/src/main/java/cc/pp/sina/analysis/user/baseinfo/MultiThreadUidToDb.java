package cc.pp.sina.analysis.user.baseinfo;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.sina.analysis.userweibo.MultiThreadsUserWeibos;

public class MultiThreadUidToDb {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsUserWeibos.class);

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, SQLException {

		final ThreadPoolExecutor pool = getThreadPoolExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				pool.shutdown();
			}
		}));

		int index = 0;
		while ((index < 20) && !pool.isShutdown()) {
			try {
				pool.execute(new UidToDb("sina_uid_" + index++ + ".txt"));
			} catch (Exception e) {
				e.printStackTrace();
			}
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
