package cc.pp.spider.main;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cc.pp.spider.constant.ParamsConstant;
import cc.pp.spider.sql.LocalJDBC;

public class MultiThreads {

	/**
	 * 主函数
	 */
	public static void main(String[] args) throws Exception {

		MultiThreads multiThreads = new MultiThreads();
		multiThreads.multiThreads();

	}

	/**
	 * 多线程
	 */
	public void multiThreads() throws SQLException, InterruptedException {

		final ThreadPoolExecutor pool = getThreadPoolExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				pool.shutdown();
			}
		}));

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "root");
		if (myjdbc.mysqlStatus()) {
			String[] types = ParamsConstant.WEIBO_TYPE_FILTER;
			//			String[] types = { "腾讯微博" };
			String[] cates = ParamsConstant.CONTENT_CATEGORIES;
			for (String type : types) {
				for (String cate : cates) {
					if (!pool.isShutdown()) {
						pool.execute(new Spider(myjdbc, type, cate, "weiboyiinfo"));
					}
				}
			}
			/**
			 * 注意：要么每个线程各自使用自己的数据库连接，要么所有线程共享一个连接时数据库不能关闭
			 */
			pool.shutdown();
			pool.awaitTermination(300, TimeUnit.SECONDS);
			//			myjdbc.sqlClose();
		}

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
