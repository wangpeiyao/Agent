package cn.com.hnisi.model;

/**
 * 快捷标签对象
 * @author WenZhiFeng
 * 2018年2月4日
 */
public class FastLabelModel {

	private String id;
	private String name;
	private String lasttime;//最后修改时间
	private int clicks;//点击（使用）次数，使用次数多的排在前面
	private int serverCount;//标签对应的数量（非数据库字段）
	public FastLabelModel(){}
	public FastLabelModel(String id, String name,String lasttime) {
		super();
		this.id = id;
		this.name = name;
		this.lasttime=lasttime;
	}
	
	/**
	 * 如果ID相同则返回true
	 * @param labelModel
	 * @return
	 */
	public boolean compare(FastLabelModel labelModel){
		if(labelModel!=null){
			if(labelModel.getId()==this.id){
				return true;
			}
		}
		return false;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLasttime() {
		return lasttime;
	}
	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}
	public int getClicks() {
		return clicks;
	}
	public void setClicks(int clicks) {
		this.clicks = clicks;
	}
	public int getServerCount() {
		return serverCount;
	}
	public void setServerCount(int serverCount) {
		this.serverCount = serverCount;
	}
}
