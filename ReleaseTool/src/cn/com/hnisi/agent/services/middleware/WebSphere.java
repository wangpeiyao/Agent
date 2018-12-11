package cn.com.hnisi.agent.services.middleware;

import org.apache.log4j.Logger;

import cn.com.hnisi.agent.services.RequestHandler;
import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.RequestType;
import cn.com.hnisi.view.composites.ReleaseInterface;

public class WebSphere implements IMiddleware {
	static Logger log = Logger.getLogger(WebSphere.class);
	ServerModel server;
	ReleaseInterface releaseInterface;

	public WebSphere(ReleaseInterface releaseInterface, ServerModel server) {
		this.releaseInterface = releaseInterface;
		this.server = server;
	}


	/**
	 * 备份应用
	 */
	public void backup() {
		GlobalConfig.THREAD_POOL.execute(new RequestHandler(releaseInterface,server,RequestType.COMMAND_BACKUP));
		
	}

	/**
	 * 停止应用
	 */
	public void stop() {	
		//Agent会根据请求的Sever端口和domainPath获取进程ID
		GlobalConfig.THREAD_POOL.execute(new RequestHandler(releaseInterface,server,RequestType.COMMAND_STOP));
	}

	/**
	 * 启动应用
	 */
	public void start() {	
		GlobalConfig.THREAD_POOL.execute(new RequestHandler(releaseInterface,server,RequestType.COMMAND_START));
	}

	/**
	 * 上传文件，文件目录读取GlobalConfig.ReleaseFolder
	 */
	public void uploadFile(String dirPath){
		GlobalConfig.THREAD_POOL.execute(new RequestHandler(releaseInterface,server,RequestType.UPLOAD_FILE));
	}

}
