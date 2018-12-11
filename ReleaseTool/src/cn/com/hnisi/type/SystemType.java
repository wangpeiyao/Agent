package cn.com.hnisi.type;

/**
 * 系统类型
 * @author FengGeGe
 *
 */
public enum SystemType {
	/**
	 * AIX系统
	 */
	AIX("aix"),
	/**
	 * WINDOWS系统
	 */
	WINDOWS("windows");
	
	private final String value;
    
	 private SystemType(String value){
		 this.value=value;
	 }
	 
	public String getValue() {
		return value;
	}
}
