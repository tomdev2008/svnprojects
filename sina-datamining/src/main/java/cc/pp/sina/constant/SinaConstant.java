package cc.pp.sina.constant;

/**
 * Title: 新浪相关参数信息
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class SinaConstant {

	private static final String[] VERIFIED_TYPE = { "名人", "政府", "蓝V-企业", "媒体", "校园", "蓝V-网站", "应用", "蓝V-团体（机构）",
			"待审企业", "初级达人", "通过审核的达人", "已故V用户", "普通用户", "微博女郎" };

	public static String getType(int value) {
		if (value == 200) {
			return VERIFIED_TYPE[9];
		} else if (value == 220) {
			return VERIFIED_TYPE[10];
		} else if (value == 400) {
			return VERIFIED_TYPE[11];
		} else if (value == -1) {
			return VERIFIED_TYPE[12];
		} else if (value == 10) {
			return VERIFIED_TYPE[13];
		} else if (value < 9) {
			return VERIFIED_TYPE[value];
		} else {
			return "未知";
		}
	}


}
