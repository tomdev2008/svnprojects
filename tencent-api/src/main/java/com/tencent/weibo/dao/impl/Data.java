package com.tencent.weibo.dao.impl;

import java.util.List;

public class Data {

	private long timestamp;
	private List<SimpleUser> info;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public List<SimpleUser> getInfo() {
		return info;
	}

	public void setInfo(List<SimpleUser> info) {
		this.info = info;
	}

}
