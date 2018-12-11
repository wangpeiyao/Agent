package cn.com.hnisi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sun.misc.BASE64Encoder;
import cn.com.hnisi.common.GlobalConfig;

/**
 * 工具类
 * 
 * @author FengGeGe
 * 
 */
public class Tool {
	static Logger log = Logger.getLogger(Tool.class);

	/**
	 * 利用MD5进行加密
	 * 
	 * @param str
	 *            待加密的字符串
	 * @return 加密后的字符串
	 */
	public static String EncoderByMd5(String str) {

		// 确定计算方法
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			BASE64Encoder base64en = new BASE64Encoder();
			String newstr = base64en.encode(md5.digest(str
					.getBytes(GlobalConfig.ENCODING)));
			return newstr;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static String getNowDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间yyyy-MM-dd HH:mm:ss:SSS
	 * 
	 * @return 返回时间类型 MM-dd HH:mm:ss
	 */
	public static String getNowDateMMDDHMSSS() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SSS");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间 yyyyMMddHHmmssSSS
	 * 
	 * @return 返回时间类型 yyyyMMddHHmmssSSS
	 */
	public static String getNowDateSSS() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 判断IP地址的合法性，这里采用了正则表达式的方法来判断 return true，合法
	 * */
	public static boolean checkIp(String text) {
		if (text != null && !text.equals("")) {
			// 定义正则表达式
			String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
					+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
			// 判断ip地址是否与正则表达式匹配
			if (text.matches(regex)) {
				// 返回判断信息
				return true;
			} else {
				// 返回判断信息
				return false;
			}
		}
		return false;
	}

	/**
	 * 计算百分比
	 * 
	 * @param member
	 *            分子
	 * @param denominator
	 *            分母(不能为0)
	 * @return
	 */
	public static String getPercent(double member, double denominator) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		return numberFormat.format(member / denominator * 100) + "%";
	}

	/**
	 * 让窗体屏幕居中显示
	 * 
	 * @param shell
	 * @param display
	 */
	public static void CenterScreen(Shell shell, Display display) {
		// 窗口屏幕居中显示
		shell.setLocation(display.getClientArea().width / 2
				- shell.getShell().getSize().x / 2,
				display.getClientArea().height / 2 - shell.getSize().y / 2);
	}

	/**
	 * 获取文件大小B\KB\MB\GB
	 * 
	 * @param size
	 * @return
	 */
	public static String getFileSize(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + " B";
		} else {
			size = size / 1024;// 取整数，判断是否大于1KB
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + " KB";// 如果小于1KB，直接返回
		} else {
			size = size / 1024;// 取整数，判断是否大于1MB
		}
		if (size < 1024) {
			// 因为如果以MB为单位的话，要保留最后1位小数，
			// 因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + " MB";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + " GB";
		}
	}

	/**
	 * 计算每秒下载速度
	 * 
	 * @param size
	 * @return
	 */
	public static String getSpeed(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + " B/S";
		} else {
			size = size / 1024;// 取整数，判断是否大于1KB
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + " KB/S";// 如果小于1KB，直接返回
		} else {
			size = size / 1024;// 取整数，判断是否大于1MB
		}
		if (size < 1024) {
			// 因为如果以MB为单位的话，要保留最后1位小数，
			// 因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + " MB/S";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + " GB/S";
		}
	}

	/**
	 * 生成一个UID
	 * 
	 * @return
	 */
	public static String getUUID() {
		return java.util.UUID.randomUUID().toString();
	}

	/**
	 * 将对象转化为二进制
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] objectToByte(Object obj) throws IOException {
		byte[] bytes = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(os);
		out.writeObject(obj);
		bytes = os.toByteArray();
		os.flush();
		out.flush();
		os.close();
		out.close();
		os = null;
		out = null;
		return bytes;
	}

	/**
	 * 将二进制转化为Object对象
	 * 
	 * @param bt
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object ByteToObject(byte[] bt) throws IOException,
			ClassNotFoundException {
		Object obj = null;
		ByteArrayInputStream is = null;
		ObjectInputStream in = null;
		try {
			is = new ByteArrayInputStream(bt);
			in = new ObjectInputStream(is);
		} catch (EOFException ex) {
			log.error(ex.getMessage());
		}
		obj = in.readObject();
		is.close();
		in.close();
		return obj;
	}

	/**
	 * 将文件转换为二进制
	 * 
	 * @param fileFath
	 * @return
	 * @throws IOException
	 */
	public static byte[] makeFileToByte(String fileFath) throws IOException {
		File file = new File(fileFath);
		FileInputStream fis = new FileInputStream(file);
		int length = (int) file.length();
		byte[] bytes = new byte[length];
		int temp = 0;
		int index = 0;
		while (true) {
			index = fis.read(bytes, temp, length - temp);
			if (index <= 0)
				break;
			temp += index;
		}
		fis.close();
		return bytes;
	}

	/**
	 * 将毫秒转化[h小时 m分 s秒 ms毫秒]
	 * 
	 * @param time
	 * @return h小时 m分 s秒 ms毫秒
	 */
	public static String millisecondFormat(long time) {
		long t = time;
		long h = 0;
		long m = 0;
		long s = 0;
		long ms = 0;
		h = t / 3600000;// 取小时
		t = t % 3600000;
		m = t / 60000;// 取分钟
		t = t % 60000;
		s = t / 1000;// 取秒
		t = t % 1000;
		ms = t;// 取毫秒

		return h + "小时 " + m + "分 " + s + "秒 " + ms + "毫秒";
	}

	/**
	 * 将路径中带空格的文件夹加双引号 例：C:\Documents and
	 * Settings\Administrator\桌面\domains\医疗9999.lnk
	 * 转为：C:\"Documents and Settings"\Administrator\桌面\domains\医疗9999.lnk
	 * 
	 * @param path
	 * @return
	 */
	public static String formatWindowsPathBlank(String path) {
		path = path.replace("\\", "/");
		String[] paths = path.split("/");
		String newPath = "";
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].indexOf(" ") > 0) {
				newPath += "\"" + paths[i] + "\"\\";
			} else {
				newPath += paths[i] + "\\";
			}
		}
		return newPath.substring(0, newPath.length() - 1);
	}

	/**
	 * 格式化文件路径
	 * 
	 * @param path
	 * @return
	 */
	public static String formatPath(String path) {
		File file = new File(path);
		return file.getPath();
	}

	/**
	 * 根据目录获取目录中所有的文件（含 路径，例：C:\folder\a.txt）
	 * 
	 * @param fileList
	 * @param dirPath
	 */
	public static void getFiles(List<String> fileList, String dirPath) {
		File file = new File(dirPath);
		if (file.isDirectory()) {
			for (String f : file.list()) {
				getFiles(fileList, dirPath + "\\" + f);
			}
		} else {
			if (fileList != null) {
				fileList.add(file.getPath());
			}
		}
	}

	public static String utfToIso(String str)
			throws UnsupportedEncodingException {
		return new String(str.getBytes("UTF-8"), "ISO8859-1");
	}

	/**
	 * 获取桌面路径
	 * 
	 * @return
	 */
	public static String getDesktopPath() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com = fsv.getHomeDirectory();
		return com.getPath();
	}

	/**
	 * 返回对象的字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if (obj != null) {
			return obj.toString();
		} else {
			return "";
		}
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj) {
		if (obj == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否为Windows操作系统
	 * 
	 * @return
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("window") ? true
				: false;
	}

	/**
	 * 判断是否为Aix操作系统
	 * 
	 * @return
	 */
	public static boolean isAix() {
		return System.getProperty("os.name").toLowerCase().contains("aix") ? true
				: false;
	}

	/**
	 * 判断是否为Linux操作系统
	 * 
	 * @return
	 */
	public static boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().contains("linux") ? true
				: false;
	}

	/**
	 * 运行命令
	 * @param commands
	 */
	public static void RunCommand(String commands[]) {
		ProcessBuilder pb = null;
		Process p = null;
		try {
			pb = new ProcessBuilder(commands);
			pb.redirectErrorStream(true);
			p = pb.start();
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			if(p!=null){
				p.destroy();
			}
		}
	}
	
	/**
	 * 检查是否为端口号
	 * @param port
	 * @throws Exception
	 */
	public static void checkPort(String port) throws Exception{
		try {
			int temp_port = Integer.parseInt(port);
			if (temp_port < 0 || temp_port > 65535) {
				throw new Exception("端口号，必需是一个1~65535的正整数");
			}
		} catch (Exception e) {
			throw new Exception("端口号，必需是一个1~65535的正整数");
		}
	}
}
