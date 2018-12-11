package cn.com.hnisi.type;

/**
 * 前端显示
 * @author FengGeGe
 *
 */
public enum ShowType {
	RESPONSE("响应"),
	STATUS("状态"),
	ERROR("错误");
	private String value;

	ShowType(String value) {
		this.value = value;
	}

	public String getValue() {
		// TODO 自动生成的方法存根
		return value;
	}
}
