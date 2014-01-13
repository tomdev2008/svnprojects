package cc.pp.sina.analysis.userfans;

import java.sql.SQLException;

import org.junit.Test;

import com.sina.weibo.model.WeiboException;


public class FansAnalysisTest {

	@Test
	public void fansTest() throws WeiboException, SQLException {
		FansAnalysis ua = new FansAnalysis("192.168.1.27");
		String uid = "1642635773";
		int index = ua.analysis(uid);
		System.out.println(index);
	}

}
