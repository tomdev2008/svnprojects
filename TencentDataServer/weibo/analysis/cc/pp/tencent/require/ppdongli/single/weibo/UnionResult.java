package cc.pp.tencent.require.ppdongli.single.weibo;

/**
 * 综合结果
 * @author Administrator
 *
 */
public class UnionResult {
	
	private OriginalWeiboInfo oriwbinfo = new OriginalWeiboInfo();
	private OriginalUserInfo oriuserinfo = new OriginalUserInfo();
	private RepostedInfo repinfo = new RepostedInfo();
	
	public OriginalWeiboInfo getOriwbinfo() {
		return oriwbinfo;
	}
	public void setOriwbinfo(OriginalWeiboInfo oriwbinfo) {
		this.oriwbinfo = oriwbinfo;
	}
	public OriginalUserInfo getOriuserinfo() {
		return oriuserinfo;
	}
	public void setOriuserinfo(OriginalUserInfo oriuserinfo) {
		this.oriuserinfo = oriuserinfo;
	}
	public RepostedInfo getRepinfo() {
		return repinfo;
	}
	public void setRepinfo(RepostedInfo repinfo) {
		this.repinfo = repinfo;
	}

}
