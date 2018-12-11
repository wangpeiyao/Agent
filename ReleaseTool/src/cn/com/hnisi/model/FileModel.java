package cn.com.hnisi.model;

import java.io.Serializable;

public class FileModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long length;
	private String name;
	private String path;//所在目录
	private String parentPath;//父目录
	private boolean isDirectory=false;//是否为目录
	private boolean isFile=false;//是否为文件
	private boolean isDrive=false;//是否为根目录、盘符
	private boolean isExists =false;//是否存在
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getParentPath() {
		return parentPath;
	}
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public boolean isDirectory() {
		return isDirectory;
	}
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}
	public boolean isFile() {
		return isFile;
	}
	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}
	public boolean isDrive() {
		return isDrive;
	}
	public void setDrive(boolean isDrive) {
		this.isDrive = isDrive;
	}
	public boolean isExists() {
		return isExists;
	}
	public void setExists(boolean isExists) {
		this.isExists = isExists;
	}
}
