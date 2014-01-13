package cc.pp.sina.analysis.userweibo.corpus;

import java.util.List;

import com.sina.weibo.model.Status;

public interface UserWeibosDao {

	List<Status> getUserWeibo(String uid);
}
