package cc.pp.sina.algorithms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Title: HashMap转换工具
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TransMap {
	
	/**
	 * 把Map中数据求比例，使用Iterator
	 * @param table
	 * @return
	 */
	public static HashMap<String,String> tranToRatio(HashMap<String,Integer> table) {
		
		HashMap<String,String> result = new HashMap<String,String>();
		int sum = 0;
		Set<String> keys = table.keySet();
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			sum += table.get(iterator.next());
		}
		iterator = keys.iterator();
		while (iterator.hasNext()) {
			String temp = (String) iterator.next();
			result.put(temp, Float.toString((float)(Math.round(((float)table.get(temp)/sum)*10000))/100) + "%");
		}

		return result;
	}
	
	/**
	 * 把Map中数据求比例，使用Entry
	 * @param table
	 * @return
	 */
	public static HashMap<String,String> mapToRatio(HashMap<String,Integer> table) {
		
		HashMap<String,String> result = new HashMap<String,String>();
		int sum = 0;
		// 求和
		for (Entry<String,Integer> str : table.entrySet()) {
			sum += str.getValue();
		}
		// 求比率，并插入结果
		for (Entry<String,Integer> str : table.entrySet()) {
			result.put(str.getKey(), Float.toString((float)(Math.round(((float)str.getValue()/sum)*10000))/100) + "%");
		}
		
		return result;
	}
	
	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String,Integer> test = new HashMap<String,Integer>();
		test.put("first", 10);
		test.put("second", 40);
		test.put("third", 50);
		System.out.println(TransMap.tranToRatio(test));
		System.out.println(TransMap.mapToRatio(test));
	}

}
