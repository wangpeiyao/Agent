package cn.com.hnisi.type;

/**
 * 客户端请求类型
 * @author FengGeGe
 *
 */
public enum RequestType {
	/**
	 * 请求命令代码:100-请求执行命令。
	 */
	COMMAND(100),
	/**
	 * 备份-101
	 */
	COMMAND_BACKUP(101),
	/**
	 * 启动-102
	 */
	COMMAND_START(102),
	/**
	 * 停止-103
	 */
	COMMAND_STOP(103),
	/**
	 * 请求命令代码:200-请求上传文件。
	 */
	UPLOAD_FILE(200),
	/**
	 * 测试
	 */
	TEST(300),
	/**
	 * 修改密码
	 */
	CHANGE_PASSWORD(400),
	/**
	 * 获取路径
	 */
	GET_PATH(500),
	/**
	 * 获取日志
	 */
	VIEW_LOGS(600),
	/**
	 * 下载文件
	 */
	DOWNLOAD_FILE(700),
	
	/**
	 * 删除文件
	 */
	DELETE_FILE(800);
	
	private int value;

	RequestType(int value) {
		this.value = value;
	}

	public int getValue() {
		// TODO 自动生成的方法存根
		return value;
	}
}
