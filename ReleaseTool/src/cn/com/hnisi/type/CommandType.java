package cn.com.hnisi.type;

/**
 * 命令类型
 * @author FengGeGe
 *
 */
public enum CommandType {
	/**
	 * 备份
	 */
	BACKUP(0),
	/**
	 * 上传文件
	 */
	UPLOAD_FILE(1),
	/**
	 * 停止应用
	 */
	STOP(2),
	/**
	 * 启动应用
	 */
	START(3);

	
	private final int value;
    
	 private CommandType(int value){
		 this.value=value;
	 }
	 
	public int getValue() {
		return value;
	}
}
