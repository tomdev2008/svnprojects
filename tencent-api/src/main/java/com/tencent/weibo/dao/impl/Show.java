package com.tencent.weibo.dao.impl;

import com.tencent.weibo.constants.Result;

public class Show extends Result {

	private ShowData data;

	public ShowData getData() {
		return data;
	}

	public void setData(ShowData data) {
		this.data = data;
	}

}
