package cc.pp.tencent.analysis.common;

import java.io.Serializable;

/**
 * 腾讯微博所有分析的abstract父类
 * @author Administrator
 *
 */
public abstract class Analysis implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	public String ip = "";
	
	public int apicount = 0;
	
	public Analysis(String ip) {
		this.ip = ip;
	}
	
	public abstract String[] analysis(String url);

	public String getIp() {
		return ip;
	}

}
