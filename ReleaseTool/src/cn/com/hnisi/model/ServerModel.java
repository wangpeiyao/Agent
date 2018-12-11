package cn.com.hnisi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 应用对象模型类
 * @author FengGeGe
 *
 */
/**
 * @author FengGeGe
 *
 */
public class ServerModel  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 682524532701660527L;
	

	private String id;
	private String groupId;
	private String groupName;
	private String name;
	private String serverName;
	private String serverIp;
	private String domainPort;
	private String systemTypeId;//系统类型ID
	private String systemType;//系统类型名称
	private String middlewareTypeId;//中间件类型ID
	private String middlewareType;//中间件类型名称
	private String domainPath;//
	private String backUpPath;//备份路径
	private String appPath;//项目路径
	private String appVerification;
	private String winRarPath;
	private String agentPath;
	private String agentPort;
	private String agentPassword;
	private String status;
	private String remark;
	private String backupType;//备份方式: 0-复制文件夹；1-压缩
	private String releaseType;//发布类型:0 -发布应用；1-文件上传
	private String username;//管理账号
	private String password;//管理账号密码
	private String serverType;//应用类型；0-默认(普通应用); 1-总控制; 2-集群机器
	private String isAdminServer;//暂不使用到
	private List<ClusterServicesModel> clusterServers;//集群服务
	
	public ServerModel(){
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getDomainPort() {
		return domainPort;
	}
	public void setDomainPort(String domainPort) {
		this.domainPort = domainPort;
	}
	public String getSystemTypeId() {
		return systemTypeId;
	}
	public void setSystemTypeId(String systemTypeId) {
		this.systemTypeId = systemTypeId;
	}
	public String getMiddlewareTypeId() {
		return middlewareTypeId;
	}
	public void setMiddlewareTypeId(String middlewareTypeId) {
		this.middlewareTypeId = middlewareTypeId;
	}
	public String getDomainPath() {
		return domainPath;
	}
	public void setDomainPath(String domainPath) {
		this.domainPath = domainPath;
	}
	public String getAppPath() {
		return appPath;
	}
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}
	public String getWinRarPath() {
		return winRarPath;
	}
	public void setWinRarPath(String winRarPath) {
		this.winRarPath = winRarPath;
	}
	public String getAgentPath() {
		return agentPath;
	}
	public void setAgentPath(String agentPath) {
		this.agentPath = agentPath;
	}
	public String getAgentPort() {
		return agentPort;
	}
	public void setAgentPort(String agentPort) {
		this.agentPort = agentPort;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		if(status==null || status.equals("")){
			status="1";
		}
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAppVerification() {
		return appVerification;
	}
	public void setAppVerification(String appVerification) {
		this.appVerification = appVerification;
	}
	public String getBackUpPath() {
		return backUpPath;
	}
	public void setBackUpPath(String backUpPath) {
		this.backUpPath = backUpPath;
	}
	public String getSystemType() {
		return systemType;
	}
	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}
	public String getMiddlewareType() {
		return middlewareType;
	}
	public void setMiddlewareType(String middlewareType) {
		this.middlewareType = middlewareType;
	}
	public String getAgentPassword() {
		return agentPassword;
	}
	public void setAgentPassword(String agentPassword) {
		this.agentPassword = agentPassword;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getBackupType() {
		
		return backupType;
	}
	
	/**
	 * 0-复制文件夹；1-压缩
	 * @param backupType
	 */
	public void setBackupType(String backupType) {
		if(backupType==null || backupType.equals("")){
			backupType="0";
		}
		this.backupType = backupType;
	}
	/**
	 * 发布类型:0 发布应用1-文件上传
	 * @return
	 */
	public String getReleaseType() {
		return releaseType;
	}
	public void setReleaseType(String releaseType) {
		if(releaseType==null || releaseType.equals("")){
			releaseType="0";
		}
		this.releaseType = releaseType;
	}
	/**
	 * 获取集群信息
	 * @return
	 */
	public List<ClusterServicesModel> getClusterServers() {
		return clusterServers;
	}
	
	public void setClusterServers(List<ClusterServicesModel> clusterServers) {
		this.clusterServers = clusterServers;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getServerType() {
		if(serverType==null){
			serverType="0";
		}
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getIsAdminServer() {
		if(isAdminServer==null){
			isAdminServer="0";
		}
		return isAdminServer;
	}

	public void setIsAdminServer(String isAdminServer) {
		this.isAdminServer = isAdminServer;
	}


}
