package cn.com.hnisi.view;


import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.database.InitEnvironment;
import cn.com.hnisi.type.StepType;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.component.MessageBoxUc;
import cn.com.hnisi.view.composites.ServerList;
import cn.com.hnisi.view.composites.ServerRelease;
import cn.com.hnisi.view.composites.Welcome;
import cn.com.hnisi.view.dialog.AboutUs;
import cn.com.hnisi.view.dialog.ConfigServer;
import cn.com.hnisi.view.dialog.CreateFolder;
import cn.com.hnisi.view.dialog.Group;
import cn.com.hnisi.view.dialog.Login;
import cn.com.hnisi.view.dialog.ManageAgentPassWord;
import cn.com.hnisi.view.dialog.ModfiPassWord;

/**
 * 主窗口
 * 
 * @author 温志锋
 * @date 2017-08-29
 * 
 */
public class MainForm {
	final static Logger log=Logger.getLogger(MainForm.class);
	private static String PRINT_DECOLLATOR = "*";// 打印分割符
	private static int PRINT_LENGTH = 70;// 打印系统信息长度


	private final static Shell shlV = new Shell(SWT.MIN|SWT.CLOSE|SWT.MAX|SWT.RESIZE);// 窗体
	private final static StackLayout sl_shlV = new StackLayout();// 窗体布局方式
	//登录背景
	private static Welcome welcome = new Welcome(shlV,
			SWT.NONE);
	/**
	 * 应用列表
	 */
	private static ServerList serverList = new ServerList(shlV, SWT.NONE);

	/**
	 * 发布界面
	 */
	public final static ServerRelease serverRelease = new ServerRelease(shlV,
			SWT.NONE);
	
	
	/**
	 * ApplicationWindow 标题名称
	 */
	private final static String TITLE="程序发布管理工具-客户端 "+GlobalConfig.Version();
 	
	/**
	 * 主函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		printCopyright();
		//初始化要在程序的最前面
		try {
			//初始化环境和数据库，新环境自动创建不必拷贝数据库文件。
			InitEnvironment.Init();
		} catch (Exception e) {
			log.error("初始化出错，程序退:"+e.getMessage());
			MessageBoxUc.OpenError(shlV, "初始化出错，程序退出！"+e.getMessage());
			shlV.dispose();
		}
		Display display = Display.getDefault();
		
		shlV.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if(closeWindows()){
					e.doit=true;
				}else{
					e.doit=false;
				}
			}
		});
		//窗口大小
		shlV.setSize(1000, 650);
		shlV.setMinimumSize(new Point(800, 600));
		shlV.setImage(SWTResourceManager.getImage(MainForm.class, "/Dropbox.png"));
	
		shlV.setText(TITLE);
		// 窗口屏幕居中显示
		Tool.CenterScreen(shlV, display);
		shlV.setLayout(sl_shlV);
		MenuBar();

		shlV.open();
	
		sl_shlV.topControl=welcome;
		
		shlV.layout();
		//需要先登录
		Login login=new Login(shlV);
		if(login.open()==0){
			SetTopControl(StepType.STEP1);
			shlV.setText(TITLE+" 用户:" + GlobalConfig.getUser().getUsername());
			//这是最大化窗口
			shlV.setMaximized(true);
			//shlV.setFullScreen(true);//这将全屏看不到标题栏
		}else{
			log.info("已取消登录，系统已退出。");
			shlV.dispose();
		}
		while (!shlV.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * 程序菜单
	 */
	private static void MenuBar() {
	
		// 添加主菜单
		Menu menu = new Menu(shlV, SWT.BAR);
		shlV.setMenuBar(menu);
		// 一级菜单：文件(F)
		MenuItem menuItem_file = new MenuItem(menu, SWT.CASCADE);
		menuItem_file.setText("文件(&F)");

		// 添加二级菜单到一级菜单
		Menu menu_file = new Menu(menuItem_file);
		menuItem_file.setMenu(menu_file);
		
		MenuItem mntm_addServer = new MenuItem(menu_file, SWT.NONE);
		mntm_addServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigServer config = new ConfigServer(shlV,null);
				config.open();
				serverList.refresh();
			}
		});
		mntm_addServer.setImage(SWTResourceManager.getImage(MainForm.class, "/server_add.png"));
		mntm_addServer.setText("添加应用");
		//管理分组
		MenuItem mntm_group = new MenuItem(menu_file, SWT.NONE);
		mntm_group.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Group group=new Group(shlV);
				group.open();
			}
		});
		mntm_group.setImage(SWTResourceManager.getImage(MainForm.class, "/chart_organisation_add.png"));
		mntm_group.setText("管理分组(&G)");
		MenuItem mntm_tool = new MenuItem(menu, SWT.CASCADE);
		mntm_tool.setText("\u5DE5\u5177(&T)");
		//锁屏
		MenuItem menu_lock = new MenuItem(menu_file, SWT.NONE);
		menu_lock.setImage(null);
		menu_lock.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Login login=new Login(shlV,true);
				login.setShellTitle("安全登录-锁屏");
				login.open();
			}
			
		});
		menu_lock.setText("锁屏(&L)");
		//修改工具登录密码
		MenuItem menu_modfiPassword = new MenuItem(menu_file, SWT.NONE);
		menu_modfiPassword.setImage(SWTResourceManager.getImage(MainForm.class, "/lock.png"));
		menu_modfiPassword.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ModfiPassWord mpw=new ModfiPassWord(shlV);
				mpw.open();
			}
		});
		menu_modfiPassword.setText("修改密码(&M)");
		
		new MenuItem(menu_file, SWT.SEPARATOR);

		// 添加二级菜单项“退”到二级菜单
		MenuItem menu_closeApplication = new MenuItem(menu_file, SWT.NONE);
		menu_closeApplication.setImage(SWTResourceManager.getImage(MainForm.class, "/out.png"));
		menu_closeApplication.setText("退出(&C)");
		menu_closeApplication.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(closeWindows()){
					GlobalConfig.destroy();
					shlV.dispose();
				}
			}
		});
		

		
		Menu menu_3 = new Menu(mntm_tool);
		mntm_tool.setMenu(menu_3);
		
	
		//根据录入的路径创建文件夹
		MenuItem mntm_createFolder = new MenuItem(menu_3, SWT.NONE);
		mntm_createFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateFolder createFolder=new CreateFolder(shlV);
				createFolder.open();
			}
		});
		mntm_createFolder.setImage(SWTResourceManager.getImage(MainForm.class, "/folder_add.png"));
		mntm_createFolder.setText("\u521B\u5EFA\u6587\u4EF6\u5939(&F)");
		
		MenuItem mntm_set = new MenuItem(menu, SWT.CASCADE);
		mntm_set.setText("\u670D\u52A1\u7AEFAgent(&A)");
		
		Menu menu_1 = new Menu(mntm_set);
		mntm_set.setMenu(menu_1);
		//修改服务端Agent密码
		MenuItem mntm_agentsPassword = new MenuItem(menu_1, SWT.NONE);
		mntm_agentsPassword.setImage(SWTResourceManager.getImage(MainForm.class, "/shield16.png"));
		mntm_agentsPassword.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ManageAgentPassWord agentPwd=new ManageAgentPassWord(shlV, "", "");
				agentPwd.open();
				//修改后需要重新加载应用
				serverList.getServerData();
			}
		});
		mntm_agentsPassword.setText("\u4FEE\u6539\u8BBF\u95EE\u5BC6\u7801(&S)");

		MenuItem menu_help = new MenuItem(menu, SWT.CASCADE);
		menu_help.setText("帮助(&H)");

		Menu menu_aboutUs = new Menu(menu_help);
		menu_help.setMenu(menu_aboutUs);
		MenuItem menuItem_aboutUs = new MenuItem(menu_aboutUs, SWT.NONE);
		menuItem_aboutUs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AboutUs ab = new AboutUs(shlV);
				ab.open();
			}
		});
		menuItem_aboutUs.setText("关于我们(&A)");
	}

	/**
	 * 关闭应用
	 */
    private static boolean closeWindows(){
        if(MessageBoxUc.OpenConfirm(shlV, "退出程序前，请确认已发布完成！\r\n确定退出请按“ 是  ”，取消退出请按“ 否  ”。")){
        	GlobalConfig.destroy();
        	welcome.dispose();
        	serverList.dispose();
        	serverRelease.dispose();
        	log.info("已退出系统.");
        	return true;
        }else{
        	return false;
        }
	
    }
	/**
	 * 显示“步骤”
	 * 
	 * @第1步：ServerList
	 * @第2步：SelectFolder
	 * @第3步：ServerRelease
	 * 
	 * @param step
	 *            StepType杖举
	 */
	public static void SetTopControl(StepType step) {
		
		switch (step) {
		case STEP1:
			sl_shlV.topControl = serverList;
			break;
		case STEP2:
			serverRelease.setReleaseText(GlobalConfig.ReleaseFolder);
			sl_shlV.topControl = serverRelease;
			break;
		default:
			return;
		}
		shlV.layout();
	}
	/**
	 * 预生成控件
	 */
	public static void createServerTab(){
		Thread th=new Thread(new Runnable() {		
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				Display.getDefault().syncExec(new Runnable() {					
					@Override
					public void run() {
						serverRelease.createServerTab(serverList.getSelectedServers());
					}
				});
			
			}
		});
		th.start();
	}

	/**
	 * 打印程序信息
	 */
	private static void printCopyright() {
		printLines();
		printInfo(TITLE, 2, 2);
		printLines();
		printInfo("广州华资软件有限公司", 1, 0);
		printInfo("www.sinobest.cn", 1, 0);
		printInfo("Version 2017", 1, 1);
		printInfo("Copyright (C) 2017 All Rights Reserved.", 0, 1);
		printLines();
	}

	/**
	 * 在内容前面和后面换行
	 * 
	 * @param content
	 *            内容
	 * @param frontLine
	 *            前面换行数
	 * @param backLine
	 *            后面换行数
	 */
	private static void printInfo(String content, int frontLine, int backLine) {
		for (int i = 0; i < frontLine; i++) {
			printInfo("  ");
		}
		printInfo(content);
		for (int i = 0; i < backLine; i++) {
			printInfo("  ");
		}
	}

	/**
	 * 打印系统相关信息
	 * 
	 * @param content
	 *            要显示的内容
	 * @return
	 */
	private static void printInfo(String content) {
		String fillChar = "";
		char[] ca = content.toCharArray();
		int len = 0;// 计算内容的实际长度
		for (char c : ca) {
			if (c >= 19968 && c <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				len += 2;// 中文计算为2个字符长度
			} else {
				len += 1;
			}
		}
		if (len >= PRINT_LENGTH) {
			System.out.println(content);
			return;
		}
		// 避免出现奇数，导致不能对齐
		if (len % 2 != 0 && PRINT_LENGTH % 2 == 0) {
			//内容长度为奇数时，在内容后面加一个长度为1的空格，凑成偶数
			content += " ";
		}
		for (int i = 0; i < (PRINT_LENGTH - len - 2) / 2; i++) {
			fillChar += " ";// 除去要显示的内容长度后，计算需要填充两边的长度，并且用空格字符填充
		}
		System.out.println(PRINT_DECOLLATOR + fillChar + content + fillChar
				+ PRINT_DECOLLATOR);
	}

	/**
	 * 用分割符PRINT_DECOLLATOR，打印一行长度为PRINT_LENGTH的线条
	 * 
	 * @return
	 */
	private static void printLines() {
		String str = "";
		for (int i = 0; i < PRINT_LENGTH; i++) {
			str += PRINT_DECOLLATOR;
		}
		System.out.println(str);
	}
}
