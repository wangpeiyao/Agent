package cn.com.hnisi.common;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.window.ApplicationWindow;

import cn.com.hnisi.model.UserModel;

/**
 * 全局配置
 * @author FengGeGe
 *
 */
public class GlobalConfig {
	/**
	 * 记录所有连接，程序退出时自动销毁
	 */
	private final static List<SocketChannel> socketList=new ArrayList<SocketChannel>();
	/**
	 * 记录打开的应用窗口，程序退出时销毁应用窗口
	 */
	private final static List<ApplicationWindow> winList=new ArrayList<ApplicationWindow>();

	/**
	 * 程序版本
	 * 
	 * @return
	 */
	public static String Version() {
		return "V17.8.30";
	}
	/**
	 * 默认编码方式：UTF-8
	 */
	public static String ENCODING="UTF-8";
	
	public static void setEncoding(String charset){
		ENCODING=charset;
	}
	/**
	 * 选择过的文件夹，用于设置打开文件夹对话框的默认目录
	 */
	public static  String historySelectFolder="";
	
	/**
	 * 需要发布的文件夹路径
	 */
	public static String ReleaseFolder="";
	
	
	public static UserModel User = new UserModel();

	
	public static UserModel getUser() {
		return User;
	}

	public static void setUser(UserModel user) {
		User = user;
	}
	
	public static List<SocketChannel> getSocketList() {
		return socketList;
	}

	/**
	 * 记录所有连接，退出时关闭所有连接
	 * @param socketChannel
	 */
	public static void saveSocketList(SocketChannel socketChannel) {
		if(socketChannel!=null){
			socketList.add(socketChannel);
		}
	}
	/**
	 * 断开所有Socket连接
	 */
	public static void disconnect(){
		for(SocketChannel channel:GlobalConfig.getSocketList()){
			if(channel!=null){
				try {
					channel.close();
				} catch (IOException e1) {
				}finally{
					channel=null;
				}
			}
		}
		socketList.clear();
	}
	/**
	 * 关闭所有应用窗口
	 */
	public static void abortApplicationWindows(){
		for(ApplicationWindow win:winList){
			if(win!=null){
				try {
					win.close();
				} catch (Exception e1) {
				}finally{
					win=null;
				}
			}
		}
	}
	/**
	 * 关闭程序，销毁所有进程实例
	 */
	public static void destroy(){
		disconnect();
		abortApplicationWindows();
		THREAD_POOL.shutdownNow();
	}
	
	/**
	 * 创建固定线程池。
	 */
	public final static ExecutorService THREAD_POOL = Executors
			.newFixedThreadPool(20);

	public static List<ApplicationWindow> getWinList() {
		return winList;
	}

	public static void setWinList(ApplicationWindow win) {
		winList.add(win);
	}
}
