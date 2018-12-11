package cn.com.hnisi.agent.services.function;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.FileModel;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.RequestType;
import cn.com.hnisi.type.ShowType;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.dialog.ManageServerFile;

public class RemoteGetServerPath {
	static Logger log = Logger.getLogger(RemoteGetServerPath.class);
	ResultModel rm = new ResultModel();
	ManageServerFile manageServerFile;
	ServerModel server;
	SocketChannel socketChannel = null;
	SocketAddress endpoint = null;
	String path = new String();

	public RemoteGetServerPath(ManageServerFile manageServerFile, ServerModel server,
			String path) {
		this.manageServerFile = manageServerFile;
		this.server = server;
		endpoint = new InetSocketAddress(server.getServerIp(),
				Integer.parseInt(server.getAgentPort()));
		this.path = path;
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		try {
			//showMessage(ShowType.STATUS, "正在连接服务器，请稍候...");
			if (NetWorkUtil.TestConnectBySocket(endpoint, 2000)) {
				socketChannel = SocketChannel.open();
				socketChannel.connect(endpoint);
//				showMessage(ShowType.STATUS, "已连接到 ["
//						+ socketChannel.socket().getInetAddress()
//								.getHostAddress() + ":"
//						+ socketChannel.socket().getPort() + "] 服务器.");
				GlobalConfig.saveSocketList(socketChannel);
			} else {
				showMessage(ShowType.STATUS, "连接异常，请检查网络和Agent是否正常");
			}
		} catch (IOException e) {
			showMessage(ShowType.STATUS, "网络连接异常，请检查网络: " + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	private void showMessage(ShowType type, String msg) {
		if (manageServerFile != null) {
			manageServerFile.showMessage(type, msg);
		}
	}

	public List<FileModel> getRemoteServerPath() {
		try {
			if(socketChannel==null){
				return null;
			}
			byte[] byteServer = Tool.objectToByte(server);// ServerModel
			if (path == null)
				path = "";
			byte[] bytePath = path.getBytes(GlobalConfig.ENCODING);// 路径
			// (请求类型)+(server对象字节长度+server对象字节)+(路径长度+路径字节)
			int countSize = (4) + (4 + byteServer.length)
					+ (4 + bytePath.length);
			ByteBuffer buf = ByteBuffer.allocate(countSize);

			// =====================
			// 请求类型为“命令类型”(命令：500)
			buf.putInt(RequestType.GET_PATH.getValue());

			// 发送server对象长度+内容
			buf.putInt(byteServer.length);
			buf.put(ByteBuffer.wrap(byteServer));
			// =====================
			// 路径长度
			buf.putInt(bytePath.length);
			buf.put(ByteBuffer.wrap(bytePath));

			buf.flip();
			while (buf.hasRemaining()) {
				socketChannel.write(buf);
			}
			buf.clear();
			socketChannel.socket().shutdownOutput();
			
			// 接收参数
			ByteBuffer pathBuf = ByteBuffer.allocate(4);
			int size = 0;
			int folderMapLength = 0;
			size = socketChannel.read(pathBuf);
			// 接收参数
			pathBuf.flip();
			folderMapLength = pathBuf.getInt();
			pathBuf.clear();

			byte[] mapByte = null;
			int bufferSize = 102400;
			int canRead = folderMapLength;
			ByteArrayOutputStream out =new ByteArrayOutputStream(); 
			while (canRead>0) {
				if (bufferSize > canRead) {
					bufferSize = canRead;
				}
				pathBuf = ByteBuffer.allocate(bufferSize);
				size = socketChannel.read(pathBuf);
				mapByte = new byte[size];
				pathBuf.flip();
				pathBuf.get(mapByte);
				out.write(mapByte);
				pathBuf.clear();
				canRead -= size;
			}
			@SuppressWarnings("unchecked")
			List<FileModel> folderMap = (List<FileModel>) Tool
					.ByteToObject(out.toByteArray());
			socketChannel.close();
			return folderMap;
		} catch (ConnectException ce) {
			showMessage(ShowType.STATUS, "已断开连接");
		} catch (ClosedChannelException cce) {
			showMessage(ShowType.STATUS, "连接已关闭");
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return null;
	}

}
