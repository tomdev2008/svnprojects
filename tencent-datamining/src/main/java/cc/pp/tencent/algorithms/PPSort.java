package cc.pp.tencent.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: 对Map进行排序工具
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class PPSort {
	
	/**
	 * 将HashMap排序成String
	 * @param temp
	 * @return
	 */
	public static String sortedToString(HashMap<String,Integer> temp) {
		
		List<Map.Entry<String,Integer>> resulttemp = new ArrayList<Map.Entry<String,Integer>>(temp.entrySet());
		Collections.sort(resulttemp, new Comparator<Map.Entry<String,Integer>>(){
			public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		
		String sortedtemp = new String();
		for (int k = 0; k < temp.size(); k++) {
			sortedtemp += resulttemp.get(k).toString() + ",";
		}
		
		return sortedtemp;
	}

	/**
	 * 将HashMap排序成String[]
	 * @param temp
	 * @return
	 */
	public static String[] sortedToStrings(HashMap<String,Integer> temp) {
		
		List<Map.Entry<String,Integer>> resulttemp = new ArrayList<Map.Entry<String,Integer>>(temp.entrySet());
		Collections.sort(resulttemp, new Comparator<Map.Entry<String,Integer>>(){
			public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		
		String[] sortedtemp = new String[temp.size()];
		for (int k = 0; k < temp.size(); k++) {
			sortedtemp[k] = resulttemp.get(k).toString();
		}
		
		return sortedtemp;
	}
	
	/**
	 * 将HashMap排序成HashMap
	 * @param temp
	 * @return
	 */
	public static HashMap<String,String> sortedToHashMap(HashMap<String,Integer> temp) {
		
		List<Map.Entry<String,Integer>> resulttemp = new ArrayList<Map.Entry<String,Integer>>(temp.entrySet());
		Collections.sort(resulttemp, new Comparator<Map.Entry<String,Integer>>(){
			public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		
		HashMap<String,String> sortedtemp = new HashMap<String,String>();
		for (int k = 0; k < temp.size(); k++) {
			sortedtemp.put(Integer.toString(k), resulttemp.get(k).toString());
		}
		
		return sortedtemp;
	}	
	
	/**
	 * 将HashMap排序，并提取前num个元素
	 * @param temp：待排序的Map
	 * @param keyname1：键名1
	 * @param keyname2：键名2
	 * @param num：提取的排序数
	 * @return
	 */
	public static HashMap<String,HashMap<String,String>> sortedToDoubleMap(HashMap<String,Integer> temp, 
			String keyname1, String keyname2, int num) {
		
		List<Map.Entry<String,Integer>> resulttemp = new ArrayList<Map.Entry<String,Integer>>(temp.entrySet());
		Collections.sort(resulttemp, new Comparator<Map.Entry<String,Integer>>(){
			public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});
		
		HashMap<String,HashMap<String,String>> sortedtemp = new HashMap<String,HashMap<String,String>>();
		String info = new String();
		String key = new String();
		String value = new String();
		int max = (temp.size() > num) ? num : temp.size();
		for (int k = 0; k < max; k++) {
			HashMap<String,String> t = new HashMap<String,String>();
			info = resulttemp.get(k).toString();
			key = info.substring(0, info.indexOf("="));
			value = info.substring(info.indexOf("=") + 1);
			t.put(keyname1, key);
			t.put(keyname2, value);
			sortedtemp.put(Integer.toString(k), t);
		}
		
		return sortedtemp;
	}
	
	/**
	 * 针对内容库使用的排序：每个小时内的栏目进行排序
	 * @param temp24h
	 * @param columname
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap<String,List<HashMap>> getTop10PeerHour(int[][] temp24h, 
			HashMap<String,String> columname) {
		
		HashMap<String,List<HashMap>> result = new HashMap<String,List<HashMap>>();
		for (int i1 = 0; i1 < 24; i1++) {
			String[] table = new String[200];
			for (int i2 = 0; i2 < 200; i2++) {
				table[i2] = "0=0";
			}
			for (int i2 = 0; i2 < 200; i2++) {
				if (temp24h[i1][i2] != 0) {
					table = InsertSort.toptable(table, columname.get(Integer.toString(i2)) + "=" + temp24h[i1][i2]);
				}
			}
			List<HashMap> list = new ArrayList<HashMap>(); 
			int temp1 = 0;
			for (int i2 = 0; i2 < 200; i2++) {
				HashMap<String,String> top10 = new HashMap<String,String>();
				if (table[i2].equals("0=0") || (temp1 > 10)) {
					break;
				} else {
					top10.put("colum", table[i2].substring(0, table[i2].indexOf("=")));
					top10.put("count", table[i2].substring(table[i2].indexOf("=") + 1));
				}
				temp1++;
				list.add(top10);
			}
			result.put(Integer.toString(i1), list);
		}
		
		return result;
	}
	
	/**
	 * 找出一个数组中的最大值对应的下表
	 * @param array
	 * @return
	 */
	public static int getMaxId(int[] array) {

		int max = 0, index = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
				index = i;
			}
		}

		return index;
	}

	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		HashMap<String,Integer> temp = new HashMap<String,Integer>();
		temp.put("aaaa", 12);
		temp.put("aw", 2);
		temp.put("af", 134);
		HashMap<String,HashMap<String,String>> result = PPSort.sortedToDoubleMap(temp, "key", "value", 10);
		System.out.println(result);
	}
}