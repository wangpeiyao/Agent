package cn.com.hnisi.type;

/**
 * 操作步骤杖举
 * @author FengGeGe
 *
 */
public enum StepType {
	/**
	 * 登录欢迎界面
	 */
	STEP(0), 
	/**
	 * 第1步：配置 应用服务器
	 */
	STEP1(1), 
	/**
	 * 第2步：应用发布
	 */
	STEP2(2);

    private final int value;
    
	 private StepType(int value){
		 this.value=value;
	 }
	 
	public int getValue() {
		return value;
	}
}
