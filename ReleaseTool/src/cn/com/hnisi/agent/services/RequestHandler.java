package cn.com.hnisi.agent.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.RequestType;
import cn.com.hnisi.type.ShowType;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.composites.ReleaseInterface;

/**
 * 发送请求到Agent
 * 
 * @author FengGeGe
 * 
 */
public class RequestHandler implements Runnable {
	static Logger log = Logger.getLogger(RequestHandler.class);
	ResultModel rm = new ResultModel();
	ReleaseInterface releaseInterface=null;
	ServerModel server;
	SocketChannel socketChannel = null;
	SocketAddress endpoint = null;
	RequestType requestType;// 请求类型
	String oldPassword, newPassword;

	public RequestHandler(ReleaseInterface releaseInterface,
			ServerModel server, RequestType requestType) {
		this.releaseInterface = releaseInterface;
		this.server = server;
		this.requestType = requestType;
		endpoint = new InetSocketAddress(server.getServerIp(),
				Integer.parseInt(server.getAgentPort()));
		init();
	}
	
	/**
	 * 请求类型描述
	 * @param requestType
	 * @return
	 */
	private String operationDesc(RequestType requestType){
		String desc=null;
		switch (requestType) {
		case COMMAND_START:
			desc="启动应用";
			break;
		case COMMAND_STOP:
			desc="停止应用";
			break;
		case COMMAND_BACKUP:
			desc="备份应用";
			break;
		case UPLOAD_FILE:
			desc="上传文件";
			break;
		default:
			break;
		}
		return desc;
	}
	/**
	 * 初始化
	 */
	private void init() {
		try {
			rm.setCode(0);
			rm.setMsg("#################################### "+operationDesc(requestType)+ " ####################################");
			console(releaseInterface,ShowType.STATUS, rm);
			rm.setCode(0);
			rm.setMsg("正在连接服务器，请稍候...");
			console(releaseInterface,ShowType.STATUS, rm);
			if (NetWorkUtil.TestConnectBySocket(endpoint, 5000)) {
				socketChannel = SocketChannel.open();
				socketChannel.connect(endpoint);
				rm.setCode(0);
				rm.setMsg("已连接到 <"
						+ socketChannel.socket().getInetAddress()
								.getHostAddress() + ":"
						+ socketChannel.socket().getPort() + "> Agent服务器.");
				console(releaseInterface,ShowType.STATUS, rm);
				GlobalConfig.saveSocketList(socketChannel);
			} else {
				rm.setCode(-1);
				rm.setMsg("连接 <"+server.getServerIp()+"> 失败，请检查网络和Agent是否正常!!!");
				console(releaseInterface,ShowType.STATUS, rm);
			}
		} catch (IOException e) {
			rm.setCode(-1);
			rm.setMsg("网络连接异常，请检查网络: " + e.getMessage());
			console(releaseInterface,ShowType.STATUS, rm);
			return;
		}
	}

	@Override
	public void run() {
		try {
			if (requestType == RequestType.UPLOAD_FILE) {
				// 上传文件
				if (!GlobalConfig.ReleaseFolder.equals("")) {
					uploadFile(socketChannel, GlobalConfig.ReleaseFolder,
							requestType);
				} else {
					rm.setCode(-1);
					rm.setMsg("请先选择需要上传的文件夹路径");
					console(releaseInterface,ShowType.STATUS, rm);
				}
			} else {
				// 调用命令
				executeCommand(socketChannel, requestType);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (socketChannel != null)
					socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 生成请求命令内容
	 * 
	 * @param requestType
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	protected void executeCommand(SocketChannel channel, RequestType requestType) {
		try {
			if (channel != null) {
				byte[] byteServer = Tool.objectToByte(server);
				// (请求类型)+(server对象字节长度+server对象字节)+(命令类型+命令行字节长度+命令行字节)
				int countSize = (4) + (4 + byteServer.length + 4);
				ByteBuffer buf = ByteBuffer.allocate(countSize);

				// =====================
				// 请求类型为“命令类型”(命令：100)
				buf.putInt(RequestType.COMMAND.getValue());
				// 发送server对象长度+内容
				buf.putInt(byteServer.length);
				buf.put(ByteBuffer.wrap(byteServer));
				// =====================
				// 命令类型(整型)(备分、停止、启动)
				buf.putInt(requestType.getValue());

				buf.flip();
				while (buf.hasRemaining()) {
					channel.write(buf);
				}
				buf.clear();
				receiveResponse(releaseInterface,channel);
			}
		} catch (ConnectException ce) {
			rm.setCode(-1);
			rm.setMsg("连接已断开,异常:" + ce.getMessage());
			console(releaseInterface,ShowType.STATUS, rm);
		} catch (ClosedChannelException cce) {
			rm.setCode(-1);
			rm.setMsg("连接已断开,异常:" + cce.getMessage());
			console(releaseInterface,ShowType.STATUS, rm);
		} catch (IOException ex) {
			rm.setCode(-1);
			rm.setMsg("接收服务器响应信息时出错,异常:" + ex.getMessage());
			console(releaseInterface,ShowType.STATUS, rm);
		}finally{		
				try {

					if(socketChannel!=null && socketChannel.isOpen()){
						log.info("断开与Agent的连接");
						socketChannel.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}			
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param uploadFileName
	 * @param requestType
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	private void uploadFile(SocketChannel channel, String uploadFileName,
			RequestType requestType) {
		// TODO 上传文件
		List<String> fileList = new ArrayList<String>();
		FileInputStream fis = null;
		FileChannel fileChannel = null;
		File serverPath = null;
		ByteBuffer fileHeadBuffer = null;
		byte[] serverByte = null;
		try {
			serverByte = Tool.objectToByte(server);
			Tool.getFiles(fileList, uploadFileName);
			serverPath = new File(uploadFileName);
			int fileCount = 0;
			fileHeadBuffer = ByteBuffer.allocate(4 + 4 + serverByte.length + 4);
			fileHeadBuffer.putInt(RequestType.UPLOAD_FILE.getValue());// 请求类型：200
			fileHeadBuffer.putInt(serverByte.length);// ServerModel对象长度
			fileHeadBuffer.put(ByteBuffer.wrap(serverByte));// ServerModel对象内容
			fileHeadBuffer.putInt(fileList.size());// 文件数 Integer
			fileHeadBuffer.flip();
			channel.write(fileHeadBuffer);
			fileHeadBuffer.clear();

			long start, end;
			start = System.currentTimeMillis();
			for (String path : fileList) {
				fileCount++;
				File file = new File(path);
				String serverFolder = file.getPath().replace(
						serverPath.getParent(), "").replace("\\", "/");

				// 文件名长度+文件名内容+文件路径长度+文件路径内容+文件长度(必需是ISO-8859-1)编码
				fileHeadBuffer = ByteBuffer.allocate(4
						+ Tool.utfToIso(file.getName()).getBytes("ISO-8859-1").length
						+ 4
						+ Tool.utfToIso(serverFolder).getBytes("ISO-8859-1").length
						+ 8);
				fileHeadBuffer.putInt(Tool.utfToIso(file.getName()).getBytes("ISO-8859-1").length);// “文件名”长度 4
				fileHeadBuffer.put(Tool.utfToIso(file.getName()).getBytes("ISO-8859-1"));// “文件名”内容
				fileHeadBuffer.putInt(Tool.utfToIso(serverFolder).getBytes("ISO-8859-1").length);// 文件路径 长度 4
				fileHeadBuffer.put(ByteBuffer.wrap(Tool.utfToIso(serverFolder).getBytes("ISO-8859-1")));// 文件路径内容
				fileHeadBuffer.putLong(file.length());// “文件”长度 8
				fileHeadBuffer.flip();
				channel.write(fileHeadBuffer);
				fileHeadBuffer.clear();

				int bufSize = 1024000;
				ByteBuffer fileBuffer = null;
				int size = 0;
				long remainingSize = file.length();//剩余可读长度
				fis = new FileInputStream(file);
				fileChannel = fis.getChannel();
				rm.setMsg("######正在上传文件  --> " + file.getPath()+" 文件大小:"+Tool.getFileSize(file.length()));
				console(releaseInterface,ShowType.STATUS, rm);
				while (remainingSize > 0) {
					if (remainingSize <= bufSize) {
						bufSize = (int) remainingSize;
					}
					fileBuffer = ByteBuffer.allocate(bufSize);
					size = fileChannel.read(fileBuffer);
					if (size >=0) {
						fileBuffer.rewind();
						fileBuffer.limit(size);
						remainingSize -= size;
						channel.write(fileBuffer);
						fileBuffer.clear();						
					} else {
						break;
					}
				}
				fileBuffer = null;
				file = null;
				fileHeadBuffer = null;
				receiveResponse(releaseInterface,channel);
			}
			if (fileCount == fileList.size()) {
				receiveResponse(releaseInterface,channel);
				end = System.currentTimeMillis();
				rm.setCode(0);
				rm.setMsg("文件发送完毕. 总文件数: " + fileList.size() + "个,已上传: "
						+ fileCount + " 个,总用时: "
						+ Tool.millisecondFormat(end - start));
				console(releaseInterface,ShowType.STATUS, rm);
			}
		} catch (ClosedChannelException ex) {
			rm.setMsg("网络连接异常，连接失败或已关闭");
			console(releaseInterface,ShowType.STATUS, rm);
		} catch (IOException e) {
			rm.setCode(-1);
			rm.setMsg("上传文件出错，原因:" + e.getMessage());
			console(releaseInterface,ShowType.ERROR, rm);
		} finally {
			System.gc();
			log.info("回收系统资源");
			serverPath = null;
			serverByte = null;
			fileHeadBuffer = null;
			if (fileList != null) {
				fileList.clear();
				fileList = null;
			}

			try {
				//关闭流，避免文件被占用
				if (fis != null) {
					fis.close();
					fis = null;
				}
				if (fileChannel != null) {
					fileChannel.close();
					fileChannel = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	/**
	 * 
	 * @param socketChannel
	 * @return 0-表示已完成; 1-表示进行中
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public static ResultModel receiveResponse(SocketChannel channel) throws IOException {
		return receiveResponse(null,channel);
	}
	/**
	 * @param ReleaseInterface
	 * @param socketChannel
	 * @return 0-表示已完成; 1-表示进行中
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public static ResultModel receiveResponse(ReleaseInterface rif,SocketChannel channel) throws IOException {
		// ==================开始--用于接收Agent响应的信息====================
		ResultModel result=new ResultModel();
		int size = 0;
		ByteBuffer resBuf = null;

		int canReadSize = -1;
		// 如果processStatus!=1时退出循环
		int processStatus = 1;
		while (processStatus==1) {
			// 处理状态：0-处理完成，1-处理中
			resBuf = ByteBuffer.allocate(4);
			try {
				size = channel.read(resBuf);
			} catch (IOException ie) {
				result.setMsg("Agent连接失败，远程主机强迫关闭了一个现有的连接。");
				if(rif!=null){
					console(rif,ShowType.STATUS, result);
				}
				break;
			}
			if (size == 4) {
				resBuf.flip();
				processStatus = resBuf.getInt();
				resBuf.clear();
			}
			// 处理结果0-正常，非0为异常
			resBuf = ByteBuffer.allocate(4);
			size = channel.read(resBuf);
			int resultCode = 1;
			if (size == 4) {
				resBuf.flip();
				resultCode = resBuf.getInt();
				resBuf.clear();
				result.setCode(resultCode);
			}
			// 返回结果字符串的长度
			resBuf = ByteBuffer.allocate(4);
			size = channel.read(resBuf);
			int resultMesLength = 0;
			if (size == 4) {
				resBuf.flip();
				resultMesLength = resBuf.getInt();
				resBuf.clear();
			}
			canReadSize = resultMesLength;// 剩余可以读取的长度
			byte[] resultByte = null;
			int bufferSize = 0;
			StringBuffer strBuf = new StringBuffer();
			while (canReadSize > 0) {
				bufferSize = 1024 * 1024;
				if (canReadSize <= bufferSize) {
					bufferSize = canReadSize;
				}
				resBuf = ByteBuffer.allocate(bufferSize);
				size = channel.read(resBuf);
				if (size >= 0) {
					resBuf.flip();
					resultByte = new byte[size];
					resBuf.get(resultByte);
					resBuf.clear();
					strBuf.append(new String(resultByte, GlobalConfig.ENCODING));
				}
				canReadSize -= size;
				if (canReadSize <= 0) {
					break;
				}
			}
			if (resultByte != null) {
				result.setMsg(strBuf.toString());
				if(rif!=null){
					console(rif,ShowType.RESPONSE, result);
				}
			}
		}
		return result;
	}

	/**
	 * 将信息输出到前台界面ReleaseInterface
	 * @param ReleaseInterface
	 * @param ShowType
	 * @param ResultModel
	 */
	private static void console(ReleaseInterface rif,ShowType type, ResultModel rm) {
		if (rif != null) {
			rif.consoleResult(type.getValue(), rm);
		}
	}

}
