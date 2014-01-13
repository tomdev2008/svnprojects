package cc.pp.weirenwu.core;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cc.pp.spider.sql.LocalJDBC;

public class MultiThreadsWeirenwu {

	/**
	 * 主函数
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws InterruptedException, SQLException {

		final ThreadPoolExecutor pool = getThreadPoolExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				pool.shutdown();
			}
		}));

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "root");
		myjdbc.mysqlStatus();

		WeiRenWuUtil weiRenWuUtil = new WeiRenWuUtil();

		for (int page = 1; page <= 962; page++) {
			if (!pool.isShutdown()) {
				pool.execute(new WeiRenWuRun(myjdbc, weiRenWuUtil, page));
			}
		}

		pool.shutdown();
		pool.awaitTermination(300, TimeUnit.SECONDS);
		//		myjdbc.sqlClose();

	}

	/**
	 * 申请线程池
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
						result.shutdown();
					}
				});
				return t;
			}
		});
		return result;
	}

}
