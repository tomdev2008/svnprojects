package cc.pp.sina.analysis.userweibo.corpus;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.sina.jdbc.UserWeiboJDBC;
import cc.pp.sina.utils.Nettool;

public class MultiThreadsUserWeibos {

	public static Logger logger = LoggerFactory.getLogger(MultiThreadsUserWeibos.class);

	/**
	 * 主函数
	 */
	public static void main(String[] args) throws InterruptedException, SQLException {

		MultiThreadsUserWeibos mtuw = new MultiThreadsUserWeibos();
		mtuw.multiThreads();

	}

	/**
	 * 多线程
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void multiThreads() throws SQLException, InterruptedException {

		final ThreadPoolExecutor pool = getThreadPoolExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				pool.shutdown();
			}
		}));

		UserWeiboJDBC myJdbc = new UserWeiboJDBC(Nettool.getServerLocalIp());
		//		UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");

		if (myJdbc.mysqlStatus()) {

			List<String> tokens = myJdbc.getAllTokens();
			List<String> sinausers = myJdbc.getSinaUsers("sinausers", 10000000);

			UserWeibosDao userWeibosDao = new UserWeibosDaoImpl(tokens);

			int count = 0;
			while ((count < sinausers.size()) && !pool.isShutdown()) {
				try {
					pool.execute(new UserWeibos(sinausers.get(count), userWeibosDao, myJdbc, "corpusweibos"));
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
