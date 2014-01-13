package cc.pp.tencent.require.ppdongli.single.weibo;

/**
 * 原创微博信息
 * @author Administrator
 *
 */
public class OriginalWeiboInfo {
	
	private String mid;        // 微博id
	private String createdat;    // 创建时间（秒）
	private String repostcount;   // 转发量
	private String commentcount;  // 评论量
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getCreatedat() {
		return createdat;
	}
	public void setCreatedat(String createdat) {
		this.createdat = createdat;
	}
	public String getRepostcount() {
		return repostcount;
	}
	public void setRepostcount(String repostcount) {
		this.repostcount = repostcount;
	}
	public String getCommentcount() {
		return commentcount;
	}
	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}
	
}
