package cn.com.hnisi.model;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * 请求和返回：处理结果对象
 * @author FengGeGe
 *
 */
public class ResultModel implements Serializable {
	static Logger log = Logger.getLogger(ResultModel.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * -1表示失败，0表示成功
	 */
	private int code=0;
	/**
	 * 返回信息
	 */
	private String msg="成功";
	public ResultModel(){}
	public ResultModel(int code,String msg){
		this.code=code;
		this.msg=msg;
	}

	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		if(code<-1){
			code=-1;
		}
		if(code>0){
			code=0;
		}
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		if(msg==null){
			msg="";
		}
		//log.info(msg);
		this.msg = msg;
	}
	

}
