package cc.pp.sina.nettools;

import java.net.InetAddress;
import java.net.UnknownHostException;

import cc.pp.sina.utils.Nettool;

public class NettoolTest {

	InetAddress myip = null;
	InetAddress myserver = null;
	String hostaddress = null;

	/**
	 * @param args
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException {

		System.out.println("服务器局域网IP是：" + Nettool.getServerLocalIp());
		//		System.out.println("我的主机IP是：" + Nettool.getMyip());
		//		System.out.println("百度服务器IP是：" + Nettool.getMyServer("www.baidu.com"));

	}

}

