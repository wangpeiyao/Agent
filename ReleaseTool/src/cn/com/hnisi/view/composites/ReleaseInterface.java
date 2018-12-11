package cn.com.hnisi.view.composites;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.agent.services.middleware.IMiddleware;
import cn.com.hnisi.agent.services.middleware.WebLogic;
import cn.com.hnisi.agent.services.middleware.WebSphere;
import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.CommandType;
import cn.com.hnisi.type.MiddlewareType;
import cn.com.hnisi.type.ShowType;
import cn.com.hnisi.view.component.MessageBoxUc;
import cn.com.hnisi.view.component.PopupMenuUc;

/**
 * 应用发布操作 和 结果显示界面
 * 
 * @author FengGeGe
 * 
 */
public class ReleaseInterface extends Composite {
	static Logger log = Logger.getLogger(ReleaseInterface.class);
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	private ServerModel server;
	StyledText txt_console;
	private CTabItem tabItem;

	/**
	 * 
	 * @param parent
	 * @param style
	 * @param server
	 *            ServerModel对象，存储了需要发布的应用关键时候
	 * @param releaseFolder
	 *            需要发布的文件夹路径
	 */
	public ReleaseInterface(Composite parent, int style, ServerModel server,
			CTabItem tabItem) {
		super(parent, SWT.NONE);
		this.server = server;
		this.tabItem = tabItem;
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));

		setLayout(new FormLayout());

		CTabFolder tab_processResult = new CTabFolder(this, SWT.FLAT);
		tab_processResult.setTabHeight(25);
		FormData fd_tab_processResult = new FormData();
		fd_tab_processResult.top = new FormAttachment(0, 31);
		fd_tab_processResult.bottom = new FormAttachment(100);
		fd_tab_processResult.right = new FormAttachment(100);
		fd_tab_processResult.left = new FormAttachment(0);
		tab_processResult.setLayoutData(fd_tab_processResult);
		tab_processResult.setBorderVisible(true);
		tab_processResult.setSingle(true);
		tab_processResult.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		formToolkit.adapt(tab_processResult);
		formToolkit.paintBordersFor(tab_processResult);
		tab_processResult.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));

		CTabItem tabItem_processResult = new CTabItem(tab_processResult,
				SWT.NONE);
		tabItem_processResult.setText(" \u5904\u7406\u60C5\u51B5");

		txt_console = new StyledText(tab_processResult, SWT.FULL_SELECTION | SWT.READ_ONLY | SWT.WRAP |SWT.V_SCROLL);
		txt_console.setIndent(5);
		txt_console.setRightMargin(5);
		txt_console.setLeftMargin(5);
		txt_console.setBottomMargin(5);
		txt_console.setTopMargin(5);
		txt_console.setWrapIndent(42);
		tabItem_processResult.setControl(txt_console);
		txt_console.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txt_console.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		txt_console.setMarginColor(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		txt_console.setFont(SWTResourceManager.getFont("微软雅黑", 10, SWT.NORMAL));
		formToolkit.adapt(txt_console);
		formToolkit.paintBordersFor(txt_console);
		tab_processResult.setSelection(tabItem_processResult);
		new PopupMenuUc(txt_console);

		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		toolBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		FormData fd_toolBar = new FormData();
		fd_toolBar.bottom = new FormAttachment(0, 31);
		fd_toolBar.left = new FormAttachment(0, 1);
		fd_toolBar.right = new FormAttachment(100);
		fd_toolBar.top = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);
		formToolkit.adapt(toolBar);
		formToolkit.paintBordersFor(toolBar);

		ToolItem tltm_stopServer = new ToolItem(toolBar, SWT.NONE);
		tltm_stopServer.setText("\u505C\u6B62\u5E94\u7528");
		tltm_stopServer.setImage(SWTResourceManager.getImage(ReleaseInterface.class, "/red.png"));
		tltm_stopServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageBoxUc.OpenConfirm(getShell(), "是否要停止应用？")) {
					stopServer();
				}
			}
		});
		tltm_stopServer.setWidth(55);
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setEnabled(false);
		tltmNewItem.setText("\u2192");

		ToolItem tltm_bakcUp = new ToolItem(toolBar, SWT.NONE);
		tltm_bakcUp.setImage(SWTResourceManager.getImage(ReleaseInterface.class, "/winrar.png"));
		tltm_bakcUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				backUp();
			}
		});
		tltm_bakcUp.setText("\u5907\u4EFD\u5E94\u7528");
		
		ToolItem tltmNewItem_1 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_1.setEnabled(false);
		tltmNewItem_1.setText("\u2192");

		ToolItem tltm_uploadFile = new ToolItem(toolBar, SWT.NONE);
		tltm_uploadFile.setImage(SWTResourceManager.getImage(ReleaseInterface.class, "/copy.png"));
		tltm_uploadFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				uploadFile();
			}
		});
		tltm_uploadFile.setText("\u4E0A\u4F20\u6587\u4EF6");
		
		ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.setEnabled(false);
		toolItem.setText("\u2192");

		ToolItem tltm_startServer = new ToolItem(toolBar, SWT.NONE);
		tltm_startServer.setImage(SWTResourceManager.getImage(ReleaseInterface.class, "/green.png"));
		tltm_startServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(MessageBoxUc.OpenConfirm(getShell(), "提醒：如果有数据脚本，请先执行脚本后再启动。\n继续启动应用请按“是”，取消请按“否”。")){
				 startServer();
				}
			}
		});
		tltm_startServer.setText("\u542F\u52A8\u5E94\u7528");
		
		ToolItem tltmNewItem_2 = new ToolItem(toolBar, SWT.SEPARATOR);
		tltmNewItem_2.setText("New Item");
		
		ToolItem tltm_clearConsole = new ToolItem(toolBar, SWT.NONE);
		tltm_clearConsole.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txt_console.setText("");
			}
		});
		tltm_clearConsole.setImage(SWTResourceManager.getImage(ReleaseInterface.class, "/clear.png"));
		tltm_clearConsole.setText("\u6E05\u7A7A\u8F93\u51FA");
		tab_processResult.setSelection(tabItem_processResult);
		
		//发布类型：0-发布应用1-上传文件
		if(server.getReleaseType()==null || server.getReleaseType().equals("0")){
			tltm_stopServer.setEnabled(true);
			tltm_startServer.setEnabled(true);
		}else{
			tltm_stopServer.setEnabled(false);
			tltm_startServer.setEnabled(false);
		}
	}

	/**
	 * 输出操作结果信息
	 * 
	 * @param server
	 * @param content
	 */
	public void consoleResult(final String type, final ResultModel rm) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (!txt_console.isDisposed()) {
					if (txt_console.getText().length() > 50000) {
						txt_console.replaceTextRange(0, 10000, "");
					}
					txt_console.append(type + ":  " + rm.getMsg() + " \n");
					txt_console.setSelection(txt_console.getText().length());

					if (tabItem != null && rm.getCode() != 0) {
						tabItem.setImage(SWTResourceManager.getImage(
								ReleaseInterface.class, "/plaint.png"));
						tabItem.setToolTipText(rm.getMsg());
					} else {
						tabItem.setImage(SWTResourceManager.getImage(
								ServerRelease.class, "/server_go.png"));
						tabItem.setToolTipText("");
					}
				}
			}
		});
	}

	/**
	 * 备份应用
	 */
	public void backUp() {
		process(CommandType.BACKUP);
	}

	/**
	 * 停止应用
	 */
	public void stopServer() {		
			if (server.getReleaseType() == null
					|| server.getReleaseType().equals("0")) {
				process(CommandType.STOP);
			}else{
			
			consoleResult(ShowType.STATUS.getValue(), new ResultModel(-1,"非应用"));
			}
	}

	/**
	 * 启动应用
	 */
	public void startServer() {
		if (server.getReleaseType() == null
				|| server.getReleaseType().equals("0")) {
			process(CommandType.START);
		}else{
			consoleResult(ShowType.STATUS.getValue(), new ResultModel(-1,"非应用"));
		}
	}

	public void uploadFile() {
		/*
		 * 需要上传的文件目录保存在全局变量：GlobalConfig.ReleaseFolder
		 * 只要在第2步选择了文件路径，在上传文件时，程序自动修改该全局变量的值。
		 */
		ResultModel rm = new ResultModel();
		if (GlobalConfig.ReleaseFolder.equals("")) {
			rm.setCode(-1);
			rm.setMsg("提示: 请先选择需要发布的文件夹（版本）路径");
			consoleResult(ShowType.STATUS.getValue(), rm);
		} else {
			File file = new File(GlobalConfig.ReleaseFolder);
			if (file.exists()) {
				if (file.isDirectory()) {
					process(CommandType.UPLOAD_FILE);
				} else {
					rm.setCode(-1);
					rm.setMsg("提示: 只能上传文件夹，不能单独上传文件，请重新选择");
					consoleResult(ShowType.STATUS.getValue(), rm);
				}
			} else {
				rm.setCode(-1);
				rm.setMsg("提示: 选择的文件夹路径 “" + GlobalConfig.ReleaseFolder
						+ "” 不存在，请重新选择");
				consoleResult(ShowType.STATUS.getValue(), rm);
			}
		}
	}

	public void clearConsole() {
		txt_console.setText("");
	}

	public void process(CommandType ct) {
		new ProcessThread(ct, this);
	}

	private class ProcessThread implements Runnable {
		// 命令类型
		CommandType comandType;
		// 界面对象，用于显示服务器响应的信息
		ReleaseInterface releaseInterface;
		// 中间件接口
		IMiddleware middleware;

		public ProcessThread(CommandType comandType,
				ReleaseInterface releaseInterface) {
			this.comandType = comandType;
			this.releaseInterface = releaseInterface;
			new Thread(this).start();
		}

		@Override
		public void run() {
			if (server == null) {
				return;
			}
			// 获取“中间件”类型
			if (server.getMiddlewareType().equalsIgnoreCase(
					MiddlewareType.WEBLOGIC.getValue())) {
				middleware = new WebLogic(releaseInterface, server);
			} else {
				 middleware = new WebSphere(releaseInterface, server);
			}
			switch (comandType) {
			case BACKUP:
				// 备份
				middleware.backup();
				break;
			case UPLOAD_FILE:
				// 上传文件
				middleware.uploadFile(GlobalConfig.ReleaseFolder);
				break;
			case STOP:
				// 停止应用
				middleware.stop();
				break;
			case START:
				// 启动应用
				middleware.start();
				break;
			default:
				log.info("不能识别的命令");
				break;
			}
		}

	}
}
