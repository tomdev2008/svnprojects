package cc.pp.price.cpm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import cc.pp.spider.constant.ParamsConstant;
import cc.pp.spider.sql.LocalJDBC;

public class CalcPrices {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		HashMap<String, String> tencentCates = ParamsConstant.getTencentCates();
		LocalJDBC myJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			BufferedReader br = new BufferedReader(new FileReader(new File("pricepic/price.txt")));
			String str = "";
			String[] arr;
			String[] temp;
			String domainname, cate;
			int averreadcount;
			float hardrepprice, hardoriprice, softrepprice, softoriprice;
			while ((str = br.readLine()) != null) {

				System.out.println(str);
				if (str.contains("dot")) {
					continue;
				}

				arr = str.split(",");
				domainname = arr[0];
				domainname = domainname.substring(domainname.indexOf("_") + 1, //
						domainname.indexOf("."));
				hardrepprice = Float.parseFloat(arr[1]);
				hardoriprice = Float.parseFloat(arr[2]);
				softrepprice = Float.parseFloat(arr[3]);
				softoriprice = Float.parseFloat(arr[4]);
				temp = myJdbc.getCateAndAvereadcount("tencentprice", domainname);
				cate = temp[0];
				if (tencentCates.get(temp[0]) != null) {
					cate = tencentCates.get(temp[0]);
				}
				averreadcount = Integer.parseInt(temp[1]);

				if (averreadcount == 0) {
					hardrepprice = 0.0f;
					hardoriprice = 0.0f;
					softrepprice = 0.0f;
					softoriprice = 0.0f;
				} else {
					hardrepprice = hardrepprice * 1000 / averreadcount;
					hardoriprice = hardoriprice * 1000 / averreadcount;
					softrepprice = softrepprice * 1000 / averreadcount;
					softoriprice = softoriprice * 1000 / averreadcount;
				}

				myJdbc.updatePrices("tencentprice", cate, domainname, hardrepprice,//
						hardoriprice, softrepprice, softoriprice);
			}
			br.close();

			myJdbc.sqlClose();
		}

	}

}
