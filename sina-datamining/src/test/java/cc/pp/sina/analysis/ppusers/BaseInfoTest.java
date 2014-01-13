package cc.pp.sina.analysis.ppusers;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

public class BaseInfoTest {

	@Test
	public void batchBaseInfo() throws WeiboException {
		String accesstoken = "2.00dJQV5BdcZIJC64cd8076acaz9VGE";
		List<String> uids = new ArrayList<String>();
		uids.add("2211047387");
		uids.add("1862087393");
		BaseInfo baseinfo = new BaseInfo("sinauserinfo", "sinausers", "sinaunusedusers");
		List<User> usersinfo = baseinfo.batchBaseinfo(accesstoken, uids);
		System.out.println(usersinfo.get(0).getDescription());
		assertEquals(2, usersinfo.size());
	}

	@Ignore
	public void test() throws SQLException {
		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			myjdbc.deleteTable("ppsinausers");
			myjdbc.sqlClose();
		}
	}

}
