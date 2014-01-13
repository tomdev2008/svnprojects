package cc.pp.tencent.result;

import java.util.HashMap;
import java.util.Map;

/**
 * Title: 高级版用户分析结果类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class AdvancedUserResult {
	
	/*1、区域分析：province                        2、头像分析：ishead
	  3、性别分析：gender                          4、用户等级分析（按粉丝数）：fansclassfans
	  5、用户等级分析（按微博数）：fansclasswb         6、Top50粉丝UID（按粉丝数）：top50fans
	  7、Top50粉丝UID（按微博数）：top50wb           8、水军分析：quality
	  9、Top50水军：top50maskusers                10、认证比例：verifiedratio
	  11、用户等级分析（按微博创建时间）：fansclasstime 12、Top50粉丝UID（按微博创建时间）：top50time
	  13、平均每天发博量：averagewb                 14、Top50粉丝UID（按平均发博量）：top50avgwb
	  15、认证分布：verifiedtype                   16、用户等级分析（按活跃度）：fansclassactive
	  17、Top50粉丝UID（按活跃度）：top50active      18、该用户的活跃度：activation
	  19、该用户的影响力：influence                 20、存在的粉丝比例 ：existuids*/
	private String fansCount;                   //0、粉丝数量
	private String createlong;                  //00、微博创建天数；
	private Map<String, String> province;       //1、区域分析
	private Map<String, String> isHead;         //2、头像分析
	private Map<String, String> gender;         //3、性别分析
	private Map<String, String> gradeByFans;    //4、用户等级分析（按粉丝数）
	private Map<String, String> gradeByWb;      //5、用户等级分析（按微博数）
	private Map<String, String> topsByFans;     //6、Top50粉丝UID（按粉丝数）
	private Map<String, String> topsByWb;       //7、Top50粉丝UID（按微博数）
	private Map<String, String> quality;        //8、水军分析
	private Map<String, String> topsMaskers;    //9、Top50水军
	private Map<String, String> verifiedRatio;  //10、认证比例
	private Map<String, String> gradeByTime;    //11、用户等级分析（按微博创建时间）
	private Map<String, String> topsByTime;     //12、Top50粉丝UID（按微博创建时间）
	private String averageWeibos;               //13、平均每天发博量 
	private Map<String, String> topsByAvgwb;    //14、Top50粉丝UID（按平均发博量）
	private Map<String, String> verifiedType;   //15、认证分布
	private Map<String, String> gradeByActive;  //16、用户等级分析（按活跃度）
	private Map<String, String> topsByActive;   //17、Top50粉丝UID（按活跃度）
	private String activation;                  //18、该用户的活跃度
	private String influence;                   //19、该用户的影响力
	private String existuids;                  //20、存在的粉丝比例
	
	/**
	 * @ construct
	 */
	public AdvancedUserResult() {
		this.fansCount = new String();
		this.createlong = new String();
		this.province = new HashMap<String, String>();
		this.isHead = new HashMap<String, String>();
		this.gender = new HashMap<String, String>();
		this.gradeByFans = new HashMap<String, String>();
		this.gradeByWb = new HashMap<String, String>();
		this.topsByFans = new HashMap<String, String>();
		this.topsByWb = new HashMap<String, String>();
		this.quality = new HashMap<String, String>();
		this.topsMaskers = new HashMap<String, String>();
		this.verifiedRatio = new HashMap<String, String>();
		this.gradeByTime = new HashMap<String, String>();
		this.topsByTime = new HashMap<String, String>();
		this.averageWeibos = new String();
		this.topsByAvgwb = new HashMap<String, String>();
		this.verifiedType = new HashMap<String, String>();
		this.gradeByActive = new HashMap<String, String>();
		this.topsByActive = new HashMap<String, String>();
		this.activation = new String();
		this.influence = new String();
	}

	public void setFansCount(String fansCount) {
		this.fansCount = fansCount;
	}
	
	public String getFansCount() {
		return this.fansCount;
	}
	
	public void setCreateLong(String createlong) {
		this.createlong = createlong;
	}

	public String getCreateLong() {
		return this.createlong;
	}
	
	public Map<String, String> getProvince() {
		return this.province;
	}

	public Map<String, String> getIsHead() {
		return this.isHead;
	}
	
	public Map<String, String> getGender() {
		return this.gender;
	}

	public Map<String, String> getGradeByFans() {
		return this.gradeByFans;
	}

	public Map<String, String> getGradeByWb() {
		return this.gradeByWb;
	}
	
	public Map<String, String> getTopsByFans() {
		return this.topsByFans;
	}
	
	public Map<String, String> getTopsByWb() {
		return this.topsByWb;
	}

	public Map<String, String> getQuality() {
		return this.quality;
	}
	
	public Map<String, String> getTopsMaskers() {
		return this.topsMaskers;
	}
	
	public Map<String, String> getVerifiedRatio() {
		return this.verifiedRatio;
	}

	public Map<String, String> getGradeByTime() {
		return this.gradeByTime;
	}
	
	public Map<String, String> getTopsByTime() {
		return this.topsByTime;
	}
	
	public void setAverageWeibos(String averageWeibos) {
		this.averageWeibos = averageWeibos;
	}
	
	public String getAverageWeibos() {
		return this.averageWeibos;
	}

	public Map<String, String> getTopsByAvgwb() {
		return this.topsByAvgwb;
	}
	
	public Map<String, String> getVerifiedType() {
		return this.verifiedType;
	}
	
	public Map<String, String> getGradeByActive() {
		return this.gradeByActive;
	}
	
	public Map<String, String> getTopsByActive() {
		return this.topsByActive;
	}
	
	public void setActivation(String activation) {
		this.activation = activation;
	}
	
	public String getActivation() {
		return this.activation;
	}
	
	public void setInfluence(String influence) {
		this.influence = influence;
	}

	public String getInfluence() {
		return this.influence;
	}
	
	public void setExistuids(String existuids) {
		this.existuids = existuids;
	}
	
	public String getExistuids() {
		return existuids;
	}
	
}


