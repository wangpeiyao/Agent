package cn.com.hnisi.agent.services.function;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.apache.log4j.Logger;




import cn.com.hnisi.agent.services.RequestHandler;
import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.FileModel;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.RequestType;
import cn.com.hnisi.type.ShowType;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.dialog.ManageServerFile;

/**
 * 删除服务器文件
 * @author WenZhiFeng
 * 2018年3月2日
 */
public class DeleteServerFileHandler {
	static Logger log = Logger.getLogger(DeleteServerFileHandler.class);
	SocketAddress endpoint = null;
	RequestType requestType;// 请求类型
	SocketChannel socketChannel = null;
	ServerModel server;
	ManageServerFile manageServerFile=null;
	
	public DeleteServerFileHandler(ManageServerFile manageServerFile, ServerModel server) {
		this.manageServerFile=manageServerFile;
		this.server = server;
		endpoint = new InetSocketAddress(server.getServerIp(),
				Integer.parseInt(server.getAgentPort()));
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		try {
			if (NetWorkUtil.TestConnectBySocket(endpoint, 2000)) {
				socketChannel = SocketChannel.open();
				socketChannel.connect(endpoint);
				GlobalConfig.saveSocketList(socketChannel);
			} else {
				manageServerFile.showMessage(ShowType.ERROR, "连接异常，请检查网络和Agent是否正常");
			}
		} catch (IOException e) {
			manageServerFile.showMessage(ShowType.ERROR, "网络连接异常，请检查网络: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 删除文件
	 * @param deleteFiles
	 * @return
	 */
	public ResultModel delete(List<FileModel> deleteFiles){
		ResultModel result=new ResultModel();
		try{
			byte[] byteServer = Tool.objectToByte(server);// ServerModel
			byte[] byteFile= Tool.objectToByte(deleteFiles);// List<FileModel>
			int capacity = (4)//请求类型
					+ (4 + byteServer.length)
					+ (4 + byteFile.length);
			ByteBuffer buf=ByteBuffer.allocate(capacity);
			buf.putInt(RequestType.DELETE_FILE.getValue());//删除文件
			buf.putInt(byteServer.length);
			buf.put(ByteBuffer.wrap(byteServer));
			buf.putInt(byteFile.length);
			buf.put(ByteBuffer.wrap(byteFile));
			
			buf.flip();
			while (buf.hasRemaining()) {
				socketChannel.write(buf);
			}
			buf.clear();
			result=RequestHandler.receiveResponse(socketChannel);
		}catch(Exception e){
			e.printStackTrace();
			result.setCode(-1);
			result.setMsg(e.getMessage());
		}
		return result;
	}

}
