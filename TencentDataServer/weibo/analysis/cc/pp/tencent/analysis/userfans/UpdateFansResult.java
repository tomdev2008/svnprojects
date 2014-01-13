package cc.pp.tencent.analysis.userfans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import net.sf.json.JSONArray;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import cc.pp.tencent.jdbc.UserJDBC;
import cc.pp.tencent.result.FansResult;

public class UpdateFansResult implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private String fromip = "";
	private String toip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public UpdateFansResult(String fromip, String toip) {
		this.fromip = fromip;
		this.toip = toip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SQLException, IOException {

		UpdateFansResult ufr = new UpdateFansResult("192.168.1.154", "192.168.1.27");
		ufr.updateResult();

	}

	/**
	 * 数据更新函数
	 * @throws SQLException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public void updateResult() throws SQLException, JsonParseException, JsonMappingException, IOException {

		HashMap<String, String> result = new HashMap<String, String>();
		UserJDBC mysql = new UserJDBC(this.fromip);
		if (mysql.mysqlStatus()) {
			result = mysql.getResult("tencentfansresult");
			UserJDBC myjdbc = new UserJDBC(this.toip);
			if (myjdbc.mysqlStatus()) {
				//				int i = 0;
				for (Entry<String, String> temp : result.entrySet()) {

					//					System.out.println(i++);

					String username = temp.getKey();
					String fansresult = temp.getValue();
					HashMap<String, String> fansaddtimeline = myjdbc.getFansAddInfo(username);
					String fanscount = myjdbc.getLastFansCount(username);
					if ((fansaddtimeline.size() > 0) && (fanscount != null)) {
						ObjectMapper mapper = new ObjectMapper();
						FansResult userdata = mapper.readValue(fansresult.substring(1, fansresult.length() - 1),
								FansResult.class);
						int oldactivefanssum = Integer.parseInt(userdata.getActivefanssum());
						int oldafanssum = Integer.parseInt(userdata.getFanssum());
						int activefanssum = Math.round((float) oldactivefanssum * Integer.parseInt(fanscount)
								/ oldafanssum);
						userdata.setFanssum(fanscount);
						userdata.setActivefanssum(Integer.toString(activefanssum));
						userdata.setFansaddtimeline(fansaddtimeline);
						JSONArray jsondata = JSONArray.fromObject(userdata);
						mysql.updateResult(username, jsondata.toString());
					}
				}
				myjdbc.sqlClose();
			}
			mysql.sqlClose();
		}
	}

}

