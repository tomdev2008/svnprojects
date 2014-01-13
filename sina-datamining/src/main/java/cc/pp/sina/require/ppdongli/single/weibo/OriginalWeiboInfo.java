package cc.pp.sina.require.ppdongli.single.weibo;

/**
 * 原创微博信息
 * @author Administrator
 *
 */
public class OriginalWeiboInfo {
	
	private String mid;        // 微博id
	private long createdat;    // 创建时间（秒）
	private int repostcount;   // 转发量
	private int commentcount;  // 评论量
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public long getCreatedat() {
		return createdat;
	}
	public void setCreatedat(long createdat) {
		this.createdat = createdat;
	}
	public int getRepostcount() {
		return repostcount;
	}
	public void setRepostcount(int repostcount) {
		this.repostcount = repostcount;
	}
	public int getCommentcount() {
		return commentcount;
	}
	public void setCommentcount(int commentcount) {
		this.commentcount = commentcount;
	}
}
