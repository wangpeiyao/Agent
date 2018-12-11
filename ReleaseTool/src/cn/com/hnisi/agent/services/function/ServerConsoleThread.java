package cn.com.hnisi.agent.services.function;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.RequestType;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.dialog.PrintConsole;

/**
 * 获取服务器应用日志
 * 
 * @author FengGeGe
 * 
 */
public class ServerConsoleThread extends Thread {
	static Logger log = Logger.getLogger(ServerConsoleThread.class);
	PrintConsole viewlogs;
	ResultModel rm = new ResultModel();
	ServerModel server;
	SocketChannel socketChannel = null;
	SocketAddress endpoint = null;
	private int STATUS = 0;
	private int SUSPEND = 1;
	private int RESUME = 2;
	private String encoding=GlobalConfig.ENCODING;
	public ServerConsoleThread(PrintConsole viewlogs, ServerModel server,String encoding) {
		this.server = server;
		this.viewlogs = viewlogs;
		this.encoding=encoding;
	}

	/**
	 * 加载日志
	 */
	public synchronized void run() {
		listener();
	}

	private ResultModel listener() {
		ResultModel result = new ResultModel();
		try {
			endpoint = new InetSocketAddress(server.getServerIp(),
					Integer.parseInt(server.getAgentPort()));
			if (NetWorkUtil.TestConnectBySocket(endpoint, 6000)) {
				if (server.getReleaseType().equals("0")) {
					if (!NetWorkUtil.TestConnectBySocket(server.getServerIp(),
							Integer.parseInt(server.getDomainPort()), 6000)) {

						result.setCode(-1);
						result.setMsg("状态：应用端口 [" + server.getServerIp() + ":"
								+ server.getDomainPort()
								+ "] 状态暂无响应.\n等应用启动运行后，手工刷新即可.");
						viewlogs.showSyslogText(result.getMsg());
						return result;
					}
					viewlogs.showSyslogText("状态：已连接到应用端口 ["
							+ server.getServerIp() + ":"
							+ server.getDomainPort() + "] 正在监控控制台输出...");

					socketChannel = SocketChannel.open();
					socketChannel.connect(endpoint);

					GlobalConfig.saveSocketList(socketChannel);

					// 开始获取日志
					byte[] byteServer = Tool.objectToByte(server);// ServerModel
					// (请求类型)+(server对象字节长度+server对象字节)
					int countSize = (4) + (4 + byteServer.length);
					ByteBuffer buf = ByteBuffer.allocate(countSize);

					// =====================
					// 请求类型为“命令类型”(命令：600 获取日志)
					buf.putInt(RequestType.VIEW_LOGS.getValue());

					// 发送server对象长度+内容
					buf.putInt(byteServer.length);
					buf.put(ByteBuffer.wrap(byteServer));

					buf.flip();
					while (buf.hasRemaining()) {
						socketChannel.write(buf);
					}
					buf.clear();

					// 接收日志信息
					int size = 0;
					int textLength = 0;
					while (true) {
						buf = ByteBuffer.allocate(4);
						size = socketChannel.read(buf);
						// 接收参数
						buf.flip();
						textLength = buf.getInt();
						buf.clear();
						int bufferSize = 8192;
						int canRead = textLength;
						try {

							byte[] logByte = null;
							while (canRead > 0) {
								if (canRead <= bufferSize) {
									bufferSize = canRead;
								}
								buf = ByteBuffer.allocate(bufferSize);
								try {
									size = socketChannel.read(buf);
								} catch (ClosedByInterruptException cbie) {
									log.info("关闭了查看控件台窗口");
								}
								if (size > 0) {
									buf.flip();
									logByte = new byte[size];
									buf.get(logByte);
									buf.clear();
								}
								if (STATUS != SUSPEND) {
									viewlogs.showSyslogText(new String(logByte,
											encoding));
								}
								canRead -= size;
							}

						} catch (Exception ex) {
							ex.printStackTrace();
						}

					}//---end while---

				} else {
					result.setCode(-1);
					result.setMsg("提示：非应用，无法查看控制台.");
					viewlogs.showSyslogText(result.getMsg());
					return result;
				}
			} else {
				result.setCode(-1);
				result.setMsg("提示：连接异常，请检查网络和Agent是否正常.");
				viewlogs.showSyslogText(result.getMsg());
				return result;
			}
		} catch (ClosedByInterruptException cie) {
			result.setCode(-99);
			result.setMsg("提示：监听已退出");
			viewlogs.showSyslogText(result.getMsg());

		} catch (ClosedChannelException cce) {
			result.setCode(-1);
			result.setMsg("提示：网络连接已断开" + cce.getMessage());
			viewlogs.showSyslogText(result.getMsg());

		} catch (IOException ioe) {
			result.setCode(-1);
			result.setMsg("提示：网络连接异常,原因: " + ioe.getMessage());
			viewlogs.showSyslogText(result.getMsg());

		} catch (Exception e) {
			result.setCode(-99);
			result.setMsg("错误：程序异常,原因: " + e.getMessage());
			viewlogs.showSyslogText(result.getMsg());
		} finally {
			closeSocket();
		}
		return result;
	}

	/**
	 * 暂停任务
	 */
	public void setSuspend() {
		STATUS = SUSPEND;
	}

	/**
	 * 继续任务
	 */
	public void setResume() {
		STATUS = RESUME;
	}

	public void closeSocket() {
		if (socketChannel != null) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
}
