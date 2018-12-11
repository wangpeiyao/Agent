package cn.com.hnisi.view.dialog;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.agent.services.function.AgentTestConnect;
import cn.com.hnisi.dao.ClusterServicesDAO;
import cn.com.hnisi.dao.ServerModelDAO;
import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.model.ClusterServicesModel;
import cn.com.hnisi.model.ConsoleAccountModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.util.ComboThread;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.component.ClusterServicesForm;
import cn.com.hnisi.view.component.MessageBoxUc;

/**
 * 应用服务器的新增、编辑管理界面
 * 
 * @author FengGeGe
 * 
 */
public class ConfigServer extends TitleAreaDialog {
	static Logger log = Logger.getLogger(ConfigServer.class);
	private Shell shell;
	private Text txt_serverIp;
	private Text txt_domainPort;
	private Text txt_domainPath;
	private Text txt_appPath;
	private Text txt_agentPort;
	private CCombo cbo_serverGroup;
	private CCombo cbo_middlewareType;
	private CCombo cbo_systemType;
	SQLiteDatabase sql = SQLiteDatabase.getInstance();
	private String serverId = null;
	private String sourcePassword = "";
	private CTabFolder tabFolder_all;
	private CTabItem tabItem_serverConifg;// 应用配置
	private CTabItem tabItem_weblogicCluster;// 集群配置
	private Text txt_name;
	private Text txt_backUpPath;
	private Text txt_appVerification;
	private Text txt_agentPassword;
	private Button rdb_compressedFolder;// 压缩文件
	private Button rdb_copyfolder;// 复制文件
	private Button btn_browser;
	private CCombo cbo_releaseType;// 发布类型

	private ServerModel serverModel = null;
	// 集群管理
	private ScrolledComposite scrolledComposite_cluster;
	private Composite composite_cluster;
	private List<ClusterServicesModel> clusterServerModelList = new ArrayList<ClusterServicesModel>();
	 // 添加集群服务
	private ToolItem btn_addClusterServer;
	private Button cbx_adminServer;
	private Button cbx_clusterComputer;
	private Button cbx_defaultServer;
	private Link link_manageUser;
	private Link link_browerDomainFromAgent;
	// 控制台管理账号
	private ConsoleAccountModel consoleAccount = new ConsoleAccountModel();

	private Text txt_serverName;
	private CLabel lbl_serverName;

	/**
	 * 添加应用服务器
	 * 
	 * @param parentShell
	 * @param serverId
	 *            应用ID
	 * @wbp.parser.constructor
	 */
	public ConfigServer(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
		this.shell = parentShell;
		this.serverId = null;
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * 编辑应用服务器
	 * 
	 * @param parentShell
	 * @param serverId
	 *            应用ID
	 * @wbp.parser.constructor
	 */
	public ConfigServer(Shell parentShell, String serverId) {
		super(parentShell);
		setHelpAvailable(false);
		this.shell = parentShell;
		this.serverId = serverId;
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceManager.getImage(ConfigServer.class,
				"/cluster_titleimage.png"));
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setMessage("\u5E26\u201C*\u201D\u4E3A\u5FC5\u5F55\u9879\uFF0C\u6240\u6709\u9879\u5747\u662F\u6307\u9700\u8981\u53D1\u5E03\u7684\u8FDC\u7A0B\u8BA1\u7B97\u673A\u4E2D\u7684\u8DEF\u5F84\u548C\u7AEF\u53E3.");
		setTitle("\u914D\u7F6E\u5E94\u7528\u670D\u52A1");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_container = new GridData(GridData.FILL_BOTH);
		gd_container.widthHint = 1048;
		container.setLayoutData(gd_container);

		/**
		 * 添加应用群
		 */

		tabFolder_all = new CTabFolder(container, SWT.BORDER);
		tabFolder_all.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				 CTabItem ti=(CTabItem)e.item;
				 if(ti!=null && ti==tabItem_weblogicCluster){
					 loadClusterServerList(getCurrentServerModel().getId());
				 }
			}
		});
		tabFolder_all.setTouchEnabled(true);
		tabFolder_all.setTabHeight(28);
		tabFolder_all.setMinimumCharacters(28);
		tabFolder_all.setSelectionBackground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));

		tabItem_serverConifg = new CTabItem(tabFolder_all, SWT.NONE);
		tabItem_serverConifg.setImage(SWTResourceManager.getImage(
				ConfigServer.class, "/server.png"));
		tabItem_serverConifg.setText("\u5E94\u7528\u53C2\u6570\u914D\u7F6E ");

		Composite com_server = new Composite(tabFolder_all, SWT.NONE);
		tabItem_serverConifg.setControl(com_server);

		tabItem_weblogicCluster = new CTabItem(tabFolder_all, SWT.NONE);
		tabItem_weblogicCluster.setImage(SWTResourceManager.getImage(ConfigServer.class, "/dns.png"));
		tabItem_weblogicCluster.setText("WebLogic\u96C6\u7FA4 ");

		Composite com_cluster = new Composite(tabFolder_all, SWT.NONE);
		tabItem_weblogicCluster.setControl(com_cluster);

		Group group_clusterType = new Group(com_cluster, SWT.NONE);
		group_clusterType.setFont(SWTResourceManager.getFont("微软雅黑", 9,
				SWT.BOLD));
		group_clusterType.setText("\u5E94\u7528\u7C7B\u578B");
		group_clusterType.setBounds(2, 1, 778, 63);

		cbx_adminServer = new Button(group_clusterType, SWT.RADIO);
		cbx_adminServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_addClusterServer.setEnabled(true);
			}
		});
		cbx_adminServer.setBounds(179, 31, 240, 17);
		cbx_adminServer.setText("\u96C6\u7FA4\u603B\u63A7\u5236\u7AEF(\u542F\u52A8\u7BA1\u7406\u5E94\u7528\u548C\u670D\u52A1)");

		cbx_clusterComputer = new Button(group_clusterType, SWT.RADIO);
		cbx_clusterComputer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_addClusterServer.setEnabled(true);
			}
		});
		cbx_clusterComputer.setBounds(445, 31, 171, 17);
		cbx_clusterComputer.setText("\u96C6\u7FA4\u673A\u5668(\u53EA\u542F\u52A8\u670D\u52A1)");
		
		cbx_defaultServer = new Button(group_clusterType, SWT.RADIO);
		cbx_defaultServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_addClusterServer.setEnabled(false);
			}
		});
		cbx_defaultServer.setSelection(true);
		cbx_defaultServer.setBounds(5, 31, 160, 17);
		cbx_defaultServer.setText("\u9ED8\u8BA4(\u666E\u901A\u5E94\u7528-\u975E\u96C6\u7FA4)");

		Composite composite = new Composite(com_cluster, SWT.BORDER);
		composite.setBounds(1, 67, 779, 456);

		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(0, 0, 779, 25);

		// 添加集群服务
		btn_addClusterServer = new ToolItem(toolBar, SWT.NONE);
		btn_addClusterServer.setEnabled(false);
		btn_addClusterServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addClusterServerForm();
			}
		});
		btn_addClusterServer.setImage(SWTResourceManager.getImage(ConfigServer.class, "/add.png"));
		btn_addClusterServer.setText("\u65B0\u589E\u670D\u52A1");
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setEnabled(false);
		tltmNewItem.setText("\u63D0\u793A\uFF1A\u201C\u670D\u52A1\u540D\u79F0\u201D\u3001\u201C\u7BA1\u7406\u5730\u5740\u201D\u548C\u201C\u5E94\u7528\u5B58\u653E\u8DEF\u5F84\u201D\u4E0D\u80FD\u4E3A\u7A7A\uFF0C\u5426\u5219\u4E0D\u4F1A\u88AB\u4FDD\u5B58\uFF01");

		scrolledComposite_cluster = new ScrolledComposite(composite, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite_cluster.setAlwaysShowScrollBars(true);
		scrolledComposite_cluster.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		scrolledComposite_cluster.setBounds(-1, 26, 780, 426);
		scrolledComposite_cluster.setExpandHorizontal(true);
		scrolledComposite_cluster.setExpandVertical(true);
		// 集群服务面板
		composite_cluster = new Composite(scrolledComposite_cluster, SWT.NONE);
		composite_cluster.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		scrolledComposite_cluster.setContent(composite_cluster);
		scrolledComposite_cluster.setMinSize(composite_cluster.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
		com_server.setLayout(new FillLayout(SWT.HORIZONTAL));

		ScrolledComposite scrolledComposite = new ScrolledComposite(com_server,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setMinHeight(500);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite_1 = new Composite(scrolledComposite, SWT.NONE);
		composite_1.setLayout(new FormLayout());

		Group grpagent = new Group(composite_1, SWT.NONE);
		grpagent.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		FormData fd_grpagent = new FormData();
		fd_grpagent.bottom = new FormAttachment(0, 62);
		fd_grpagent.top = new FormAttachment(0, 4);
		fd_grpagent.right = new FormAttachment(100, -2);
		fd_grpagent.left = new FormAttachment(0, 2);
		grpagent.setLayoutData(fd_grpagent);
		grpagent.setText("AGENT\u4FE1\u606F");

		CLabel lbl_ip = new CLabel(grpagent, SWT.NONE);
		lbl_ip.setBounds(20, 24, 96, 23);
		lbl_ip.setAlignment(SWT.RIGHT);
		lbl_ip.setText("*\u670D\u52A1\u5668IP\u5730\u5740");

		txt_serverIp = new Text(grpagent, SWT.BORDER);
		txt_serverIp.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		txt_serverIp.setBounds(128, 24, 167, 23);
		txt_serverIp
				.setToolTipText("\u5E94\u7528\u670D\u52A1\u5668\u7684IP\u5730\u5740\r\n\u4F8B\uFF1A128.110.9.72");

		CLabel lbl_agentPort = new CLabel(grpagent, SWT.NONE);
		lbl_agentPort.setBounds(301, 24, 79, 23);
		lbl_agentPort.setAlignment(SWT.RIGHT);
		lbl_agentPort.setText("*AGENT\u7AEF\u53E3");

		txt_agentPort = new Text(grpagent, SWT.BORDER);
		txt_agentPort.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		txt_agentPort.setBounds(385, 24, 104, 23);
		txt_agentPort.setText("7788");

		Label lblNewLabel_1 = new Label(grpagent, SWT.NONE);
		lblNewLabel_1.setBounds(491, 27, 71, 17);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setText("AGENT\u5BC6\u7801");

		txt_agentPassword = new Text(grpagent, SWT.BORDER | SWT.PASSWORD);
		txt_agentPassword
				.setToolTipText("\u5982\u679C\u672A\u8BBE\u7F6E\uFF0C\u9ED8\u8BA4\u4E3A\u7A7A\u3002");
		txt_agentPassword.setBounds(567, 24, 104, 23);

		// 测试Agent
		Button btn_testAgent = new Button(grpagent, SWT.NONE);
		btn_testAgent.setBounds(683, 22, 77, 27);
		btn_testAgent.setImage(SWTResourceManager.getImage(ConfigServer.class,
				"/server_go.png"));
		btn_testAgent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (txt_serverIp.getText().trim().length() <= 0) {
					MessageBoxUc.OpenOk(shell, "“IP地址”不能为空!");
					txt_serverIp.setFocus();
					return;
				} else {
					if (!Tool.checkIp(txt_serverIp.getText())) {
						MessageBoxUc.OpenOk(shell, "请输入合法的IP地址格式!");
						txt_serverIp.setFocus();
						return;
					}
				}
				try {
					int port = Integer.parseInt(txt_agentPort.getText());
					if (port < 0 || port > 65535) {
						MessageBoxUc.OpenOk(shell,
								"“Agent端口号”请输入一个1~65535的正整数，例：7788");
						txt_agentPort.setFocus();
						return;
					}
				} catch (Exception ex) {
					MessageBoxUc.OpenOk(shell,
							"“Agent端口号”请输入一个1~65535的正整数，例：7788");
					txt_agentPort.setFocus();
					return;
				}
				String pwd = "";
				// Agent服务端保存的密码是经过MD5加载的，这里需要加密后再发送。但是，数据库保存的值是经过加密，新录入的没有加密。
				if (sourcePassword != null && sourcePassword.length() > 0) {
					if (sourcePassword.equals(txt_agentPassword.getText())) {
						pwd = txt_agentPassword.getText();
					} else {
						try {
							pwd = Tool.EncoderByMd5(txt_agentPassword.getText());
						} catch (Exception ee) {
							ee.printStackTrace();
							pwd = "";
						}
					}
				} else {
					try {
						pwd = Tool.EncoderByMd5(txt_agentPassword.getText());
					} catch (Exception ee) {
						ee.printStackTrace();
						pwd = "";
					}
				}
				MessageBoxUc.OpenOk(
						shell,
						AgentTestConnect.connectAgent(txt_serverIp.getText(),
								Integer.parseInt(txt_agentPort.getText()), pwd)
								.getMsg());
			}
		});
		btn_testAgent.setText("\u8FDE\u63A5");

		Group grpDomain = new Group(composite_1, SWT.NONE);
		grpDomain.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		FormData fd_grpDomain = new FormData();
		fd_grpDomain.top = new FormAttachment(0, 62);
		grpagent.setTabList(new Control[] { txt_serverIp, txt_agentPort,
				txt_agentPassword, btn_testAgent, lbl_ip, lbl_agentPort });
		fd_grpDomain.bottom = new FormAttachment(100, -1);
		fd_grpDomain.right = new FormAttachment(100, -2);
		fd_grpDomain.left = new FormAttachment(0, 2);
		grpDomain.setLayoutData(fd_grpDomain);
		grpDomain.setText("\u5E94\u7528\u914D\u7F6E\u4FE1\u606F");

		CLabel lbl_systemType = new CLabel(grpDomain, SWT.NONE);
		lbl_systemType.setBounds(8, 183, 107, 23);
		lbl_systemType.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_FOREGROUND));
		lbl_systemType.setAlignment(SWT.RIGHT);
		lbl_systemType.setText("\u64CD\u4F5C\u7CFB\u7EDF");
		// 操作系统
		cbo_systemType = new CCombo(grpDomain, SWT.BORDER | SWT.FLAT);
		cbo_systemType.setFont(SWTResourceManager
				.getFont("微软雅黑", 9, SWT.NORMAL));
		cbo_systemType.setBounds(128, 184, 168, 21);
		cbo_systemType.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_FOREGROUND));
		cbo_systemType
				.setToolTipText("\u8BF7\u9009\u62E9Agent\u6240\u5728\u7684\u64CD\u4F5C\u7CFB\u7EDF");
		cbo_systemType.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				loadComboSystemType();
			}
		});
		cbo_systemType.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		cbo_systemType.setEditable(false);

		CLabel lbl_domainPath = new CLabel(grpDomain, SWT.NONE);
		lbl_domainPath.setBounds(8, 221, 107, 23);
		lbl_domainPath.setAlignment(SWT.RIGHT);
		lbl_domainPath.setText("*\u57DF\u8DEF\u5F84(\u670D\u52A1)");

		txt_domainPath = new Text(grpDomain, SWT.BORDER);
		txt_domainPath.setFont(SWTResourceManager
				.getFont("微软雅黑", 9, SWT.NORMAL));
		txt_domainPath.setBounds(128, 221, 394, 23);
		txt_domainPath
				.setToolTipText("\u4F8B\uFF1A/weblogic/10.3.5/user_projects/domains/jmsb_9999\r\n\u6216\r\n../IBM/WebSphere/AppServer/profiles/AppSrv01");

		CLabel lbl_domainPort = new CLabel(grpDomain, SWT.NONE);
		lbl_domainPort.setBounds(8, 260, 107, 23);
		lbl_domainPort.setAlignment(SWT.RIGHT);
		lbl_domainPort.setText("*\u57DF\u7AEF\u53E3(\u670D\u52A1)");

		txt_domainPort = new Text(grpDomain, SWT.BORDER);
		txt_domainPort.setFont(SWTResourceManager
				.getFont("微软雅黑", 9, SWT.NORMAL));
		txt_domainPort.setBounds(128, 260, 168, 23);
		txt_domainPort
				.setToolTipText("WebLogic\u6216WebSphere\u5DF2\u90E8\u7F72\u7684\u5E94\u7528\u7AEF\u53E3.");

		CLabel lbl_appPath = new CLabel(grpDomain, SWT.NONE);
		lbl_appPath.setBounds(9, 301, 107, 23);
		lbl_appPath.setAlignment(SWT.RIGHT);
		lbl_appPath.setText("*\u5E94\u7528\u90E8\u7F72\u8DEF\u5F84");

		txt_appPath = new Text(grpDomain, SWT.BORDER);
		txt_appPath.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		txt_appPath.setBounds(128, 301, 394, 23);
		txt_appPath
				.setToolTipText("\u90E8\u7F72\u5E94\u7528\u7684\u6240\u5728\u8DEF\u5F84\uFF0C\u542B\u5E94\u7528\u540D\u79F0\r\n\u4F8B\uFF1A/weblogic/10.3.5/user_projects/domains/jmsb_9999/web\r\n\u6216\r\nD:\\template");

		CLabel lblWinrarwindows = new CLabel(grpDomain, SWT.NONE);
		lblWinrarwindows.setBounds(9, 345, 107, 23);
		lblWinrarwindows.setText("\u5907\u4EFD\u5B58\u653E\u8DEF\u5F84");
		lblWinrarwindows.setAlignment(SWT.RIGHT);

		txt_backUpPath = new Text(grpDomain, SWT.BORDER);
		txt_backUpPath.setFont(SWTResourceManager
				.getFont("微软雅黑", 9, SWT.NORMAL));
		txt_backUpPath.setBounds(128, 345, 394, 23);
		txt_backUpPath
				.setToolTipText("\u63D0\u793A\uFF1A\u9ED8\u8BA4\u5B58\u653E\u5728\u5E94\u7528\u90E8\u7F72\u8DEF\u5F84\u540C\u7EA7\u76EE\u5F55\u4E0B.");

		txt_appVerification = new Text(grpDomain, SWT.BORDER);
		txt_appVerification.setFont(SWTResourceManager.getFont("微软雅黑", 9,
				SWT.NORMAL));
		txt_appVerification.setText("http://");
		txt_appVerification.setBounds(128, 417, 394, 23);
		txt_appVerification
				.setToolTipText("\u4F8B\uFF1Ahttp://127.0.0.1:9999/web");

		Menu menu = new Menu(txt_appVerification);
		txt_appVerification.setMenu(menu);

		MenuItem menu_autoJoint = new MenuItem(menu, SWT.NONE);
		menu_autoJoint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String projectName = "";
				if (!txt_appPath.getText().trim().isEmpty()) {
					File file = new File(txt_appPath.getText());
					projectName = file.getName();
					txt_appVerification.setText("http://"
							+ txt_serverIp.getText() + ":"
							+ txt_domainPort.getText() + "/" + projectName);
				} else {
					txt_appVerification.setText("http://"
							+ txt_serverIp.getText() + ":"
							+ txt_domainPort.getText());
				}

			}
		});
		menu_autoJoint.setText("\u81EA\u52A8\u62FC\u63A5");

		MenuItem mntm_copy = new MenuItem(menu, SWT.NONE);
		mntm_copy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				Transferable tText = new StringSelection(txt_appVerification
						.getText());
				clip.setContents(tText, null);
			}
		});
		mntm_copy.setImage(SWTResourceManager.getImage(ConfigServer.class,
				"/copy.png"));
		mntm_copy.setText("\u590D\u5236");

		Label label_1 = new Label(grpDomain, SWT.NONE);
		label_1.setBounds(9, 418, 107, 21);
		label_1.setText("*\u5E94\u7528\u9996\u9875\u8BBF\u95EE\u5730\u5740");
		label_1.setAlignment(SWT.RIGHT);
		// 测试应用地址是否可访问
		btn_browser = new Button(grpDomain, SWT.NONE);
		btn_browser.setText("\u6D4F\u89C8");
		btn_browser.setToolTipText("\u6D4B\u8BD5");
		btn_browser.setBounds(527, 415, 67, 27);
		btn_browser.setImage(SWTResourceManager.getImage(ConfigServer.class,
				"/ie.png"));
		btn_browser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (txt_appVerification.getText().trim().length() > 0) {
					Tool.RunCommand(new String[] { "cmd.exe", "/c",
							"start " + txt_appVerification.getText() });
				} else {
					MessageBoxUc.OpenOk(getShell(), "请先录入需要访问的地址！");
				}
			}
		});

		cbo_middlewareType = new CCombo(grpDomain, SWT.BORDER);
		cbo_middlewareType.setEditable(false);
		cbo_middlewareType.setFont(SWTResourceManager.getFont("微软雅黑", 9,
				SWT.BOLD));
		cbo_middlewareType.setBounds(128, 145, 168, 21);
		cbo_middlewareType.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				loadComboMiddlewareType();
			}
		});

		cbo_middlewareType.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));

		CLabel lbl_middleware = new CLabel(grpDomain, SWT.NONE);
		lbl_middleware.setBounds(8, 144, 107, 23);
		lbl_middleware.setAlignment(SWT.RIGHT);
		lbl_middleware.setText("*\u4E2D\u95F4\u4EF6\u7C7B\u578B");

		CLabel label = new CLabel(grpDomain, SWT.NONE);
		label.setBounds(8, 26, 107, 23);
		label.setAlignment(SWT.RIGHT);
		label.setText("*\u540D\u79F0\u63CF\u8FF0");

		txt_name = new Text(grpDomain, SWT.BORDER);
		txt_name.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		txt_name.setBounds(128, 26, 168, 23);
		txt_name.setToolTipText("\u5E94\u7528\u7684\u522B\u540D\uFF0C\u7528\u4E8E\u6807\u8BC6\u5E94\u7528\u3002");

		CLabel lbl_serverGroup = new CLabel(grpDomain, SWT.NONE);
		lbl_serverGroup.setBounds(8, 105, 107, 23);
		lbl_serverGroup.setAlignment(SWT.RIGHT);
		lbl_serverGroup.setText(" *\u5E94\u7528\u5206\u7EC4");

		cbo_serverGroup = new CCombo(grpDomain, SWT.BORDER | SWT.ALL);
		cbo_serverGroup.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		cbo_serverGroup.setBounds(128, 106, 168, 21);
		cbo_serverGroup.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		cbo_serverGroup.setEditable(false);
		Button btn_addServerGroup = new Button(grpDomain, SWT.NONE);
		btn_addServerGroup.setText("\u65B0\u589E");
		btn_addServerGroup.setToolTipText("\u6DFB\u52A0\u5206\u7EC4");
		btn_addServerGroup.setBounds(305, 103, 61, 27);

		btn_addServerGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cn.com.hnisi.view.dialog.Group group = new cn.com.hnisi.view.dialog.Group(
						getShell());
				group.open();
			}
		});
		btn_addServerGroup.setImage(SWTResourceManager.getImage(ConfigServer.class, "/chart_organisation_add.png"));

		Label lblNewLabel_2 = new Label(grpDomain, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setBounds(9, 386, 107, 17);
		lblNewLabel_2.setText("\u5907\u4EFD\u65B9\u5F0F");

		rdb_compressedFolder = new Button(grpDomain, SWT.RADIO);
		rdb_compressedFolder.setImage(SWTResourceManager.getImage(
				ConfigServer.class, "/winrar.png"));
		rdb_compressedFolder.setBounds(307, 385, 214, 17);
		rdb_compressedFolder
				.setText("\u538B\u7F29\u6587\u4EF6(WinRAR/TAR)(\u63A8\u8350)");

		rdb_copyfolder = new Button(grpDomain, SWT.RADIO);
		rdb_copyfolder
				.setToolTipText("\u590D\u5236\u4E00\u4EFD\u201C\u9879\u76EE\u8DEF\u5F84\u201D\u6587\u4EF6\u5939");
		rdb_copyfolder.setImage(SWTResourceManager.getImage(ConfigServer.class, "/folders.png"));
		rdb_copyfolder.setSelection(true);
		rdb_copyfolder.setBounds(128, 385, 167, 17);
		rdb_copyfolder.setText("\u590D\u5236\u6587\u4EF6\u5939(\u65F6\u95F4\u8F83\u957F)");

		Label lblNewLabel_3 = new Label(grpDomain, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setBounds(53, 69, 61, 17);
		lblNewLabel_3.setText("\u53D1\u5E03\u7C7B\u578B");

		cbo_releaseType = new CCombo(grpDomain, SWT.BORDER);
		cbo_releaseType.setFont(SWTResourceManager.getFont("微软雅黑", 9,
				SWT.NORMAL));
		cbo_releaseType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (cbo_releaseType.getSelectionIndex() == 1) {
					setReleaseControlEnabled(false);
				} else {
					setReleaseControlEnabled(true);
				}

			}
		});
		cbo_releaseType.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		cbo_releaseType.setEditable(false);
		cbo_releaseType.setItems(new String[] { "应用发布", "文件上传" });
		cbo_releaseType.setBounds(128, 67, 168, 21);
		cbo_releaseType.select(0);

		link_manageUser = new Link(grpDomain, SWT.NONE);
		link_manageUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MiddlewareConsoleUser webSphereConfig = new MiddlewareConsoleUser(getShell(),
						consoleAccount.getUsername(), consoleAccount
								.getPassword());
				if (webSphereConfig.open() == 0) {
					consoleAccount = webSphereConfig.getConsoleAccount();
				}

			}
		});
		link_manageUser.setBounds(306, 145, 94, 17);
		link_manageUser.setText("<a>\u63A7\u5236\u53F0\u7BA1\u7406\u8D26\u53F7</a>");

		link_browerDomainFromAgent = new Link(grpDomain, SWT.NONE);
		link_browerDomainFromAgent.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browerServer(txt_domainPath);
			}
		});
		link_browerDomainFromAgent.setBounds(525, 224, 86, 17);
		link_browerDomainFromAgent
				.setText("<a>\u6D4F\u89C8\u670D\u52A1\u5668</a>");

		Link link_browerProjectPath = new Link(grpDomain, SWT.NONE);
		link_browerProjectPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browerServer(txt_appPath);
			}
		});
		link_browerProjectPath.setBounds(528, 304, 86, 17);
		link_browerProjectPath.setText("<a>\u6D4F\u89C8\u670D\u52A1\u5668</a>");

		Link link_browerBackupPath = new Link(grpDomain, SWT.NONE);
		link_browerBackupPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browerServer(txt_backUpPath);
			}
		});
		link_browerBackupPath.setBounds(527, 348, 86, 17);
		link_browerBackupPath.setText("<a>\u6D4F\u89C8\u670D\u52A1\u5668</a>");

		lbl_serverName = new CLabel(grpDomain, SWT.NONE);
		lbl_serverName.setAlignment(SWT.RIGHT);
		lbl_serverName.setBounds(305, 260, 84, 23);
		lbl_serverName.setText("\u670D\u52A1\u540D(WAS)");

		txt_serverName = new Text(grpDomain, SWT.BORDER);
		txt_serverName
				.setToolTipText("\u4E2D\u95F4\u4EF6\u4E3AWebSphere\u65F6\uFF0C\u9700\u8981\u5F55\u5165\r\n\u4F8B\uFF1Aserver1");
		txt_serverName.setText("server1");
		txt_serverName.setBounds(395, 260, 127, 23);
		grpDomain.setTabList(new Control[] { txt_name, cbo_releaseType,
				cbo_serverGroup, cbo_middlewareType, cbo_systemType,
				txt_domainPort, txt_domainPath, txt_appPath, txt_backUpPath,
				rdb_copyfolder, rdb_compressedFolder, txt_appVerification,
				btn_browser, lbl_systemType, lbl_domainPath, lbl_domainPort,
				lbl_appPath, lblWinrarwindows, lbl_middleware, label,
				lbl_serverGroup, btn_addServerGroup });
		cbo_serverGroup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				loadServerGroupData();
			}
		});
		scrolledComposite.setContent(composite_1);
		// 初始化下位项
		loadComboData();
		// 如果Server的ID不为空，则获取应用配置信息
		if (serverId != null && !serverId.equals("")) {
			loadServerInfoById();
		}
		return area;
	}

	// 加载所有下位字典项
	private void loadComboData() {
		loadServerGroupData();
		loadComboSystemType();
		loadComboMiddlewareType();
	}

	/**
	 * 获取“系统类型”字典项
	 */
	private void loadComboSystemType() {
		ComboThread ct = new ComboThread(Display.getDefault(), cbo_systemType,
				"select * from dicts where dicName='systemType' and status='1'");
		Thread th = new Thread(ct);
		th.start();
	}

	/**
	 * 获取“中间件类型”字典项
	 */
	private void loadComboMiddlewareType() {
		ComboThread ct = new ComboThread(Display.getDefault(),
				cbo_middlewareType,
				"select * from dicts where dicName='middlewareType' and status='1'");
		Thread th = new Thread(ct);
		th.start();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		button.setImage(SWTResourceManager.getImage(ConfigServer.class,
				"/save.png"));
		button.setText("保存(&S)");
		Button btn_cancel = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		btn_cancel.setImage(null);
		btn_cancel.setText("关闭(&C)");
		btn_cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * 加载应用信息
	 */
	private void loadServerInfoById() {
		// TODO 获取应用信息
		if (this.serverId != null && !this.serverId.equals("")) {

			try {
				// 根据ID获取ServerModel应用实例
				serverModel = ServerModelDAO
						.getServerModelById(this.serverId);
				if (serverModel != null) {
					// 显示应用配置信息
					setComboByData(cbo_serverGroup, "groups",
							serverModel.getGroupId());
					setComboByData(cbo_systemType, "dicts",
							serverModel.getSystemTypeId());
					setComboByData(cbo_middlewareType, "dicts",
							serverModel.getMiddlewareTypeId());
					txt_name.setText(serverModel.getName());
					txt_serverName.setText(serverModel.getServerName());
					txt_serverIp.setText(serverModel.getServerIp());
					txt_domainPath.setText(serverModel.getDomainPath());
					// 用来判断是否有改变，有改变的话就要判断数据库同一IP，同一端口已经添加。
					txt_domainPort.setText(serverModel.getDomainPort());
					txt_domainPort.setData(serverModel.getDomainPort());

					txt_appPath.setText(serverModel.getAppPath());
					txt_appVerification.setText(serverModel
							.getAppVerification());
					txt_agentPort.setText(serverModel.getAgentPort());
					txt_agentPassword.setText(serverModel.getAgentPassword());
					// 变量：sourcePassword，用于保存时，判断密码是否有被修改
					sourcePassword = serverModel.getAgentPassword();

					txt_backUpPath.setText(serverModel.getBackUpPath());
					// 备份方式：0-复制文件夹；1-压缩文件夹
					if (serverModel.getBackupType() != null
							&& serverModel.getBackupType().equals("1")) {
						rdb_compressedFolder.setSelection(true);
						rdb_copyfolder.setSelection(false);
					} else {
						rdb_copyfolder.setSelection(true);
						rdb_compressedFolder.setSelection(false);
					}
					
					if (serverModel.getReleaseType() != null
							&& serverModel.getReleaseType().equals("1")
							&& cbo_releaseType.getItemCount() > 1) {
						setReleaseControlEnabled(false);
					} else {
						setReleaseControlEnabled(true);
					}
					
					if(serverModel.getServerType().equals("1")){
						//集群总控制
						cbx_adminServer.setSelection(true);
						cbx_clusterComputer.setSelection(false);
						cbx_defaultServer.setSelection(false);
						
						btn_addClusterServer.setEnabled(true);
					}else if(serverModel.getServerType().equals("2")){
						//集群机器
						cbx_clusterComputer.setSelection(true);
						cbx_defaultServer.setSelection(false);
						cbx_adminServer.setSelection(false);
						
						btn_addClusterServer.setEnabled(true);
					}else{
						//默认普通应用
						cbx_defaultServer.setSelection(true);
						cbx_adminServer.setSelection(false);
						cbx_clusterComputer.setSelection(false);
						
						btn_addClusterServer.setEnabled(false);
					}
					if (consoleAccount != null) {
						consoleAccount.setUsername(serverModel.getUsername());
						consoleAccount.setPassword(serverModel.getPassword());
					}
					if(cbo_releaseType.getItemCount()>1){
						if(serverModel.getReleaseType().equals("0")){
							cbo_releaseType.select(0);
						}else{
							cbo_releaseType.select(1);
						}
					}
				}else{
					MessageBoxUc.OpenOk(getShell(), "没有找到应用配置信息！");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error("获取应用信息出错，原因：" + ex.getMessage());
				MessageBoxUc.OpenError(shell, "获取应用信息出错，原因：" + ex.getMessage());
			} finally {
			}
		}
	}

	/**
	 * 浏览服务器文件目录
	 * 
	 * @param text
	 */
	private void browerServer(Text text) {
		try {
			serverModel=getCurrentServerModel();
			if (NetWorkUtil.TestConnectBySocket(serverModel.getServerIp(),
					Integer.parseInt(txt_agentPort.getText()), 3000)) {
				ManageServerFile getpath = new ManageServerFile(getShell(),
						getCurrentServerModel(), text.getText());
				int status = getpath.open();
				if (status == 0) {
					// 确定
					if (getpath.getSelectPath() != null
							&& !getpath.getSelectPath().equals("")) {
						text.setText(getpath.getSelectPath());
					}
				}
			} else {
				MessageBoxUc.OpenError(getShell(), "连接Agent失败，请检查!");
			}
		} catch (Exception e) {
			MessageBoxUc.OpenOk(getShell(), ""+e.getMessage());
		}
	}

	/**
	 * 获取当前最新实例ServerModel，如果为空则创建新实例
	 * 
	 * @return
	 * @throws Exception
	 */
	private ServerModel getCurrentServerModel() {
		if (this.serverModel == null) {
			this.serverModel = new ServerModel();
			this.serverModel.setId(Tool.getUUID());// 创建新的ID
		}
		this.serverModel.setServerIp(txt_serverIp.getText());
		this.serverModel.setName(txt_name.getText());
		this.serverModel.setServerName(txt_serverName.getText());
		this.serverModel.setGroupName(cbo_serverGroup.getText());
		this.serverModel.setGroupId(cbo_serverGroup.getData(
				cbo_serverGroup.getSelectionIndex() + "").toString());
		this.serverModel
				.setReleaseType(cbo_releaseType.getSelectionIndex() == 0 ? "0"
						: "1");
		this.serverModel.setSystemType(cbo_systemType.getText());
		this.serverModel.setSystemTypeId(cbo_systemType.getData(
				cbo_systemType.getSelectionIndex() + "").toString());
		this.serverModel.setMiddlewareType(cbo_middlewareType.getText());
		this.serverModel.setMiddlewareTypeId(cbo_middlewareType.getData(
				cbo_middlewareType.getSelectionIndex() + "").toString());
		this.serverModel.setDomainPath(txt_domainPath.getText());
		this.serverModel.setDomainPort(txt_domainPort.getText());
		this.serverModel.setAppPath(txt_appPath.getText());
		this.serverModel.setAgentPath("");
		this.serverModel.setBackUpPath(txt_backUpPath.getText());
		this.serverModel.setBackupType(rdb_copyfolder.getSelection() == true ? "0"
				: "1");
		this.serverModel.setAppVerification(txt_appVerification.getText());
		this.serverModel.setAgentPort(txt_agentPort.getText());
		String agentPassword = "";
		// 如果是编辑操作，则判断原来的密码是否有修改过
		if (txt_agentPassword.getText().trim().length() > 0) {
			if (sourcePassword.equals(txt_agentPassword.getText())) {
				// 密码没有改动
				agentPassword = sourcePassword;
			} else {
				try {
					agentPassword = Tool.EncoderByMd5(txt_agentPassword
							.getText());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else {
			agentPassword = "";
		}
		//应用类别：0-默认(普通应用); 1-总控制; 2-集群机器
		if(cbx_adminServer.getSelection()){
			//1-总控制;
			this.serverModel.setServerType("1");
		}else if(cbx_clusterComputer.getSelection()){
			//2-集群机器
			this.serverModel.setServerType("2");
		}else{
			//0-默认(普通应用)
			this.serverModel.setServerType("0");
		}
		this.serverModel.setAgentPassword(agentPassword);
		this.serverModel.setUsername(consoleAccount.getUsername() != null ? consoleAccount
						.getUsername() : "");
		this.serverModel.setPassword(consoleAccount.getPassword() != null ? consoleAccount
						.getPassword() : "");
		return serverModel;
	}

	/**
	 * 更改“发布类型”时把相关录入项设为不可编辑
	 * 
	 * @param enabled
	 */
	private void setReleaseControlEnabled(boolean enabled) {
		cbo_middlewareType.setEnabled(enabled);
		cbo_systemType.setEnabled(enabled);
		txt_domainPath.setEnabled(enabled);
		txt_domainPort.setEnabled(enabled);
		txt_appVerification.setEnabled(enabled);
		btn_browser.setEnabled(enabled);
		link_browerDomainFromAgent.setEnabled(enabled);
	}

	/**
	 * 点击“保存”时执行操作
	 */
	protected void okPressed() {
		if (txt_serverIp.getText().trim().length() <= 0) {
			MessageBoxUc.OpenOk(shell, "“服务器IP地址”不能为空!");
			txt_serverIp.setFocus();
			return;
		} else {
			if (!Tool.checkIp(txt_serverIp.getText())) {
				MessageBoxUc.OpenOk(shell, "请输入合法的IP地址格式!");
				txt_serverIp.setFocus();
				return;
			}
		}
		
		if (txt_agentPort.getText().trim().length() <= 0) {
			MessageBoxUc.OpenOk(shell, "“Agent端口”不能为空!");
			txt_agentPort.setFocus();
			return;
		} else {
			
			try {
				if(txt_agentPort.getText().trim().length()==0){
					MessageBoxUc.OpenOk(getShell(), "“AGENT端口号”不能为空！");
					txt_agentPort.setFocus();
					return;
				}
				Tool.checkPort(txt_agentPort.getText());
			} catch (Exception e) {
				MessageBoxUc.OpenOk(shell, e.getMessage());
				txt_agentPort.setFocus();
				return;
			}
		}
		
		if (txt_name.getText().trim().length() <= 0) {
			MessageBoxUc.OpenOk(shell, "“名称”不能为空!");
			txt_name.setFocus();
			return;
		}
		
		if (cbo_serverGroup.getText().trim().length() <= 0) {
			MessageBoxUc.OpenOk(shell, "请选择对应的应用群!");
			cbo_serverGroup.setFocus();
			return;
		}

		if (cbo_systemType.getText().trim().length() <= 0) {
			MessageBoxUc.OpenOk(shell, "请选择对应的操作系统类型!");
			cbo_systemType.setFocus();
			return;
		}


		if (cbo_middlewareType.getText().trim().length() <= 0) {
			MessageBoxUc.OpenOk(shell, "请选择对应的中间件类型!");
			cbo_middlewareType.setFocus();
			return;
		}
		
		// 发布类型0-应用发布；1-文件上传
		if (cbo_releaseType.getSelectionIndex()==0) {
			
			if (txt_domainPath.getText().trim().length() <= 0) {
				MessageBoxUc.OpenOk(shell, "“域路径（服务）”不能为空!");
				txt_domainPath.setFocus();
				return;
			}
			
			if (txt_domainPort.getText().trim().length() <= 0) {
				MessageBoxUc.OpenOk(shell, "“域端口号（服务）”不能为空!");
				txt_domainPort.setFocus();
				return;
			} else {
				try {
					int port = Integer.parseInt(txt_domainPort.getText());
					if (port < 0 || port > 65535) {
						MessageBoxUc.OpenOk(shell,
								"“域端口号（服务）”请输入一个1~65535的正整数，例：8888");
						txt_domainPort.setFocus();
						return;
					}
				} catch (Exception e) {
					MessageBoxUc.OpenOk(shell,
							"“域端口号（服务）”请输入一个1~65535的正整数，例：8888");
					txt_domainPort.setFocus();
					return;
				}
			}


			if (txt_appVerification.getText().trim().length() <= 0) {
				MessageBoxUc.OpenOk(shell, "请输入“应用首页访问地址”!");
				txt_appVerification.setFocus();
				return;
			}

			if (cbo_middlewareType.getText().toLowerCase().equals("websphere")) {
				if (txt_serverName.getText().trim().length() <= 0) {
					MessageBoxUc.OpenOk(shell, "WebSphere应用，请输入“服务名”!");
					txt_serverName.setFocus();
					return;
				}
			}
		}

		if (txt_appPath.getText().trim().length() <= 0) {
			MessageBoxUc.OpenOk(shell, "“应用部署路径”不能为空!");
			txt_appPath.setFocus();
			return;
		}

		int code = 0;
		try {
			// ------------插入更新应用-----------
			if (serverId == null) {
				// 新增应用服务器
				code = ServerModelDAO
						.insertServer(getCurrentServerModel());
			} else {
				// 更新应用服务器
				code = ServerModelDAO
						.updateServer(getCurrentServerModel());
			}

			
			clusterServerModelList.clear();//清空后，获取所有集群表单记录
			
			for (int i = 0; i < clusterFormList.size();i++) {
				ClusterServicesForm csf=clusterFormList.get(i);
				ClusterServicesModel save_csm=csf.getClusterServerModel();
				//如果“服务名”、“管理地址”和“应用存在路径”不能为，否则将不被更新和新插入
				if(save_csm!=null && save_csm.getName().trim().length()>0 
						&& save_csm.getAdminUrl().trim().length()>0
					&& save_csm.getStagePath().trim().length()>0){
					clusterServerModelList.add(save_csm);//获取实例
				}
			}
			// 插入集群
			if (clusterServerModelList != null && clusterServerModelList.size()>0) {
				for (ClusterServicesModel csm : clusterServerModelList) {
					code = ClusterServicesDAO.insertOrUpdate(csm);
				}
			}
			
			if (code > 0) {
				if (serverId == null) {
					MessageBoxUc.OpenOk(shell, "保存成功!");
				} else {
					MessageBoxUc.OpenOk(shell, "更新成功!");
				}
			} else {
				MessageBoxUc.OpenOk(shell, "操作失败，更新记录为0 条!!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("保存记录失败，原因：" + e.getMessage());
			MessageBoxUc.OpenError(shell, "保存失败，原因：" + e.getMessage());
			return;
		}
		super.okPressed();
	}

	// ----动态生成服务控件 开始--------------
	final List<ClusterServicesForm> clusterFormList = new ArrayList<ClusterServicesForm>();
	int x = 0;
	int y = 0;
	int width = 0;
	final int height = 110;
	int count = 0;
	
	private void loadClusterServerList(String sid){		
		try {
			if(composite_cluster.getChildren().length<=0){
				List<ClusterServicesModel> csmList = ClusterServicesDAO.getClusterServerModelList(sid);
				for(ClusterServicesModel csm:csmList){
					addClusterServerForm(csm);
				}
				csmList.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 添加集群服务
	 */
	private void addClusterServerForm() {
		addClusterServerForm(null);
	}

	private void addClusterServerForm(ClusterServicesModel csm) {
		String id = "";
		if (csm != null) {
			id = csm.getId();
		} else {
			id = Tool.getUUID();
		}

		y = height * count + (count * 1);
		width = 759;//宽度
		Link lik_delete = new Link(composite_cluster, SWT.NONE);
		lik_delete.setText("<a>删除</a>");
		lik_delete.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lik_delete.setBounds(x + width - 37, y + 50, 35, 20);
		lik_delete.setData(id);
		lik_delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Link lik = (Link) e.getSource();
				String csId = lik.getData().toString();
				try {
					boolean confirm_delete = true;
					ClusterServicesModel delete_csm = ClusterServicesDAO
							.getClusterServerMode(csId);
					if (delete_csm != null) {
						if (MessageDialog.openConfirm(getShell(), "删除确认",
								"是否要删除服务名称为 “" + delete_csm.getName()
										+ "” 的记录？")) {
							confirm_delete = true;
							ClusterServicesDAO
									.deleteClusterServer(delete_csm);
						} else {
							confirm_delete = false;
						}
					}
					if (confirm_delete) {
						removeServerForm(csId);
						lik.dispose();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		ClusterServicesForm csf = new ClusterServicesForm(composite_cluster,
				SWT.NONE, getCurrentServerModel());
		csf.setNumber(String.valueOf(count + 1));//显示序号
		csf.setBounds(x, y, width, height);
		csf.setData(id);

		csf.setId(id);
		csf.setSid(getCurrentServerModel().getId());// 对应的ServerModel主键ID
		if (csm != null) {
			csf.setServerName(csm.getName());//服务名
			csf.setAdminUrl(csm.getAdminUrl());//管理地址
			csf.setStagePath(csm.getStagePath());//应用存放路径
			csf.setIsUploadFile(csm.getIsUploadFile());//是否同步部署文件
		}
		clusterFormList.add(csf);

		count++;
		scrolledComposite_cluster.setMinSize(width, y + height);
		composite_cluster.setFocus();
		clusterServerModelList.add(csf.getClusterServerModel());
	}

	// 动态删除控件
	public void removeServerForm(String uuid) {
		synchronized (clusterFormList) {

			// 删除表单
			for (int i = 0; i < clusterFormList.size(); i++) {
				if (clusterFormList.get(i).getData().toString().equals(uuid)) {
					clusterFormList.get(i).dispose();
					clusterFormList.remove(i);
					break;
				}
			}
			for (int i = 0; i < clusterFormList.size(); i++) {
				ClusterServicesForm csf = clusterFormList.get(i);
				if (csf.getBounds().y > 0) {
					// 设置表单位置
					csf.setLocation(0, (i * height) + (i * 1));
					csf.setNumber(String.valueOf(i + 1));
					for (Control con : composite_cluster.getChildren()) {
						if (con instanceof Link) {
							Link lik = (Link) con;
							if (csf.getData().equals(lik.getData())) {
								// 设置删除按钮位置(位置在右上角，向下偏移3)
								lik.setLocation(0 + csf.getBounds().width - 37,
										(i * height) + 50);
							}
						}
					}
				}
			}
			count = clusterFormList.size();
			y = count * height;
			scrolledComposite_cluster.setMinSize(
					composite_cluster.getBounds().width, y + height);

		}
	}

	// ----动态生成服务控件 结束--------------

	/**
	 * 加载所有应用分组记录
	 */
	private void loadServerGroupData() {
		ComboThread ct = new ComboThread(Display.getDefault(), cbo_serverGroup,
				"select * from groups order by sortNum desc");
		Thread th = new Thread(ct);
		th.start();
	}

	private void setComboByData(CCombo combo, String tableName, String id) {
		String sqlStr = "select * from " + tableName + " where id='" + id + "'";
		ComboThread ct = new ComboThread(Display.getDefault(), combo, sqlStr);
		Thread th = new Thread(ct);
		th.start();
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

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(SWTResourceManager.getImage(ConfigServer.class,
				"/server.png"));
		super.configureShell(newShell);
		// Dialog Title
		newShell.setText("应用服务管理");

	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(794, 714);
	}
}
