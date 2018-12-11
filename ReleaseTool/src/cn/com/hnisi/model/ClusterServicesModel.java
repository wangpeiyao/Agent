package cn.com.hnisi.model;

import java.io.Serializable;


public class ClusterServicesModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String sid;
	private String name;
	private String adminUrl;
	private String stagePath;
	private int port;
	private int isUploadFile=1;//是否同步上传文件;0-不同步;1-同步
	private int isAutoStart=1;//是否同步启动;0-不同步;1-同步
	private String userName;
	private String passWord;
	private int status=1;//1-有交；0-无效
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSid() {
		if(sid==null){
			return "";
		}
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getName() {
		if(name==null){
			return "";
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdminUrl() {
		if(adminUrl==null){
			return "";
		}
		return adminUrl;
	}
	public void setAdminUrl(String adminUrl) {
		this.adminUrl = adminUrl;
	}
	public String getStagePath() {
		if(stagePath==null){
			return "";
		}
		return stagePath;
	}
	public void setStagePath(String stagePath) {
		this.stagePath = stagePath;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public int getIsUploadFile() {
		if(isUploadFile!=1){
			isUploadFile=0;
		}
		return isUploadFile;
	}
	public void setIsUploadFile(int isUploadFile) {
		if(isUploadFile!=1){
			isUploadFile=0;
		}
		this.isUploadFile = isUploadFile;
	}
	public int getIsAutoStart() {
		return isAutoStart;
	}
	public void setIsAutoStart(int isAutoStart) {
		if(isAutoStart!=1){
			isAutoStart=0;
		}
		this.isAutoStart = isAutoStart;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
