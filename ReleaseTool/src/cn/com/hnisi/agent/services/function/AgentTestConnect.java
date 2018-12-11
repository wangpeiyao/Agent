package cn.com.hnisi.agent.services.function;

import java.io.IOException;
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
 * 测试Agent连接
 * @author FengGeGe
 *
 */
public class AgentTestConnect {
	static Logger log = Logger.getLogger(AgentTestConnect.class);

	/**
	 * 连接到Agent服务
	 * @param ip
	 * @param port
	 * @param password
	 * @return
	 */
	public static ResultModel connectAgent(String ip, int port, String password) {
		ResultModel result = new ResultModel();
		SocketAddress endpoint = null;
		SocketChannel socketChannel = null;
		String pwd="";
		try {
			endpoint = new InetSocketAddress(ip, port);
			if(NetWorkUtil.TestConnectBySocket(endpoint, 1500)){
				socketChannel = SocketChannel.open();
				socketChannel.connect(endpoint);
				GlobalConfig.saveSocketList(socketChannel);
			}else{
				result.setCode(-1);
				result.setMsg("连接失败，请检查网络和端口是否正常!");
				return result;

			}
		} catch (Exception e) {
			result.setCode(-1);
			result.setMsg("连接失败，请检查Agent是否已运行、网络是否正常!");
			return result;
		}

		ByteBuffer buf =null;
		try {
			if(password!=null){
				pwd=password;
			}	
			buf=ByteBuffer.allocate(4+4+pwd.getBytes().length);
			buf.putInt(RequestType.TEST.getValue());
			buf.putInt(pwd.getBytes().length);
			buf.put(ByteBuffer.wrap(pwd.getBytes()));
			buf.flip();
			while (buf.hasRemaining()) {
				try {
					socketChannel.write(buf);
				} catch (IOException e) {
					result.setCode(-1);
					result.setMsg("Agent连接失败.");
					return result;
				}
			}
			buf.clear();
			return receiveResponse(socketChannel);
		} catch (Exception e) {
			result.setCode(-1);
			result.setMsg("Agent连接失败.");
			return result;
		}
	}

	public static ResultModel receiveResponse(SocketChannel channel) throws IOException{
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
				result.setCode(-1);
				result.setMsg("Agent连接失败!");
				return result;
			}
			if (size == 4) {
				resBuf.flip();
				processStatus = resBuf.getInt();
				resBuf.clear();
			} else {
				result.setCode(-1);
				result.setMsg("Agent连接失败!");
				return result;
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
			result.setCode(resultCode);
			
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
			result.setMsg(strBuf.toString());
			if (processStatus != 1) {
				channel.close();
				break;
			}
		}
		return result;
	}
}
