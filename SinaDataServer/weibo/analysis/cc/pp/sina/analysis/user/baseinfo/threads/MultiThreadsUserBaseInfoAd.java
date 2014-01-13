package cc.pp.sina.analysis.user.baseinfo.threads;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.sina.jdbc.UidJDBC;
import cc.pp.sina.utils.Nettool;

public class MultiThreadsUserBaseInfoAd {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsUserBaseInfoAd.class);

	/**
	 * @param args
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws InterruptedException, SQLException {

		MultiThreadsUserBaseInfoAd multiThreadsUserBaseinfoAd = new MultiThreadsUserBaseInfoAd();
		multiThreadsUserBaseinfoAd.multiThreads();

	}

	/**
	 * 多线程
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	public void multiThreads() throws InterruptedException {

		final ThreadPoolExecutor pool = getThreadPoolExecutor();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				pool.shutdown();
			}
		}));

		UidJDBC myJdbc = new UidJDBC(Nettool.getServerLocalIp());
		myJdbc.mysqlStatus();

		List<String> tokens = null;
		try {
			tokens = myJdbc.getAllTokens();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.info("Sql occurs error.", e);
		}

		UserBaseinfoDao userBaseinfoDao = new UserBaseinfoDaoImpl(tokens);

		List<String> sinausers = null;

		for (int i = 0; i < 32; i++) {

			System.out.println("Read at: " + i);

			String sinauserstable = "sinausers_" + i;
			String baseinfotable = "sinauserbaseinfo_" + i;
			String sinaunusedtable = "sinaunuseduids_" + i;
			try {
				sinausers = myJdbc.getSinaUsers(sinauserstable, 1000_0000);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.info("Sql occurs error.", e);
			}

			int count = 0;
			while ((count < sinausers.size()) && !pool.isShutdown()) {
				try {
					pool.execute(new UserBaseInfo(sinausers.get(count), userBaseinfoDao, myJdbc, baseinfotable,
							sinaunusedtable));
				} catch (Exception e) {
					try {
						myJdbc.insertUnusedUsers(sinaunusedtable, sinausers.get(count));
					} catch (SQLException e1) {
						e1.printStackTrace();
						logger.info("Sql occurs error.", e1);
					}
					logger.error("Thread exception: " + Thread.currentThread().getName(), e);
					break;
				}
				count++;
			}

		}

		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.SECONDS);
		//			myJdbc.sqlClose();
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
