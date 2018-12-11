package cn.com.hnisi.type;

/**
 * 处理状态
 * @author FengGeGe
 *
 */
public enum ProcessStatus {
	/**
	 * 已完成
	 */
	Completed(0),
	/**
	 * 运行中
	 */
	Run(1);
	
	private int value;

	ProcessStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		// TODO 自动生成的方法存根
		return value;
	}
}
