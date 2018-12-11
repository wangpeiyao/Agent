package cn.com.hnisi.model;

/**
 * 快捷标签应用对照对象
 * @author WenZhiFeng
 * 2018年2月4日
 */
public class FastLabelServerModel {
	
	private String id;
	private String label_id;//标签表主键
	private String server_id;//应用表主键
	public FastLabelServerModel(){}
	public FastLabelServerModel(String id, String label_id, String server_id) {
		super();
		this.id = id;
		this.label_id = label_id;
		this.server_id = server_id;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel_id() {
		return label_id;
	}
	public void setLabel_id(String label_id) {
		this.label_id = label_id;
	}
	public String getServer_id() {
		return server_id;
	}
	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}
}
