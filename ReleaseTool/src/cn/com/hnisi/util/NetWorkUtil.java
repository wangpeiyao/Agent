package cn.com.hnisi.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;

public class NetWorkUtil {
	static Logger log = Logger.getLogger(NetWorkUtil.class);

	/**
	 * 测试IP端口是否有响应
	 * 
	 * @param endpoint
	 * @param timeout
	 * @return
	 */
	public static boolean TestConnectBySocket(SocketAddress endpoint,
			int timeout) {
		Socket socket = new Socket();

		long start = 0;
		long end = 0;
		try {
			start = System.currentTimeMillis();
			socket.connect(endpoint, timeout);

			end = System.currentTimeMillis();
			log.info("测试:" + endpoint + "连接成功. 时间=" + (end - start) + "ms");
			return true;

		} catch (Exception e) {
			log.info("测试:" + endpoint + "连接失败.");
			return false;
		} finally {
			try {
				log.info("释放连接");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean TestConnectBySocket(String ip,int port,
			int timeout) {
		Socket socket = new Socket();
		SocketAddress endpoint=new InetSocketAddress(ip,port);
		long start = 0;
		long end = 0;
		try {
			start = System.currentTimeMillis();
			socket.connect(endpoint, timeout);

			end = System.currentTimeMillis();
			log.info("测试:" + endpoint + "连接成功. 时间=" + (end - start) + "ms");
			return true;

		} catch (Exception e) {
			log.info("测试:" + endpoint + "连接失败.");
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 支持HTTP和HTTPS，测试URL是否有响应，响应代码是为200 则返回true，其他返回false
	 * 
	 * @param url
	 * @param timeout
	 * @return
	 */
	public static boolean TestConnectByUrl(String urlAddress, int timeout) {
		HttpURLConnection conn = null;
		try {
			// 该部分必须在获取connection前调用
			trustAllHttpsCertificates();
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
//					log.info("Warning: URL Host: " + urlHostName + " vs. "
//							+ session.getPeerHost());
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
			conn = (HttpURLConnection) new URL(urlAddress).openConnection();
			// 发送GET请求必须设置如下两行
			conn.setConnectTimeout(timeout);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			log.info("测试:" + urlAddress + "连接失败.原因: "+e.getMessage());
		}finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return false;
	}

	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}
}
