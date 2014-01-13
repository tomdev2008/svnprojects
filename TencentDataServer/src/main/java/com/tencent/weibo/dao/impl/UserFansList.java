package com.tencent.weibo.dao.impl;

import com.tencent.weibo.constants.Result;

public class UserFansList extends Result {

	private UserFansData data;

	public UserFansData getData() {
		return data;
	}

	public void setData(UserFansData data) {
		this.data = data;
	}

}
