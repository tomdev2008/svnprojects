package cc.pp.sina.analysis.userclassfy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cc.pp.e.bozhu.Bozhu;
import cc.pp.e.bozhu.bozhuDao;
import cc.pp.sina.analysis.userclassfy.dao.UserAllParams;
import cc.pp.sina.jdbc.LocalJDBC;
import cc.pp.sina.jdbc.UserClassfyJDBC;

public class UnionData {

	private String ip = "";

	public UnionData(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {

		UnionData unionData = new UnionData("192.168.1.51");
		//		unionData.UnionResult();
		unionData.transResult();

	}

	/**
	 * 使用奥林的dao，转成皮皮动力数据表
	 * @throws SQLException 
	 */
	public void transResult() throws SQLException {

		LocalJDBC myJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			bozhuDao bozhudao = new bozhuDao(
					"jdbc:mysql://127.0.0.1:3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8", "root", "");
			bozhudao.connectDb();
			for (int i = 0; i < 28; i++) {
				System.out.println(i);
				List<Bozhu> result = myJdbc.getUserResult("sinalibrary", i * 10000, (i + 1) * 10000);
				bozhudao.bashWriteBozhu(result);
			}
			bozhudao.closeDb();

			myJdbc.sqlClose();
		}
	}

	public void UnionResult() throws SQLException {

		UserClassfyJDBC ucJdbc = new UserClassfyJDBC(ip);
		if (ucJdbc.mysqlStatus()) {
			for (int ips = 36; ips < 45; ips++) {

				System.out.println(ips);

				UserClassfyJDBC tucjdbc = new UserClassfyJDBC("192.168.1." + ips);
				if (tucjdbc.mysqlStatus()) {
					List<UserAllParams> tresult = tucjdbc.getUserResult("sinalibrary");
					for (int i = 0; i < tresult.size(); i++) {
						UserAllParams temp = tresult.get(i);
						ucJdbc.insertUsersParams("sinalibrary_all", temp.getUsername(), temp.getNickname(),
								temp.getDescription(), temp.getFanscount(), temp.getWeibocount(), temp.getAveragewbs(),
								temp.getInfluence(), temp.getActivation(), temp.getActivecount(), temp.getAddvratio(),
								temp.getActiveratio(), temp.getMaleratio(), temp.getFansexistedratio(),
								temp.getVerify(), temp.getAllfanscount(), temp.getAllactivefanscount(),
								temp.getTop5provinces(), temp.getOriratio(), temp.getAveorirepcom(),
								temp.getAverepcom(), temp.getWbsource(), temp.getAverepcombyweek(),
								temp.getAverepcombymonth(), temp.getAvereposterquality(), temp.getAveexposionsum(),
								temp.getValidrepcombyweek(), temp.getValidrepcombymonth(), temp.getUsertags());
					}
					tucjdbc.sqlClose();
				}
			}
			ucJdbc.sqlClose();
		}
	}

	public void UnionResult1() throws SQLException {

		UserClassfyJDBC ucJdbc = new UserClassfyJDBC(ip);
		String info = new String();
		if (ucJdbc.mysqlStatus()) {
			for (int ips = 184; ips < 190; ips++) {
				System.out.println(ips);
				UserClassfyJDBC tucjdbc = new UserClassfyJDBC("192.168.1." + ips);
				if (tucjdbc.mysqlStatus()) {
					HashMap<String, String> tresult = tucjdbc.getResult("sinalibrary");
					for (Entry<String, String> temp : tresult.entrySet()) {
						String username = temp.getKey();
						info = temp.getValue();
						String nickname = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String description = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String usertags = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String fanscount = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String weibocount = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String averagewbs = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String influence = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String activation = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String activecount = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String addvratio = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String activeratio = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String maleratio = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String fansexistedratio = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String verify = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String allfanscount = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String allactivefanscount = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String top5provinces = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String oriratio = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String aveorirepcom = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String averepcom = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String wbsource = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String averepsbyweek = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String averepsbymonth = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String validrepcombyweek = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String validrepcombymonth = info.substring(0, info.indexOf("&="));
						info = info.substring(info.indexOf("&=") + 2);
						String avereposterquality = info.substring(0, info.indexOf("&="));
						String aveexposionsum = info.substring(info.indexOf("&=") + 2);

						ucJdbc.insertUsersParams("sinalibrary_all", username, nickname, description,
								Integer.parseInt(fanscount), Integer.parseInt(weibocount),
								Float.parseFloat(averagewbs), Integer.parseInt(influence),
								Integer.parseInt(activation), Integer.parseInt(activecount),
								Float.parseFloat(addvratio), Float.parseFloat(activeratio),
								Float.parseFloat(maleratio), Float.parseFloat(fansexistedratio),
								Integer.parseInt(verify), Long.parseLong(allfanscount),
								Long.parseLong(allactivefanscount), top5provinces, Float.parseFloat(oriratio),
								Float.parseFloat(aveorirepcom), Float.parseFloat(averepcom), wbsource,
								Float.parseFloat(averepsbyweek), Float.parseFloat(averepsbymonth),
								Float.parseFloat(avereposterquality), Long.parseLong(aveexposionsum),
								Float.parseFloat(validrepcombyweek), Float.parseFloat(validrepcombymonth), usertags);
						;
					}
					tucjdbc.sqlClose();
				}
			}
			ucJdbc.sqlClose();
		}

	}

}
