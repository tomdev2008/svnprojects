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

import cc.pp.sina.analysis.userweibo.MultiThreadsUserWeibos;
import cc.pp.sina.jdbc.UidJDBC;
import cc.pp.sina.utils.Nettool;

public class MultiThreadsUserBaseinfo {

	private static Logger logger = LoggerFactory.getLogger(MultiThreadsUserWeibos.class);

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws InterruptedException, SQLException {
		
		MultiThreadsUserBaseinfo multiThreadsUserBaseinfo = new MultiThreadsUserBaseinfo();
		multiThreadsUserBaseinfo.multiThreads();

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

		UidJDBC myJdbc = new UidJDBC(Nettool.getServerLocalIp());
		//		UidJDBC myJdbc = new UidJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {

			String sinauserstable = myJdbc.getLastTablename("tablename_sinausers");
			String baseinfotable = myJdbc.getLastTablename("tablename_sinauserbaseinfo");
			String sinaunusedtable = myJdbc.getLastTablename("tablename_sinaunuseduids");
			myJdbc.deleteLastTablename("tablename_sinausers", sinauserstable);
			myJdbc.deleteLastTablename("tablename_sinauserbaseinfo", baseinfotable);
			myJdbc.deleteLastTablename("tablename_sinaunuseduids", sinaunusedtable);
			List<String> tokens = myJdbc.getAllTokens();
			List<String> sinausers = myJdbc.getSinaUsers(sinauserstable, 14000000);
			UserBaseinfoDao userBaseinfoDao = new UserBaseinfoDaoImpl(tokens);

			int count = 0;
			while ((count < sinausers.size()) && !pool.isShutdown()) {
				try {
					pool.execute(new UserBaseInfo(sinausers.get(count), userBaseinfoDao, myJdbc, baseinfotable,
							sinaunusedtable));
				} catch (Exception e) {
					myJdbc.insertUnusedUsers(sinaunusedtable, sinausers.get(count));
					logger.error("Thread exception: " + Thread.currentThread().getName(), e);
					break;
				}
				count++;
				/**
				 * 采用均衡策略，每隔60万次的时候休息40分钟
				 */
				if (count % 600000 == 0) {
					Thread.sleep(40 * 60 * 1000);
				}
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
