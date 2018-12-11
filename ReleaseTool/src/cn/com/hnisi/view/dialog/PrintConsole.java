package cn.com.hnisi.view.dialog;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.agent.services.function.ServerConsoleThread;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.view.component.PopupMenuUc;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

/**
 * 查看控件台输出
 * 
 * @author FengGeGe
 * 
 */
public class PrintConsole extends ApplicationWindow {
	private Shell shell;
	private ServerModel server;
	private StyledText txt_console;
	private ServerConsoleThread serverConsoleThread = null;

	private ToolItem tltm_btn_suspend;
	private ToolItem tltm_btn_resume;
	private ToolItem tltm_btn_clear;
	private ToolItem tltm_btn_refresh;
	private ToolItem tltmNewItem;
	private ToolItem tltmNewItem_1;
	private ToolItem tltmNewItem_info;
	private ToolItem tltm_switchEncoding;
	private String encoding="UTF-8";//默认为utf-8

	private Shell thisShell=null;
	private String title="";//标题
	/**
	 * Create the application window.
	 */
	public PrintConsole(Shell shell, ServerModel server) {
		super(null);
		this.shell = shell;
		this.server = server;
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if (serverConsoleThread != null) {
					serverConsoleThread.interrupt();
				}
			}
		});
		setShellStyle( SWT.ON_TOP | SWT.CLOSE | SWT.MAX | SWT.MIN
				 | SWT.RESIZE);
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		parent.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		shell.setImage(SWTResourceManager.getImage(PrintConsole.class,
				"/Dropbox.png"));
		Composite container = new Composite(parent, SWT.NO_BACKGROUND);
		container.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		container.setLayout(new FormLayout());

		Composite composite = new Composite(container, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(100, -2);
		fd_composite.right = new FormAttachment(100);
		fd_composite.top = new FormAttachment(0, 23);
		fd_composite.left = new FormAttachment(0);
		composite.setLayoutData(fd_composite);
		composite.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		txt_console = new StyledText(composite, SWT.WRAP | SWT.V_SCROLL);
		txt_console.setTouchEnabled(true);
		txt_console.setBottomMargin(5);
		txt_console.setTopMargin(3);
		txt_console.setRightMargin(8);
		txt_console.setLeftMargin(8);
		txt_console.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txt_console.setFont(SWTResourceManager.getFont("黑体", 11, SWT.NORMAL));
		txt_console.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txt_console.setTextLimit(1000);
		txt_console
				.setText("\u6B63\u5728\u8FDE\u63A5\u670D\u52A1\u5668\uFF0C\u8BF7\u7A0D\u5019...\r\n");

		new PopupMenuUc(txt_console);

		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.right = new FormAttachment(100, 1);
		fd_toolBar.top = new FormAttachment(0, -2);
		fd_toolBar.left = new FormAttachment(0, 1);
		toolBar.setLayoutData(fd_toolBar);

		tltm_btn_refresh = new ToolItem(toolBar, SWT.NONE);
		tltm_btn_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectServer();
			}
		});
		tltm_btn_refresh.setToolTipText("\u91CD\u65B0\u8FDE\u63A5");
		tltm_btn_refresh.setImage(SWTResourceManager.getImage(
				PrintConsole.class, "/refresh.png"));

		tltmNewItem = new ToolItem(toolBar, SWT.SEPARATOR);
		tltmNewItem.setText("New Item");
		//暂停
		tltm_btn_suspend = new ToolItem(toolBar, SWT.NONE);
		tltm_btn_suspend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (serverConsoleThread != null) {
					serverConsoleThread.setSuspend();
					tltm_btn_suspend.setEnabled(false);
					tltm_btn_resume.setEnabled(true);
					showStatus(0);
				}
			}
		});
		tltm_btn_suspend.setImage(SWTResourceManager.getImage(
				PrintConsole.class, "/pause.png"));
		tltm_btn_suspend.setToolTipText("\u6682\u505C\u6253\u5370");
		//同步中
		tltm_btn_resume = new ToolItem(toolBar, SWT.NONE);
		tltm_btn_resume.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (serverConsoleThread != null) {
					serverConsoleThread.setResume();
					tltm_btn_suspend.setEnabled(true);
					tltm_btn_resume.setEnabled(false);
					showStatus(1);
				}
			}
		});
		tltm_btn_resume.setToolTipText("\u7EE7\u7EED\u6253\u5370");
		tltm_btn_resume.setImage(SWTResourceManager.getImage(
				PrintConsole.class, "/go.png"));

		tltm_btn_clear = new ToolItem(toolBar, SWT.NONE);
		tltm_btn_clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txt_console.setText("");
			}
		});
		tltm_btn_clear.setToolTipText("\u6E05\u7A7A\u6253\u5370");
		tltm_btn_clear.setImage(SWTResourceManager.getImage(PrintConsole.class,
				"/clear.png"));
		
				tltmNewItem_1 = new ToolItem(toolBar, SWT.SEPARATOR);
				tltmNewItem_1.setText("New Item");
		
		tltm_switchEncoding = new ToolItem(toolBar, SWT.NONE);
		tltm_switchEncoding.setToolTipText("\u7F16\u7801\u65B9\u5F0F:UTF-8\u6216GBK");
		tltm_switchEncoding.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(encoding.toLowerCase().contains("gbk")){
					encoding="UTF-8";
				}else{
					encoding="GBK";
				}
				tltm_switchEncoding.setText("切换编码方式:"+encoding);
				connectServer();
			}
		});
		tltm_switchEncoding.setText("切换编码方式:"+encoding);

		tltmNewItem_info = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_info.setEnabled(false);
		tltmNewItem_info
				.setText("   \u63D0\u793A\uFF1A\u672C\u7A97\u53E3\u5B9E\u65F6\u663E\u793A\u5E94\u7528\u540E\u53F0\u65E5\u5FD7\u8F93\u51FA\uFF0C\u5F53\u6709\u4E1A\u52A1\u8BF7\u6C42\u4EA7\u751F\u65F6\uFF0C\u5C06\u4F1A\u540C\u6B65\u6253\u5370\u5230\u6B64\u7A97\u53E3.");
		connectServer();
		return container;
	}

	/**
	 * 连接服务器应用
	 */
	public void connectServer() {
		txt_console.setText("");
		if (serverConsoleThread != null && !serverConsoleThread.isInterrupted()) {
			serverConsoleThread.interrupt();
		}
		serverConsoleThread = new ServerConsoleThread(this, this.server,this.encoding);
		serverConsoleThread.start();
		tltm_btn_suspend.setEnabled(true);
		tltm_btn_resume.setEnabled(false);
		showStatus(1);
	}

	public void showSyslogText(final String text) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (!txt_console.isDisposed()) {

					txt_console.append(text + "\n");
					txt_console.setSelection(txt_console.getText().length());
				}
			}
		});
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				if(serverConsoleThread!=null){
					//关闭连接
					serverConsoleThread.closeSocket();
				}
			}
		});
		newShell.setImage(SWTResourceManager.getImage(PrintConsole.class,
				"/action_log.png"));
		newShell.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		super.configureShell(newShell);
		if (server != null) {
			title=server.getServerIp() + ":"
					+ server.getDomainPort() + "-控制台";
			newShell.setText(title);
		}
		thisShell=newShell;
	}
	
	/**
	 * 设置标题内容，显示状态,0-暂停,1-监控
	 * @param titleText
	 */
	private void showStatus(int status){
		if(thisShell!=null){
			if(status==0){
				thisShell.setText(title+"--已暂停");
			}else{
				thisShell.setText(title+"--同步中");
			}
		}
	}
	/**
	 * 屏幕居中显示
	 */
	@Override
	protected Point getInitialLocation(Point initialSize) {
		int width = shell.getMonitor().getClientArea().width;
		int height = shell.getMonitor().getClientArea().height;
		int x = getInitialSize().x;
		int y = getInitialSize().y;
		if (x > width) {
			getInitialSize().x = width;
		}
		if (y > height) {
			getInitialSize().y = height;
		}
		Point p = new Point((width - x) / 2, (height - y) / 2);
		return p;
	}
	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(949, 568);
	}
}
