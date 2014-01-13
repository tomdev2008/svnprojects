package cc.pp.tencent.analysis.username.threads;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.tencent.jdbc.UsernameJDBC;

public class MultiThreadsUsername {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsUsername.class);

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

		//		UsernameJDBC myJdbc = new UsernameJDBC(Nettool.getServerLocalIp());
		UsernameJDBC myJdbc = new UsernameJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {

			List<String> tokens = myJdbc.getAllTokens();
			List<String> tencentusers = myJdbc.getTencentUsers("tencentusers", 5000);
			UsernameDao usernameDao = new UsernameDaoImpl(tokens);

			int count = 0;
			while (count < tencentusers.size() && !pool.isShutdown()) {
				try {
					pool.execute(new FansAndFriends(tencentusers.get(count), usernameDao, myJdbc));
				} catch (Exception e) {
					myJdbc.insertUnusedUsers("tencentunuseduids", tencentusers.get(count));
					logger.error("Thread exception: " + Thread.currentThread().getName(), e);
					break;
				}
				count++;
			}

			Thread.sleep(1000 * 600);
			System.out.println(usernameDao.getApicount());
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
