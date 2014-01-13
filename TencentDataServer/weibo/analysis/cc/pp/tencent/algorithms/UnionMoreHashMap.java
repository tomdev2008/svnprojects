package cc.pp.tencent.algorithms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Title: 合并HashMap
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UnionMoreHashMap {

	/**
	 * 合并多层HashMap为但层HashMap，如
	 *        HashMap<String,HashMap<String,Integer>>
	 *   ---> HashMap<String,Integer>
	 * @param table
	 * @return
	 */
	public static HashMap<String,Integer> unionHashMap(HashMap<String,HashMap<String,Integer>> table) {

		HashMap<String,Integer> result = new HashMap<String,Integer>();
		for (int i = 0; i < table.size(); i++) {
			Set<String> keys = table.get(Integer.toString(i)).keySet();
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				String temp = (String) iterator.next();
				if (result.get(temp) == null) {
					result.put(temp, table.get(Integer.toString(i)).get(temp));
				} else {
					result.put(temp, result.get(temp) + table.get(Integer.toString(i)).get(temp));
				}
			}
		}

		return result;
	}

	/**
	 * 合并HashMap,key不是数字
	 * @param map1
	 * @param map2
	 * @return
	 */
	public HashMap<String,String> unionHashMap(HashMap<String,String> map1, 
			HashMap<String,String> map2, HashMap<String,String> map3, 
			HashMap<String,String> map4, HashMap<String,String> map5) {

		HashMap<String,String> result = map1;
		for (Entry<String,String> temp : map2.entrySet()) {
			result.put(temp.getKey(), temp.getValue());
		}
		for (Entry<String,String> temp : map3.entrySet()) {
			result.put(temp.getKey(), temp.getValue());
		}
		for (Entry<String,String> temp : map4.entrySet()) {
			result.put(temp.getKey(), temp.getValue());
		}
		for (Entry<String,String> temp : map5.entrySet()) {
			result.put(temp.getKey(), temp.getValue());
		}

		return result;
	}
	
	/**
	 * 合并HashMap,key是数字
	 * @param map1
	 * @param map2
	 * @return
	 */
	public HashMap<String,String> unionHashMapNum(HashMap<String,String> map1, 
			HashMap<String,String> map2, HashMap<String,String> map3, 
			HashMap<String,String> map4, HashMap<String,String> map5) {

		HashMap<String,String> result = map1;
		int i = result.size();
		for (Entry<String,String> temp : map2.entrySet()) {
			result.put(Integer.toString(i++), temp.getValue());
		}
		for (Entry<String,String> temp : map3.entrySet()) {
			result.put(Integer.toString(i++), temp.getValue());
		}
		for (Entry<String,String> temp : map4.entrySet()) {
			result.put(Integer.toString(i++), temp.getValue());
		}
		for (Entry<String,String> temp : map5.entrySet()) {
			result.put(Integer.toString(i++), temp.getValue());
		}

		return result;
	}

	/**
	 * 求Map中value总和
	 * @param table
	 * @return
	 */
	public static int getSum(HashMap<String,Integer> table) {

		int sum = 0;
		for (Entry<String,Integer> temp : table.entrySet()) {
			sum += temp.getValue();
		}

		return sum;
	}

	/**
	 * 求Map中value值大于num的元素个数、元素值总和
	 * @param table
	 * @param num
	 * @return
	 */
	public static int[] getRobots(HashMap<String,Integer> table, int num) {

		int[] result = new int[2];
		for (Entry<String,Integer> temp : table.entrySet()) {
			if (temp.getValue() >= num) {
				result[0]++;
				result[1] += temp.getValue(); 
			}
		}

		return result;
	}

	/**
	 * 每小时真实用户比例，真实用户使用的内容比例
	 * @param table
	 * @param num
	 * @return
	 */
	public static String[] getRobotRatio(HashMap<String,Integer> table, int num) {

		String[] result = new String[2];
		int[] sum = new int[2];
		int[] hour = new int[2];
		sum[0] = table.size();
		for (Entry<String,Integer> temp : table.entrySet()) {
			sum[1] += temp.getValue();
			if (temp.getValue() < num) {
				hour[0]++;
				hour[1] += temp.getValue(); 
			}
		}
		result[0] = Float.toString((float)(Math.round(((float)hour[0]/sum[0])*10000))/100) + "%";
		result[1] = Float.toString((float)(Math.round(((float)hour[1]/sum[1])*10000))/100) + "%";

		return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String,HashMap<String,Integer>> table = new HashMap<String,HashMap<String,Integer>>();
		HashMap<String,Integer> map1 = new HashMap<String,Integer>();
		map1.put("11", 12);
		map1.put("22", 22);
		HashMap<String,Integer> map2 = new HashMap<String,Integer>();
		map2.put("111", 122);
		map2.put("22", 2);
		table.put("0", map1);
		table.put("1", map2);
		System.out.println(table);
		HashMap<String,Integer> result = UnionMoreHashMap.unionHashMap(table);
		System.out.println(result);
		String[] robot = UnionMoreHashMap.getRobotRatio(result, 110);
		System.out.println(robot[0]);
		System.out.println(robot[1]);
	}

}

