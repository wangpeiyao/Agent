package cn.com.hnisi.type;

/**
 *  应用类型   0-默认（普通应用）； 1-集群总控制端； 2-集群机器
 * 
 * @author FengGeGe
 * 
 */
public enum ServerType {

	/**
	 * 默认（普通应用）
	 */
	DEFAULT(0),
	/**
	 * 集群总控制端
	 */
	ADMINSERVER(1),
	/**
	 * 集群机器
	 */
	CLUSTER_SERVICES(2);
	private final int value;

	private ServerType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
