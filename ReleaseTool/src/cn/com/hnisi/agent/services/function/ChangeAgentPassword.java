package cn.com.hnisi.agent.services.function;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.type.RequestType;
import cn.com.hnisi.util.NetWorkUtil;

/**
 * 更改服务器Agent密码
 * 
 * @author FengGeGe
 * 
 */
public class ChangeAgentPassword {
	static Logger log = Logger.getLogger(ChangeAgentPassword.class);
	SocketAddress endpoint = null;
	RequestType requestType;// 请求类型
	SocketChannel socketChannel = null;

	public ChangeAgentPassword(String ip, int port) {
		// 设置请求类型为“修改密码”
		this.requestType = RequestType.CHANGE_PASSWORD;
		endpoint = new InetSocketAddress(ip, port);
	}

	/**
	 * 修改Agent端密码
	 * 
	 * @param oldPassword 入参前要求先进行加密
	 * @param newPassword 入参前要求先进行加密
	 * @return
	 */
	public String change(String oldPassword, String newPassword) {
		try {
			if(NetWorkUtil.TestConnectBySocket(endpoint, 3000)){
				socketChannel = SocketChannel.open();
				socketChannel.connect(endpoint);
				GlobalConfig.saveSocketList(socketChannel);
			}else{
				return "连接失败，请检查Agent是否已运行、网络是否正常";
			}		
		} catch (ConnectException ce) {
			return "连接失败，请检查Agent是否已运行、网络是否正常";
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (oldPassword == null) {
			oldPassword = "";
		}
		if (newPassword == null || newPassword.trim().length() <= 0) {
			return "“新密码”不能为空!";
		}
		ByteBuffer buf =null;
		try {
		buf=ByteBuffer.allocate(4
				+ (4 + oldPassword.getBytes().length)
				+ (4 + newPassword.getBytes().length));
		    buf.putInt(requestType.getValue());
		
			buf.putInt(oldPassword.getBytes().length);
			buf.put(ByteBuffer.wrap(oldPassword.getBytes()));
			buf.putInt(newPassword.getBytes().length);
			buf.put(ByteBuffer.wrap(newPassword.getBytes()));
		} catch (Exception e) {
			return "设置Agent密码时，系统发生异常:"+e.getMessage();
		}
		buf.flip();
		while (buf.hasRemaining()) {
			try {
				socketChannel.write(buf);
			} catch (IOException e) {
				log.error("设置服务器Agent密码失败.");
			}
		}
		buf.clear();
		try {
			return receiveResponse(socketChannel);
		} catch (ConnectException ce) {
			return "连接失败，请检查Agent是否已运行、网络是否正常";
		} catch (IOException e) {
			return "获取Agent响应信息时出错.";
		}

	}

	public String receiveResponse(SocketChannel channel) throws IOException {
		ResultModel result = new ResultModel();
		// ==================开始--用于接收Agent响应的信息====================
		int size = 0;
		ByteBuffer resBuf = null;
		StringBuffer strBuf = new StringBuffer();
		int canReadSize = -1;
		// 如果processStatus!=1(不是处理中)时退出循环
		int processStatus = 1;
		while (true) {
			// 处理状态：0-处理完成，1-处理中
			resBuf = ByteBuffer.allocate(4);
			try {
				size = channel.read(resBuf);
			} catch (IOException e) {
				return "网络连接失败!";
			}
			if (size == 4) {
				resBuf.flip();
				processStatus = resBuf.getInt();
				resBuf.clear();
			} else {
				return "修改失败，服务器未有响应.";
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
			}else{
				break;
			}
			// 返回结果字符串的长度
			resBuf = ByteBuffer.allocate(4);
			size = channel.read(resBuf);
			int resultMesLength = 0;
			if (size == 4) {
				resBuf.flip();
				resultMesLength = resBuf.getInt();
				resBuf.clear();
			}else{
				break;
			}
			canReadSize = resultMesLength;// 剩余可以读取的长度
			byte[] resultByte = null;
			int bufferSize = 0;

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
			if (processStatus != 1) {
				channel.close();
				break;
			}
		}
		return strBuf.toString();
	}
}
