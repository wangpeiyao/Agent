package cn.com.hnisi.agent.services.function;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.RequestType;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.dialog.DownloadFile;

/**
 * 下载服务器文件
 * @author WenZhiFeng
 * 2018年3月5日
 */
public class DownloadFileHandler implements Runnable{
	DownloadFile downloadModel;
	ServerModel server;
	SocketChannel socketChannel = null;
	SocketAddress endpoint = null;
	String downloadPath = null;
	String saveFilePath = null;
	
	public DownloadFileHandler(String downloadPath, String saveFilePath,
			ServerModel server, DownloadFile downloadModel) {
		this.server = server;
		this.downloadPath = downloadPath;
		this.saveFilePath = saveFilePath;
		this.downloadModel = downloadModel;
	}

	public void run() {
		FileOutputStream out = null;
		try {
			endpoint = new InetSocketAddress(server.getServerIp(),
					Integer.parseInt(server.getAgentPort()));
			if (NetWorkUtil.TestConnectBySocket(endpoint, 3500)) {
				socketChannel = SocketChannel.open();
				socketChannel.connect(endpoint);
				// 开始请求
				byte[] byteServer = Tool.objectToByte(server);// ServerModel
				// (请求类型)+(server对象字节长度+server对象字节)+(4+下载文件目标)
				// fileHeadBuffer.put(ByteBuffer.wrap(Tool.utfToIso(serverFolder).getBytes("ISO-8859-1")));//
				// 文件路径内容
				int countSize = (4)
						+ (4 + byteServer.length)
						+ (4 + Tool.utfToIso(downloadPath).getBytes(
								"ISO-8859-1").length);
				ByteBuffer buf = ByteBuffer.allocate(countSize);

				// =====================
				// 请求类型为“命令类型”(命令：700 下载日志)
				buf.putInt(RequestType.DOWNLOAD_FILE.getValue());

				// 发送server对象长度+内容
				buf.putInt(byteServer.length);
				buf.put(ByteBuffer.wrap(byteServer));

				// 发送下载文件路径
				buf.putInt(Tool.utfToIso(downloadPath).getBytes(
						"ISO-8859-1").length);
				buf.put(ByteBuffer.wrap(Tool.utfToIso(downloadPath)
						.getBytes("ISO-8859-1")));

				buf.flip();
				while (buf.hasRemaining()) {
					socketChannel.write(buf);
				}
				buf.clear();
				// ================================

				Thread.sleep(1000);

				// 开始接收文件
				int size = 0;
				// 接收状态码
				buf = ByteBuffer.allocate(4);
				int statusCode = 0;
				size = socketChannel.read(buf);
				if (size > 0) {
					buf.flip();
					statusCode = buf.getInt();
					buf.clear();
				}
				// 接收状态信息长度值
				buf = ByteBuffer.allocate(4);
				int statusInfoLength = 0;
				size = socketChannel.read(buf);
				if (size > 0) {
					buf.flip();
					statusInfoLength = buf.getInt();
					buf.clear();
				}
				byte[] statusInfo = null;
				buf = ByteBuffer.allocate(statusInfoLength);
				size = socketChannel.read(buf);
				if (size > 0) {
					statusInfo = new byte[size];
					buf.flip();
					buf.get(statusInfo);
					buf.clear();
				}
				if (statusCode != 0) {
					//如果状态不为0，则提示报错
					downloadModel.showDownloadInfo(new String(statusInfo,"utf-8"));
					return;
				} else {
					buf = ByteBuffer.allocate(4);
					int nameLength = 0;
					byte[] fileName = null;
					size = socketChannel.read(buf);
					if (size > 0) {
						buf.flip();
						nameLength = buf.getInt();
						buf.clear();
						buf = ByteBuffer.allocate(nameLength);
						size = socketChannel.read(buf);
						fileName = new byte[size];
						buf.flip();
						buf.get(fileName);
						buf.clear();
					}

					buf = ByteBuffer.allocate(8);
					long fileLength = 0L;

					size = socketChannel.read(buf);
					if (size == 8) {
						buf.flip();
						fileLength = buf.getLong();// 文件长度
						downloadModel.setFileSize(fileLength);
						buf.clear();
						int bufSize = 1024000;
						long remainingSize = fileLength;
						// 获取文件内容
						byte[] fileByte = null;

						long writedSize = 0;
						File file = new File(saveFilePath);
						if (!file.exists()) {
							file.createNewFile();
						}
						out = new FileOutputStream(file);

						while (remainingSize > 0) {
							bufSize = 1024 * 1024;
							if (remainingSize < bufSize) {
								bufSize = (int) remainingSize;
							}
							buf = ByteBuffer.allocate(bufSize);
							size = socketChannel.read(buf);
							if (size >= 0) {
								buf.flip();
								fileByte = new byte[size];
								buf.get(fileByte);
								writedSize += size;
								out.write(fileByte);
								out.flush();
								buf.clear();
								remainingSize -= size;// 计算剩余可读取的字节数
								downloadModel.setProgressBar((int) writedSize);
							}
						}
					}
				}
			} else {
				downloadModel.downloadStatus("下载失败，无法连接到Agent代理器，请检查");
			}
		} catch (Exception e) {
			e.printStackTrace();
			downloadModel.downloadStatus("下载失败，原因:" + e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
					socketChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		downloadModel.handleShellCloseEvent();
	}
}
