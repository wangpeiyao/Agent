package cn.com.hnisi.type;

/**
 * 中间件类型
 * @author FengGeGe
 *
 */
public enum MiddlewareType {
	/**
	 * webLogic
	 */
	WEBLOGIC("weblogic"),
	/**
	 * webSphere
	 */
	WEBSPHERE("websphere");
	
	private final String value;
    
	 private MiddlewareType(String value){
		 this.value=value;
	 }
	 
	public String getValue() {
		return value;
	}
}
