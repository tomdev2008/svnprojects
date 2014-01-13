package com.tencent.weibo.dao.impl;

import com.tencent.weibo.constants.Result;

public class UserTimeline extends Result {
	
	private UserTimelineData data;

	public UserTimelineData getData() {
		return data;
	}

	public void setData(UserTimelineData data) {
		this.data = data;
	}

}
