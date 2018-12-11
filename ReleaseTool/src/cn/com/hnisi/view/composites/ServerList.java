package cn.com.hnisi.view.composites;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.agent.services.function.AgentTestConnect;
import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.dao.ClusterServicesDAO;
import cn.com.hnisi.dao.FastLabelDAO;
import cn.com.hnisi.dao.FastLabelServerDAO;
import cn.com.hnisi.dao.ServerModelDAO;
import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.model.ClusterServicesModel;
import cn.com.hnisi.model.FastLabelModel;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.StepType;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.TableViewThread;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.MainForm;
import cn.com.hnisi.view.component.FastLabelUc;
import cn.com.hnisi.view.component.MessageBoxUc;
import cn.com.hnisi.view.dialog.BatchModifiAgentPwd;
import cn.com.hnisi.view.dialog.ConfigServer;
import cn.com.hnisi.view.dialog.DownloadFile;
import cn.com.hnisi.view.dialog.ManageServerFile;
import cn.com.hnisi.view.dialog.PrintConsole;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * 第1步：显示服务器列表
 * 
 * @author FengGeGe
 * 
 */
public class ServerList extends Composite {

	static Logger log = Logger.getLogger(ServerList.class);
	private final SQLiteDatabase sql = SQLiteDatabase.getInstance();// 数据库操作类
	private Table table_serverList;// 表格
	private Button btn_nextStep;// 下一步
	static MenuItem mntm_testAgent;// 测检Agent
	private Tree tree_servers;// 分组树
	private SashForm sashForm;// 布局窗口
	private Composite composite_tree;// 左侧导航面板-分组
	private Composite composite_label;// 左侧导航面板-快捷标签
	private Composite composite_list;// 右侧应用列表面板

	private CTabFolder tabFolder_serverList;// 选项卡容器
	private CTabItem tabItem_serverList;// 选 项卡
	private CTabFolder tabFolder_servers;// 选项卡容器
	private CTabItem tabItem_label;// 快捷标签
	// 添加快捷标签
	private ToolItem btn_addLabel;
	private boolean isExpand = false;// 记录分组树状态是否展开或折叠
	private CheckboxTableViewer checkboxTableViewer;// CheckBox列表视图容器
	// 移动到分组
	private Menu menu_move_group;
	// 添加到快捷标签
	private Menu menu_label;
	/**
	 * 记录当前Table列表中的应用记录 第1次会加载所有应用
	 */
	static List<ServerModel> serverList = new ArrayList<ServerModel>();
	/**
	 * 记录已选分组，“全局”记录
	 */
	private String selectionGroupId = "";
	private final FormToolkit formToolkit = new FormToolkit(
			Display.getDefault());
	private Link link_showAll;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ServerList(Composite parent, int style) {
		super(parent, SWT.NONE);
		setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		setLayout(new FormLayout());
		
				Composite composite_head = new Composite(this, SWT.NONE);
				composite_head.setLayout(new FormLayout());
				FormData fd_composite_head = new FormData();
				fd_composite_head.bottom = new FormAttachment(0, 56);
				fd_composite_head.top = new FormAttachment(0, 1);
				fd_composite_head.left = new FormAttachment(0, 1);
				fd_composite_head.right = new FormAttachment(100);
				composite_head.setLayoutData(fd_composite_head);
				
						CLabel lbl_step1 = new CLabel(composite_head, SWT.NONE);
						FormData fd_lbl_step1 = new FormData();
						fd_lbl_step1.right = new FormAttachment(0, 286);
						fd_lbl_step1.top = new FormAttachment(0, 8);
						fd_lbl_step1.left = new FormAttachment(0);
						lbl_step1.setLayoutData(fd_lbl_step1);
						lbl_step1.setImage(SWTResourceManager.getImage(ServerList.class,
								"/direction.png"));
						lbl_step1.setForeground(SWTResourceManager
								.getColor(SWT.COLOR_INFO_FOREGROUND));
						lbl_step1.setBackground(SWTResourceManager
								.getColor(SWT.COLOR_WIDGET_BACKGROUND));
						lbl_step1.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.NORMAL));
						lbl_step1
								.setText("\u7B2C1\u6B65:\uFF1A\u9009\u62E9&&\u914D\u7F6E\u5E94\u7528\u670D\u52A1\u5668");
					
		
				Composite composite_tool = new Composite(this, SWT.NONE);
				FormData fd_composite_tool = new FormData();
				fd_composite_tool.top = new FormAttachment(composite_head);
				fd_composite_tool.left = new FormAttachment(0, 1);
				fd_composite_tool.right = new FormAttachment(100, -1);
				composite_tool.setLayoutData(fd_composite_tool);
				
						Button btn_addServer = new Button(composite_tool, SWT.NONE);
						btn_addServer
								.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
						btn_addServer.setBounds(2, 0, 90, 30);
						btn_addServer.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								ConfigServer config = new ConfigServer(getShell(), null);
								config.open();
								// 删除之后，重新加载数据
								refresh();
							}
						});
						btn_addServer.setImage(SWTResourceManager.getImage(ServerList.class,
								"/server_add.png"));
						btn_addServer.setText("\u6DFB\u52A0\u5E94\u7528");

		sashForm = new SashForm(this, SWT.NONE);
		FormData fd_sashForm = new FormData();
		fd_sashForm.top = new FormAttachment(composite_tool, 1);
		fd_sashForm.left = new FormAttachment(0, 2);
		fd_sashForm.right = new FormAttachment(100, -2);
		sashForm.setLayoutData(fd_sashForm);

		composite_tree = new Composite(sashForm, SWT.NONE);
		composite_tree.setLayout(new FillLayout(SWT.HORIZONTAL));
		tabFolder_servers = new CTabFolder(composite_tree, SWT.BORDER
				| SWT.FLAT);
		tabFolder_servers.setTabHeight(30);

		tabFolder_servers.setFont(SWTResourceManager.getFont("微软雅黑", 9,
				SWT.NORMAL));
		tabFolder_servers.setMaximizeVisible(true);
		tabFolder_servers.setUnselectedCloseVisible(false);
		tabFolder_servers.addCTabFolder2Listener(new CTabFolder2Adapter() {

			@Override
			public void maximize(CTabFolderEvent event) {
				tabFolder_servers.setMaximized(true);
				sashForm.setMaximizedControl(composite_tree);
				layout(true);
			}

			@Override
			public void restore(CTabFolderEvent event) {
				tabFolder_servers.setMaximized(false);
				sashForm.setMaximizedControl(null);
				layout(true);
			}
		});

		tabFolder_servers.setSelectionBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));

		CTabItem tabItem_servers = new CTabItem(tabFolder_servers, SWT.NONE);
		tabItem_servers.setImage(SWTResourceManager.getImage(ServerList.class,
				"/computer.png"));
		tabItem_servers.setText(" \u5E94\u7528\u5206\u7EC4  ");

		tree_servers = new Tree(tabFolder_servers, SWT.NONE);
		tree_servers.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		tree_servers
				.setToolTipText("F5 \u68C0\u6D4B\u5E94\u7528\u72B6\u6001\uFF1BF6\u68C0\u6D4BAgent\u7F51\u7EDC");
		tree_servers.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.keyCode == SWT.F4) {
					// 展开或折叠分组树
					serverGroupExpand();
					return;
				} else if (e.keyCode == SWT.F5) {
					// 检测应用状态
					testStatus(1);
					return;
				} else if (e.keyCode == SWT.F6) {
					// 检查Agent网络
					testStatus(0);
					return;
				}
			}
		});
		tree_servers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// 双击分组树节点
				for (TreeItem ti : tree_servers.getSelection()) {
					if (ti.getData() instanceof ServerModel) {
						// 双击应用
						ServerModel server = (ServerModel) ti.getData();
						selectionGroupId = server.getGroupId();
						editSelectTableItem(server.getId());
					}
					// else {
					// // 双击分组
					// String groupId = ti.getData().toString();
					// // 获取到group的Id
					// selectionGroupId = groupId;
					// // 打开分组编辑窗口
					// ServerGroup sg = new ServerGroup(getShell(), groupId);
					// // 如果点击了“保存”，则刷新树列表
					// if (sg.open() == 0) {
					// getTreeServers();
					// }
					// }
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {

				if (e.button == 1) {// 左键
					for (TreeItem ti : tree_servers.getSelection()) {
						Rectangle re = ti.getBounds();
						if ((re.x <= e.x && e.x <= re.x + re.width)
								&& e.y <= re.y + re.height) {
							showSelectedServer();
						}
					}
				}
			}
		});

		tabItem_servers.setControl(tree_servers);

		Menu menu_1 = new Menu(tree_servers);
		tree_servers.setMenu(menu_1);
		// 右击菜单：展开/折叠
		MenuItem mntm_expandFalse = new MenuItem(menu_1, SWT.NONE);
		mntm_expandFalse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serverGroupExpand();
			}
		});
		mntm_expandFalse.setText("\u5C55\u5F00/\u6298\u53E0(F4)");

		new MenuItem(menu_1, SWT.SEPARATOR);

		MenuItem mntm_checkServerStatus = new MenuItem(menu_1, SWT.NONE);
		mntm_checkServerStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testStatus(1);
			}
		});
		mntm_checkServerStatus.setImage(SWTResourceManager.getImage(
				ServerList.class, "/server_connect.png"));
		mntm_checkServerStatus
				.setText("\u68C0\u6D4B\u5E94\u7528\u72B6\u6001(F5)");

		MenuItem mntm_checkAgentStatus = new MenuItem(menu_1, SWT.NONE);
		mntm_checkAgentStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testStatus(0);
			}
		});
		mntm_checkAgentStatus.setImage(SWTResourceManager.getImage(
				ServerList.class, "/check.png"));
		mntm_checkAgentStatus.setText("\u68C0\u6D4BAgent\u7F51\u7EDC(F6)");
		// 刷新分组菜单
		MenuItem mntm_refresh = new MenuItem(menu_1, SWT.NONE);
		mntm_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getTreeServers();
			}
		});
		mntm_refresh.setImage(null);
		mntm_refresh.setText("\u5237\u65B0(&R)");

		composite_list = new Composite(sashForm, SWT.BORDER);
		composite_list.setLayout(new FormLayout());

		Composite composite_1 = new Composite(composite_list, SWT.NONE);
		//fd_composite_2.right = new FormAttachment(composite_1, 0, SWT.LEFT);
		FormData fd_composite_1 = new FormData();
		fd_composite_1.bottom = new FormAttachment(100);
		fd_composite_1.left = new FormAttachment(0);
		fd_composite_1.top = new FormAttachment(0, 30);
		fd_composite_1.right = new FormAttachment(100);
		composite_1.setLayoutData(fd_composite_1);
		formToolkit.adapt(composite_1);
		formToolkit.paintBordersFor(composite_1);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder_serverList = new CTabFolder(composite_1, SWT.BORDER
				| SWT.FLAT);
		tabFolder_serverList.setFont(SWTResourceManager.getFont("微软雅黑", 9,
				SWT.NORMAL));
		tabFolder_serverList.addCTabFolder2Listener(new CTabFolder2Adapter() {
			@Override
			public void maximize(CTabFolderEvent event) {
				tabFolder_serverList.setMaximized(true);
				sashForm.setMaximizedControl(composite_list);
				layout(true);
			}

			@Override
			public void restore(CTabFolderEvent event) {
				tabFolder_serverList.setMaximized(false);
				sashForm.setMaximizedControl(null);
				layout(true);
			}
		});
		tabFolder_serverList.setMaximizeVisible(true);
		tabFolder_serverList.setSingle(true);
		tabFolder_serverList.setSimple(false);
		tabFolder_serverList.setTabHeight(0);
		tabFolder_serverList.setSelectionBackground(SWTResourceManager
				.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));

		tabItem_serverList = new CTabItem(tabFolder_serverList, SWT.NONE);
		tabItem_serverList.setImage(SWTResourceManager.getImage(
				ServerList.class, "/list.png"));
		tabItem_serverList
				.setText(" \u5E94\u7528\u5217\u8868\uFF08\u52FE\u9009\u53D1\u5E03\uFF09");
		tabFolder_serverList.setSelection(tabItem_serverList);
		checkboxTableViewer = CheckboxTableViewer.newCheckList(
				tabFolder_serverList, SWT.FULL_SELECTION | SWT.MULTI);
		checkboxTableViewer.setAllChecked(false);
		table_serverList = checkboxTableViewer.getTable();
		tabItem_serverList.setControl(table_serverList);
		table_serverList.setHeaderVisible(true);
		table_serverList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// F5测试Agent状态
				switch (e.keyCode) {
				case SWT.F6:
					testAgent();
					break;
				case SWT.F5:
					getServerData(selectionGroupId, false);
					break;
				default:
					break;
				}

			}
		});
		table_serverList.setToolTipText("");
		
				table_serverList.setFont(SWTResourceManager.getFont("微软雅黑", 9,
						SWT.NORMAL));
				// 双击多记录表进行编辑
				table_serverList.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseDoubleClick(MouseEvent e) {
						int index = table_serverList.getSelectionIndex();
						// 防止getItem(x)时超出索引范围报错
						if (index >= 0 && index <= table_serverList.getItemCount()) {
							String id = table_serverList.getItem(index).getData()
									.toString();
							editSelectTableItem(id);
						}
					}

					@Override
					public void mouseUp(MouseEvent e) {
						// .button==1 是左键，==3是右键
						if (e.button == 1) {
							int index = table_serverList.getSelectionIndex();
							// 防止getItem(x)时超出索引范围报错
							if (index >= 0 && index <= table_serverList.getItemCount()) {
								TableItem item = table_serverList.getItem(index);
								ViewerCell viewerCell = checkboxTableViewer
										.getCell(new Point(e.x, e.y));
								if (viewerCell != null) {// 如是是点击了勾选框viewerCell=null
									if (item.getChecked()) {
										item.setChecked(false);
									} else {
										item.setChecked(true);
									}
								}
							}
						}
					}
				});
				
						TableViewerColumn tbcv_select = new TableViewerColumn(
								checkboxTableViewer, SWT.NONE);
						TableColumn tbc_select = tbcv_select.getColumn();
						tbc_select.setAlignment(SWT.CENTER);
						tbc_select.setWidth(60);
						tbc_select.setText("\u9009\u62E9");
						
								TableColumn tbc_networkStatus = new TableColumn(table_serverList,
										SWT.LEFT);
								
										tbc_networkStatus.setWidth(100);
										tbc_networkStatus.setText("Agent\u72B6\u6001");
										
												TableColumn tbc_serverName = new TableColumn(table_serverList, SWT.NONE);
												tbc_serverName.setWidth(110);
												tbc_serverName.setText("\u540D\u79F0");
												
														TableViewerColumn tbcv_serverGroup = new TableViewerColumn(
																checkboxTableViewer, SWT.NONE);
														TableColumn tbc_serverGroup = tbcv_serverGroup.getColumn();
														tbc_serverGroup.setWidth(150);
														tbc_serverGroup.setText("\u5E94\u7528\u5206\u7EC4");
														
																TableViewerColumn tbcv_ipAddress = new TableViewerColumn(
																		checkboxTableViewer, SWT.NONE);
																TableColumn tbc_ipAddress = tbcv_ipAddress.getColumn();
																tbc_ipAddress.setAlignment(SWT.RIGHT);
																tbc_ipAddress.setWidth(110);
																tbc_ipAddress.setText("IP\u5730\u5740");
																
																		TableViewerColumn tbcv_serverPort = new TableViewerColumn(
																				checkboxTableViewer, SWT.NONE);
																		TableColumn tbc_domainPort = tbcv_serverPort.getColumn();
																		tbc_domainPort.setWidth(100);
																		tbc_domainPort.setText("应用端口");
																		
																				TableViewerColumn tbcv_systemType = new TableViewerColumn(
																						checkboxTableViewer, SWT.NONE);
																				TableColumn tbc_systemType = tbcv_systemType.getColumn();
																				tbc_systemType.setWidth(130);
																				tbc_systemType.setText("\u64CD\u4F5C\u7CFB\u7EDF");
																				
																						TableViewerColumn tbcv_middleware = new TableViewerColumn(
																								checkboxTableViewer, SWT.NONE);
																						TableColumn tbc_agentPort = tbcv_middleware.getColumn();
																						tbc_agentPort.setWidth(100);
																						tbc_agentPort.setText("Agent\u7AEF\u53E3\u53F7");
																						
																								TableColumn tbc_middlewareType = new TableColumn(table_serverList,
																										SWT.NONE);
																								tbc_middlewareType.setWidth(100);
																								tbc_middlewareType.setText("\u4E2D\u95F4\u4EF6\u7C7B\u578B");
																								
																										TableViewerColumn tbcv_status = new TableViewerColumn(
																												checkboxTableViewer, SWT.NONE);
																										TableColumn tbc_appPath = tbcv_status.getColumn();
																										tbc_appPath.setWidth(400);
																										tbc_appPath.setText("\u5E94\u7528\u90E8\u7F72\u8DEF\u5F84");
																										
																												TableColumn tbc_appVerification = new TableColumn(table_serverList,
																														SWT.NONE);
																												tbc_appVerification.setWidth(200);
																												tbc_appVerification.setText("\u5E94\u7528URL\u5730\u5740");
																												
																														TableColumn tblc_domainPath = new TableColumn(table_serverList,
																																SWT.NONE);
																														tblc_domainPath.setWidth(400);
																														tblc_domainPath.setText("\u57DF\u8DEF\u5F84\uFF08\u670D\u52A1\uFF09");
																														
																																final Menu menu = new Menu(table_serverList);
																																menu.addMenuListener(new MenuAdapter() {
																																	@Override
																																	public void menuShown(MenuEvent e) {
																																		// 右键菜单，动态生成分组菜单项
																																		createGroupMenu();
																																		// 右键菜单，动态生成快捷标签
																																		createFastLabelMenu();
																																	}
																																});
																																table_serverList.setMenu(menu);
																																// 右击全选
																																MenuItem mntms_selectAll = new MenuItem(menu, SWT.CASCADE);
																																mntms_selectAll.addSelectionListener(new SelectionAdapter() {
																																	@Override
																																	public void widgetSelected(SelectionEvent e) {
																																		selectAll();
																																	}
																																});
																																mntms_selectAll.setImage(SWTResourceManager.getImage(ServerList.class,
																																		"/check_box.png"));
																																mntms_selectAll.setText("\u5168\u9009(&A)");
																																
																																		MenuItem mntmc_cancelSelect = new MenuItem(menu, SWT.CASCADE);
																																		mntmc_cancelSelect.addSelectionListener(new SelectionAdapter() {
																																			@Override
																																			public void widgetSelected(SelectionEvent e) {
																																				cancelSelect();
																																			}
																																		});
																																		mntmc_cancelSelect.setText("\u53D6\u6D88\u9009\u62E9(&C)");
																																		
																																				new MenuItem(menu, SWT.SEPARATOR);
																																				// 查看控制台
																																				MenuItem mntm_viewConsole = new MenuItem(menu, SWT.NONE);
																																				mntm_viewConsole.addSelectionListener(new SelectionAdapter() {
																																					@Override
																																					public void widgetSelected(SelectionEvent e) {

																																						ServerModel server = getSelectedServerModel();
																																						if (server != null) {
																																							PrintConsole viewLogs = null;
																																							try {
																																								viewLogs = new PrintConsole(getShell(), ServerModelDAO
																																										.getServerModelById(server.getId()));
																																								GlobalConfig.setWinList(viewLogs);
																																							} catch (Exception e1) {
																																								e1.printStackTrace();
																																							}
																																							viewLogs.open();
																																						}

																																					}
																																				});
																																				mntm_viewConsole.setImage(SWTResourceManager.getImage(ServerList.class,
																																						"/action_log.png"));
																																				mntm_viewConsole
																																						.setText("\u67E5\u770B-\u5E94\u7528\u63A7\u5236\u53F0(&L)");
																																				// 下载应用日志文件
																																				MenuItem mntm_downloadLogFile = new MenuItem(menu, SWT.NONE);
																																				mntm_downloadLogFile.addSelectionListener(new SelectionAdapter() {
																																					@Override
																																					public void widgetSelected(SelectionEvent e) {
																																						ServerModel server = getSelectedServerModel();
																																						if (server != null) {
																																							try {
																																								int agentPort = Integer.parseInt(server.getAgentPort());
																																								if (NetWorkUtil.TestConnectBySocket(
																																										server.getServerIp(), agentPort, 2000)) {
																																									String downloadFile = "";
																																									FileDialog fileDialog = new FileDialog(getShell());
																																									fileDialog.setText("保存文件");
																																									if (server.getMiddlewareType().toLowerCase()
																																											.contains("weblogic")) {
																																										fileDialog.setFileName("syslog.log");
																																										downloadFile = server.getDomainPath().replace(
																																												"\\", "/")
																																												+ "/syslog.log";
																																									} else if (server.getMiddlewareType().toLowerCase()
																																											.contains("websphere")) {
																																										fileDialog.setFileName("SystemOut.log");
																																										downloadFile = server.getDomainPath().replace(
																																												"\\", "/")
																																												+ "/logs/"
																																												+ server.getServerName()
																																												+ "/SystemOut.log";
																																									}
																																									// 默认目录设置为：桌面
																																									fileDialog.setFilterPath(Tool.getDesktopPath());
																																									String saveFilePath = fileDialog.open();
																																									boolean go = true;
																																									if (saveFilePath != null) {
																																										File existsFile = new File(saveFilePath);
																																										if (existsFile.exists()) {
																																											if (MessageBoxUc.OpenConfirm(getShell(),
																																													existsFile + " 文件已存在，是否要覆盖文件？")) {
																																												go = true;
																																											} else {
																																												go = false;
																																											}
																																										}
																																										if (go) {
																																											DownloadFile download = new DownloadFile(
																																													getShell(), downloadFile,
																																													saveFilePath, server);
																																											download.open();
																																										}
																																									}

																																								} else {
																																									MessageDialog.openInformation(getShell(), "操作提示",
																																											"连接Agent失败，请检查!");
																																								}
																																							} catch (Exception e1) {
																																								e1.printStackTrace();
																																							}
																																						}

																																					}
																																				});
																																				mntm_downloadLogFile.setImage(SWTResourceManager.getImage(
																																						ServerList.class, "/download_file.png"));
																																				mntm_downloadLogFile
																																						.setText("\u4E0B\u8F7D-\u5E94\u7528\u65E5\u5FD7\u6587\u4EF6(&D)");
																																				// 在浏览器中打开
																																				MenuItem mntm_openInbrowser = new MenuItem(menu, SWT.NONE);
																																				mntm_openInbrowser.addSelectionListener(new SelectionAdapter() {
																																					@Override
																																					public void widgetSelected(SelectionEvent e) {
																																						ServerModel server = getSelectedServerModel();
																																						if (server != null) {
																																							Tool.RunCommand(new String[] { "cmd.exe", "/c",
																																									"start " + server.getAppVerification() });
																																						}
																																					}
																																				});
																																				mntm_openInbrowser.setImage(SWTResourceManager.getImage(
																																						ServerList.class, "/ie.png"));
																																				mntm_openInbrowser
																																						.setText("\u5728\u6D4F\u89C8\u5668\u4E2D\u6253\u5F00(&B)");
																																				
																																						new MenuItem(menu, SWT.SEPARATOR);
																																						// 刷新当前列表
																																						MenuItem mntm_refreshServer = new MenuItem(menu, SWT.NONE);
																																						mntm_refreshServer.addSelectionListener(new SelectionAdapter() {
																																							@Override
																																							public void widgetSelected(SelectionEvent e) {
																																								getServerData(selectionGroupId, false);
																																							}
																																						});
																																						mntm_refreshServer.setText("\u5237\u65B0(&F5)");
																																						
																																								new MenuItem(menu, SWT.SEPARATOR);
																																								
																																										mntm_testAgent = new MenuItem(menu, SWT.NONE);
																																										mntm_testAgent.setImage(SWTResourceManager.getImage(ServerList.class,
																																												"/connect.png"));
																																										mntm_testAgent.addSelectionListener(new SelectionAdapter() {
																																											@Override
																																											public void widgetSelected(SelectionEvent e) {
																																												testAgent();
																																											}
																																										});
																																										mntm_testAgent
																																												.setText("\u68C0\u6D4BAgent\u7F51\u7EDC&&\u5BC6\u7801(F6)");
																																										// 批量修改选中应用的Agent密码
																																										MenuItem mntm_modiAgentPwd = new MenuItem(menu, SWT.NONE);
																																										mntm_modiAgentPwd.addSelectionListener(new SelectionAdapter() {
																																											@Override
																																											public void widgetSelected(SelectionEvent e) {
																																												Object[] objs = getSelectedServers();
																																												BatchModifiAgentPwd modpwd = new BatchModifiAgentPwd(
																																														getShell(), objs);
																																												// 修改确认后，刷新当前应用列表
																																												if (modpwd.open() == 0) {
																																													getServerData();
																																												}
																																											}
																																										});
																																										mntm_modiAgentPwd
																																												.setText("\u6279\u91CF\u4FEE\u6539\u5E94\u7528\u7684Agent\u5BC6\u7801(&P)");
																																										
																																												new MenuItem(menu, SWT.SEPARATOR);
																																												
																																														MenuItem mntmm_manageServer = new MenuItem(menu, SWT.CASCADE);
																																														mntmm_manageServer.setText("\u670D\u52A1\u5668(&M)");
																																														
																																																Menu menu_2 = new Menu(mntmm_manageServer);
																																																mntmm_manageServer.setMenu(menu_2);
																																																// 服务器-文件管理
																																																MenuItem mntms_fileManage = new MenuItem(menu_2, SWT.NONE);
																																																mntms_fileManage.setImage(null);
																																																mntms_fileManage.addSelectionListener(new SelectionAdapter() {
																																																	@Override
																																																	public void widgetSelected(SelectionEvent e) {
																																																		try {
																																																			ServerModel server = getSelectedServerModel();// 右键获取
																																																			if (NetWorkUtil.TestConnectBySocket(server.getServerIp(),
																																																					Integer.parseInt(server.getAgentPort()), 3000)) {
																																																				ManageServerFile manageServerFile = new ManageServerFile(
																																																						getShell(), server, server.getAppPath());
																																																				int status = manageServerFile.open();
																																																				if (status == 0) {
																																																					// 确定
																																																					if (manageServerFile.getSelectPath() != null
																																																							&& !manageServerFile.getSelectPath()
																																																									.equals("")) {

																																																					}
																																																				}
																																																			} else {
																																																				MessageBoxUc.OpenError(getShell(), "连接Agent失败，请检查!");
																																																			}
																																																		} catch (Exception ex) {
																																																			MessageBoxUc.OpenOk(getShell(), "发生异常:" + ex.getMessage());
																																																		}
																																																	}
																																																});
																																																mntms_fileManage.setText("\u6587\u4EF6\u7BA1\u7406(&F)");
																																																
																																																		new MenuItem(menu, SWT.SEPARATOR);
																																																		// 右键菜单：添加应用
																																																		MenuItem mntm_add = new MenuItem(menu, SWT.CASCADE);
																																																		mntm_add.addSelectionListener(new SelectionAdapter() {
																																																			@Override
																																																			public void widgetSelected(SelectionEvent e) {
																																																				ConfigServer config = new ConfigServer(getShell(), null);
																																																				config.open();
																																																				refresh();
																																																			}
																																																		});
																																																		mntm_add.setImage(SWTResourceManager.getImage(ServerList.class,
																																																				"/server_add.png"));
																																																		mntm_add.setText("\u6DFB\u52A0\u5E94\u7528(&A)");
																																																		
																																																				MenuItem mntm_group = new MenuItem(menu, SWT.CASCADE);
																																																				mntm_group.setImage(SWTResourceManager.getImage(ServerList.class,
																																																						"/chart_organisation.png"));
																																																				mntm_group
																																																						.setText("\u5C06\u52FE\u9009\u5E94\u7528\u79FB\u5230\u5206\u7EC4(&G)");
																																																				
																																																						menu_move_group = new Menu(mntm_group);
																																																						mntm_group.setMenu(menu_move_group);
																																																						
																																																								MenuItem mntm_fastLabel = new MenuItem(menu, SWT.CASCADE);
																																																								mntm_fastLabel.setImage(SWTResourceManager.getImage(ServerList.class,
																																																										"/bright_star.png"));
																																																								mntm_fastLabel.setText("\u6DFB\u52A0\u5230\u5FEB\u6377\u6807\u7B7E");
																																																								// 快捷标签
																																																								menu_label = new Menu(mntm_fastLabel);
																																																								mntm_fastLabel.setMenu(menu_label);
																																																								// 编辑应用
																																																								MenuItem mntm_editServer = new MenuItem(menu, SWT.CASCADE);
																																																								mntm_editServer.setImage(SWTResourceManager.getImage(ServerList.class,
																																																										"/edit.png"));
																																																								mntm_editServer.addSelectionListener(new SelectionAdapter() {
																																																									@Override
																																																									public void widgetSelected(SelectionEvent e) {
																																																										ServerModel server = getSelectedServerModel();
																																																										if (server != null) {
																																																											editSelectTableItem(server.getId());
																																																										}
																																																									}
																																																								});
																																																								mntm_editServer.setText("\u4FEE\u6539(&E)");
																																																								
																																																										// 删除选中
																																																										MenuItem mntmd_deleteSelect = new MenuItem(menu, SWT.CASCADE);
																																																										mntmd_deleteSelect.setImage(SWTResourceManager.getImage(
																																																												ServerList.class, "/icons/full/message_error.gif"));
																																																										mntmd_deleteSelect.addSelectionListener(new SelectionAdapter() {
																																																											@Override
																																																											public void widgetSelected(SelectionEvent e) {
																																																												deleteApplications();
																																																											}
																																																										});
																																																										mntmd_deleteSelect.setText("\u5220\u9664(&D)");
																																																										
																																																												new MenuItem(menu, SWT.SEPARATOR);
																																																												// 下一步
																																																												MenuItem mntm_nextStep = new MenuItem(menu, SWT.NONE);
																																																												mntm_nextStep.setImage(null);
																																																												mntm_nextStep.addSelectionListener(new SelectionAdapter() {
																																																													@Override
																																																													public void widgetSelected(SelectionEvent e) {
																																																														nextStep();
																																																													}
																																																												});
																																																												mntm_nextStep.setText("\u4E0B\u4E00\u6B65(&N)");
																																																												
																																																												Composite composite_3 = new Composite(composite_list, SWT.NONE);
																																																												FormData fd_composite_3 = new FormData();
																																																												fd_composite_3.bottom = new FormAttachment(composite_1, -1);
																																																												fd_composite_3.top = new FormAttachment(0);
																																																												fd_composite_3.left = new FormAttachment(0);
																																																												fd_composite_3.right = new FormAttachment(100);
																																																												composite_3.setLayoutData(fd_composite_3);
																																																												GridLayout gl_composite_3 = new GridLayout(6, false);
																																																												composite_3.setLayout(gl_composite_3);
																																																												formToolkit.adapt(composite_3);
																																																												formToolkit.paintBordersFor(composite_3);
																																																												
																																																														Link link_selectAll = new Link(composite_3, SWT.NONE);
																																																														GridData gd_link_selectAll = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
																																																														gd_link_selectAll.verticalIndent = 2;
																																																														gd_link_selectAll.horizontalIndent = 3;
																																																														link_selectAll.setLayoutData(gd_link_selectAll);
																																																														link_selectAll.addSelectionListener(new SelectionAdapter() {
																																																															@Override
																																																															public void widgetSelected(SelectionEvent e) {
																																																																selectAll();
																																																															}
																																																														});
																																																														formToolkit.adapt(link_selectAll, true, true);
																																																														link_selectAll.setText("<a>\u5168\u9009</a>");
																																																														
																																																																Label label = new Label(composite_3, SWT.CENTER);
																																																																GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
																																																																gd_label.horizontalIndent = 5;
																																																																label.setLayoutData(gd_label);
																																																																label.setText("|");
																																																																label.setAlignment(SWT.CENTER);
																																																																formToolkit.adapt(label, true, true);
																																																																		//取消选择
																																																																		Link link_cancelSelect = new Link(composite_3, SWT.NONE);
																																																																		GridData gd_link_cancelSelect = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
																																																																		gd_link_cancelSelect.horizontalIndent = 5;
																																																																		link_cancelSelect.setLayoutData(gd_link_cancelSelect);
																																																																		link_cancelSelect.addSelectionListener(new SelectionAdapter() {
																																																																			@Override
																																																																			public void widgetSelected(SelectionEvent e) {
																																																																				cancelSelect();
																																																																			}
																																																																		});
																																																																		formToolkit.adapt(link_cancelSelect, true, true);
																																																																		link_cancelSelect.setText("<a>\u53D6\u6D88\u9009\u62E9</a>");
																																																																		
																																																																		Link link = new Link(composite_3, SWT.NONE);
																																																																		GridData gd_link = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
																																																																		gd_link.horizontalIndent = 5;
																																																																		link.setLayoutData(gd_link);
																																																																		link.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
																																																																		link.addSelectionListener(new SelectionAdapter() {
																																																																			@Override
																																																																			public void widgetSelected(SelectionEvent e) {
																																																																				deleteApplications();
																																																																			}
																																																																		});
																																																																		formToolkit.adapt(link, true, true);
																																																																		link.setText("<a>\u5220\u9664</a>");
																																																																		
																																																																		Label lblNewLabel = new Label(composite_3, SWT.CENTER);
																																																																		GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
																																																																		gd_lblNewLabel.horizontalIndent = 5;
																																																																		lblNewLabel.setLayoutData(gd_lblNewLabel);
																																																																		formToolkit.adapt(lblNewLabel, true, true);
																																																																		lblNewLabel.setText("|");
																																																																				
																																																																						link_showAll = new Link(composite_3, SWT.NONE);
																																																																						GridData gd_link_showAll = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
																																																																						gd_link_showAll.horizontalIndent = 5;
																																																																						link_showAll.setLayoutData(gd_link_showAll);
																																																																						link_showAll.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
																																																																						link_showAll
																																																																								.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
																																																																						link_showAll.addSelectionListener(new SelectionAdapter() {
																																																																							@Override
																																																																							public void widgetSelected(SelectionEvent e) {
																																																																								selectionGroupId = "";
																																																																								getServerData();
																																																																								// 切换选项卡
																																																																								tabFolder_serverList.setSelection(tabItem_serverList);
																																																																							}

																																																																						});
																																																																						link_showAll.setText("<a>\u663E\u793A\u5168\u90E8</a>");
																																																												table_serverList.setFocus();
		tabFolder_servers.setSelection(tabItem_servers);

		tabItem_label = new CTabItem(tabFolder_servers, SWT.NONE);
		tabItem_label.setImage(SWTResourceManager.getImage(ServerList.class,
				"/bright_star.png"));
		tabItem_label.setText(" \u5FEB\u6377\u6807\u7B7E  ");

		Composite composite = new Composite(tabFolder_servers, SWT.NONE);
		tabItem_label.setControl(composite);
		composite.setLayout(new FormLayout());

		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.right = new FormAttachment(100);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.left = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);
		// 添加新标签
		btn_addLabel = new ToolItem(toolBar, SWT.NONE);
		btn_addLabel.setText("\u6DFB\u52A0");
		btn_addLabel.setImage(SWTResourceManager.getImage(ServerList.class,
				"/add.png"));
		btn_addLabel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createFastLabel(null);
			}
		});
		btn_addLabel.setToolTipText("\u6DFB\u52A0\u6807\u7B7E");

		composite_label = new Composite(composite, SWT.INHERIT_FORCE);
		composite_label.setBackgroundMode(SWT.INHERIT_FORCE);
		composite_label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				composite_label.setFocus();
			}
		});
		composite_label.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		composite_label.setLayout(new RowLayout(SWT.HORIZONTAL));
		FormData fd_composite_label = new FormData();
		fd_composite_label.left = new FormAttachment(0, 1);
		fd_composite_label.bottom = new FormAttachment(100);
		fd_composite_label.right = new FormAttachment(100);
		fd_composite_label.top = new FormAttachment(0, 23);
		composite_label.setLayoutData(fd_composite_label);

		Composite composite_bottom = new Composite(this, SWT.NONE);
		fd_sashForm.bottom = new FormAttachment(100, -41);
		sashForm.setWeights(new int[] {157, 694});
		composite_bottom.setLayout(new FormLayout());
		FormData fd_composite_bottom = new FormData();
		fd_composite_bottom.bottom = new FormAttachment(100, -3);
		fd_composite_bottom.left = new FormAttachment(0);
		fd_composite_bottom.top = new FormAttachment(100, -40);
		fd_composite_bottom.right = new FormAttachment(100);
		composite_bottom.setLayoutData(fd_composite_bottom);

		// 点击下步，跳转到“第2步：选择发布文件夹”
		btn_nextStep = new Button(composite_bottom, SWT.NONE);
		FormData fd_btn_nextStep = new FormData();
		fd_btn_nextStep.top = new FormAttachment(0, 5);
		fd_btn_nextStep.right = new FormAttachment(100, -19);
		fd_btn_nextStep.left = new FormAttachment(100, -114);
		fd_btn_nextStep.bottom = new FormAttachment(0, 35);
		btn_nextStep.setLayoutData(fd_btn_nextStep);
		btn_nextStep.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		btn_nextStep
				.setToolTipText("\u8BF7\u52FE\u9009\u9700\u8981\u8FDB\u884C\u53D1\u5E03\u7684\u5E94\u7528\uFF0C\u7136\u540E\u70B9\u51FB\u201C\u4E0B\u4E00\u6B65\u201D\u3002");
		btn_nextStep.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nextStep();
			}
		});
		btn_nextStep.setText("\u4E0B\u4E00\u6B65(&N)");
		refresh();
		loadFastLabel();
	}

	/**
	 * 加载快捷标签
	 */
	private void loadFastLabel() {
		try {
			// 判断是否已加载标签，如果已加载则不重新获取
			if (composite_label.getChildren().length <= 0) {
				// 先注销控件
				for (Control ctr : composite_label.getChildren()) {
					if (!ctr.isDisposed()) {
						ctr.dispose();
					}
				}
				// 获取快捷标签
				List<FastLabelModel> labelList = FastLabelDAO.getAllFastLabel();
				for (FastLabelModel labelModel : labelList) {
					createFastLabel(labelModel);
				}
			}
		} catch (Exception e1) {
			MessageBoxUc.OpenError(getShell(),
					"获取快捷标签数据出错，原因:" + e1.getMessage());
			e1.printStackTrace();
		}
	}

	/**
	 * 创建快捷标签
	 * 
	 * @param labelModel
	 */
	private void createFastLabel(FastLabelModel labelModel) {
		final FastLabelUc fluc = new FastLabelUc(composite_label,
				SWT.INHERIT_FORCE, labelModel);
		fluc.lbl_delete.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				// 删除标签
				deleteFastLabel(fluc);
			}
		});

		fluc.lbl_name.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 1) {// 左键
					// 选中标签，查询应用
					fluc.setSelectedStyle();
					queryServerByLabel();
					try {
						// 累计点击数，点击数多的标签排在前面
						FastLabelDAO.updateClicks(fluc.getLabelId());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		// 编辑
		fluc.lbl_name.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				fluc.setEdit();
			}
		});

	}

	/**
	 * 删除选中应用
	 */
	private void deleteApplications(){		
		try {
			Object[] objs = getSelectedServers();
			if (objs != null && objs.length > 0) {
				if (MessageBoxUc.OpenConfirm(getShell(), "是否要删除选中的"
						+ objs.length + "条记录？")) {
					List<String> idList = new ArrayList<String>();
					for (Object obj : objs) {
						idList.add(obj.toString());
					}
					ServerModelDAO.deleteServer(idList);
					// 删除之后，重新加载数据
					refresh();
				}
			} else {
				ServerModel server = getSelectedServerModel();
				if (server != null) {
					if (MessageBoxUc.OpenConfirm(getShell(),
							"是否要删除选中的“" + server.getName() + "”记录？")) {
						ServerModelDAO.deleteServer(server.getId());
						// 删除之后，重新加载数据
						refresh();
					}
				}
			}
		} catch (Exception ex) {
			MessageBoxUc.OpenError(getShell(), ex.getMessage());
		}
	}
	/**
	 * 根据标签查询应用
	 */
	private void queryServerByLabel() {
		// log.info("根据标签查询应用");
		List<String> labelIds = new ArrayList<String>();
		for (Control ctr : composite_label.getChildren()) {
			if (ctr instanceof FastLabelUc) {
				FastLabelUc fluc = (FastLabelUc) ctr;
				if (fluc.isSelected && fluc.getLabelId() != null) {
					labelIds.add(fluc.getLabelId());
				}
			}
		}
		try {
			if (labelIds.size() > 0) {
				List<String> sids = FastLabelServerDAO.getServerIds(labelIds
						.toArray());
				if (sids != null && sids.size() > 0) {
					getServerData(sids, true);
				}
			} else {
				selectionGroupId = "";
				getServerData();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除快捷标签
	 */
	private void deleteFastLabel(FastLabelUc fluc) {
		try {
			for (Control ctr : composite_label.getChildren()) {
				FastLabelUc fluc_ctr = (FastLabelUc) ctr;
				if (fluc_ctr == fluc && !fluc.isDisposed()) {
					fluc.deleteLabel();
					// 同步删除标签应用对照表
					FastLabelServerDAO.deleteLabelServer(fluc.getLabelId());
					fluc.dispose();
				}
			}
			composite_label.layout();// 刷新面板
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 折叠分组树
	 */
	private void serverGroupExpand() {
		if (isExpand) {
			isExpand = false;
		} else {
			isExpand = true;
		}
		for (TreeItem ti : tree_servers.getItems()) {
			ti.setExpanded(isExpand);
		}
	}

	/**
	 * 点击“下一步”
	 */
	public void nextStep() {
		// 获取所有checkbox被选中的列 返回一个Object数组
		Object[] objects = checkboxTableViewer.getCheckedElements();
		if (objects.length == 0) {
			MessageBoxUc.OpenOk(getShell(), "请先勾选需要发布的版本!");
			return;
		} else {
			MainForm.createServerTab();
			MainForm.SetTopControl(StepType.STEP2);
		}
	}

	/**
	 * 右键移动到“分组”
	 */
	public void createGroupMenu() {
		for (MenuItem mi : menu_move_group.getItems()) {
			mi.dispose();
		}
		String sqlStr = "select id,name from groups order by sortNum desc";
		ResultSet rs = null;
		try {
			sql.openDatabase();
			// 获取需要发布的应用
			rs = sql.executeQuery(sqlStr);
			// 生成控件
			while (rs.next()) {
				final MenuItem mntmNewItem = new MenuItem(menu_move_group,
						SWT.NONE);
				mntmNewItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						tabFolder_serverList.setEnabled(false);
						btn_nextStep.setEnabled(false);
						Object[] objs = checkboxTableViewer
								.getCheckedElements();
						if (objs.length > 0) {
							if (MessageDialog.openConfirm(getShell(), "操作确认",
									"是否要将勾选的记录移动到 “" + mntmNewItem.getText()
											+ "” 分组下面?")) {
								try {
									sql.openDatabase();
									for (Object id : objs) {
										String sqlStr = "update servers set groupId='"
												+ mntmNewItem.getData()
												+ "' where id = '" + id + "'";
										sql.executeUpdate(sqlStr);
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
									sql.close();
								}
								getServerData(mntmNewItem.getData().toString(),
										null, true);
								getTreeServers();
							}
						}
						tabFolder_serverList.setEnabled(true);
						btn_nextStep.setEnabled(true);
					}
				});
				mntmNewItem.setText(rs.getString("name"));
				mntmNewItem.setData(rs.getString("id"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 创建快捷标签右击菜单
	 */
	private void createFastLabelMenu() {
		for (MenuItem mi : menu_label.getItems()) {
			mi.dispose();
		}
		try {
			List<FastLabelModel> labelList = FastLabelDAO.getAllFastLabel();
			for (FastLabelModel flm : labelList) {
				final MenuItem mntmNewItem = new MenuItem(menu_label, SWT.NONE);
				mntmNewItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						// log.info("添加到快捷标签");
						String[] serverIds = getSelectedServers();
						MenuItem select_labelMi = (MenuItem) e.getSource();
						try {
							FastLabelServerDAO.insertLabelServer(select_labelMi
									.getData().toString(), serverIds);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
				mntmNewItem.setText(flm.getName());
				mntmNewItem.setData(flm.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 编辑选中的记录
	 */
	public void editSelectTableItem(String serverId) {
		if (serverId != null) {
			try {
				ConfigServer cs = new ConfigServer(getShell(), serverId);
				cs.open();
			} catch (Exception ex) {
				MessageDialog.openError(getShell(), "错误提示",
						"删除出错，原因：" + ex.getMessage());
			} finally {
				sql.close();
			}
			// 删除之后，重新加载数据
			refresh();
		}
	}

	/**
	 * 测试Agent状态
	 */
	private void testAgent() {
		// 第4列是IP，第7列是Agent端口号
		if (table_serverList != null) {
			for (final TableItem item : table_serverList.getItems()) {
				String ip = item.getText(4);
				int port = Integer.parseInt(item.getText(7));
				SocketAddress agentEndpoint = new InetSocketAddress(ip, port);
				testBySocket(item, agentEndpoint);
			}
		}
	}

	/**
	 * 全选tableItem
	 */
	public void selectAll() {
		for (TableItem tableItem : table_serverList.getItems()) {
			tableItem.setChecked(true);
		}
	}

	/**
	 * 取消选择tableItem
	 */
	public void cancelSelect() {
		for (TableItem tableItem : table_serverList.getItems()) {
			tableItem.setChecked(false);
		}
	}

	public void testBySocket(TableItem item, SocketAddress endpoint) {
		item.setText(1, "正在检测...");
		item.setImage(SWTResourceManager
				.getImage(ServerList.class, "/warn.png"));
		// 由于Socket会造成I/O阻塞，因此放在线程中。 如果应用IP连不上，6000毫秒后超时。
		if (item.getData() != null) {// item.getData()存放了server的记录ID
			TestSocketThread tst = new TestSocketThread(item, item.getData()
					.toString(), 6000);
			Thread thread = new Thread(tst);
			thread.start();
		}
	}

	/**
	 * 获取当前右键选中的ServerModel
	 * 
	 * @return
	 */
	public ServerModel getSelectedServerModel() {
		try {
			ServerModel server = null;
			int selectIndex = table_serverList.getSelectionIndex();
			TableItem item = null;
			if (selectIndex >= 0) {
				item = table_serverList.getItem(selectIndex);
				String id = item.getData().toString();
				server = ServerModelDAO.getServerModelById(id);
			}
			return server;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取已选择的应用记录ID
	 * 
	 * @return
	 */
	public String[] getSelectedServers() {
		Object[] objIds = checkboxTableViewer.getCheckedElements();
		if (objIds != null && objIds.length > 0) {
			String[] ids = new String[objIds.length];
			for (int i = 0; i < objIds.length; i++) {
				ids[i] = objIds[i].toString();
			}
			return ids;
		}
		return null;
	}

	private void getServerData(List<String> ids, boolean isAutoSelect) {
		getServerData(null, ids, isAutoSelect);
	}

	private void getServerData(String groupId, boolean isAutoSelect) {
		getServerData(groupId, null, isAutoSelect);
	}

	/**
	 * 加载应用数据
	 */
	public void getServerData() {
		getServerData(selectionGroupId, false);

	}

	/**
	 * 根据应用组ID，加载应用数据
	 */
	private void getServerData(String groupId, List<String> ids,
			boolean isAutoSelect) {
		try {
			String sqlStr = "select \"---\" test,a.id,b.name groupName,a.name,a.serverName,a.serverIp,a.domainPath,a.domainPort,"
					+ "(select c.name from dicts c where c.id=a.systemTypeId) systemTypeName,"
					+ "(select d.name from dicts d where d.id=a.middlewareTypeId) middlewareTypeName,agentPort,appPath,appVerification,backUpPath,backupType"
					+ " from servers a,groups b where a.groupId=b.id ";
			if (groupId != null && !groupId.equals("")) {
				sqlStr += " and a.groupId='" + groupId + "' ";
			}
			if (ids != null && ids.size() > 0) {
				String inId = "";
				for (String s : ids) {
					inId += ",'" + s + "'";
				}
				inId = inId.substring(1);
				sqlStr += " and a.id in (" + inId + ")";
			}
			sqlStr += " order by groupName,a.name,serverIp desc";
			// 要显示的列
			TableViewThread tableViewThread = new TableViewThread(
					Display.getDefault(), sqlStr, table_serverList,
					new String[] { "id", "test", "name", "groupName",
							"serverIp", "domainPort", "systemTypeName",
							"agentPort", "middlewareTypeName", "appPath",
							"appVerification", "domainPath" }, "id", true,
					isAutoSelect);
			Thread th = new Thread(tableViewThread);
			th.start();
			table_serverList.setFocus();
			serverList.clear();
			serverList = ServerModelDAO
					.getServerModelListByGroupId(selectionGroupId);
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBoxUc
					.OpenError(getShell(), "获取应用记录出错，原因:" + ex.getMessage());
		}
	}

	// 点击左侧分组树时显示分组应用
	public void showSelectedServer() {
		for (TreeItem ti : tree_servers.getSelection()) {
			String currGroupId = "";
			if (ti.getData() instanceof ServerModel) {
				// 应用
				ServerModel server = (ServerModel) ti.getData();
				currGroupId = server.getGroupId();
				setSelectTableItem(server.getId());
			} else {
				// 分组
				tree_servers.select(ti);
				currGroupId = ti.getData().toString();
				ti.setExpanded(!ti.getExpanded());
			}
			if (!currGroupId.equals(selectionGroupId)) {
				selectionGroupId = currGroupId;
				getServerData(selectionGroupId, false);
			}

		}
	}

	/**
	 * 刷新应用数据列表
	 */
	public void refresh() {
		getServerData();
		getTreeServers();
	}

	/**
	 * 获取服务器分组
	 */
	private void getTreeServers() {
		try {
			// 先把原来的给清空
			for (TreeItem ti : tree_servers.getItems()) {
				ti.dispose();
			}
			// 从数据库获取所有的应用
			serverList.clear();
			serverList = ServerModelDAO.getAllServerModel();
			for (ServerModel s : serverList) {
				createTree(s);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openInformation(getShell(), "错误提示", "获取应用记录出错，原因："
					+ ex.getMessage());
		}
	}

	/**
	 * 创建分组下的“节点”
	 * 
	 * @param parentItem
	 * @param server
	 */
	private void createTree(ServerModel server) {
		TreeItem groupItem = createGroupTreeItem(server);
		TreeItem item;
		if (groupItem != null) {
			item = new TreeItem(groupItem, SWT.NONE);
		} else {
			item = new TreeItem(tree_servers, SWT.NONE);
		}

		if (server.getReleaseType() == null
				|| server.getReleaseType().equals("0")) {
			item.setText(server.getName() == null ? "" : server.getName());
			// 获取集群服务
			List<ClusterServicesModel> clusterServicesList = ClusterServicesDAO
					.getClusterServerModelList(server.getId());
			if (clusterServicesList != null && clusterServicesList.size() > 0) {
				server.setClusterServers(clusterServicesList);
				TreeItem treeItemCluster = new TreeItem(item, SWT.NONE);
				treeItemCluster.setText("集群服务");
				treeItemCluster.setData(server);
				treeItemCluster.setImage(SWTResourceManager.getImage(
						ServerList.class, "/chart_organisation.png"));
				for (ClusterServicesModel csm : clusterServicesList) {
					TreeItem treeItemServices = new TreeItem(treeItemCluster,
							SWT.NONE);
					treeItemServices.setText(csm.getName());
					treeItemServices.setData(server);
					treeItemServices.setImage(SWTResourceManager.getImage(
							ServerList.class, "/plugin.png"));
				}
			}
		} else {
			item.setText((server.getName() == null ? "" : server.getName())
					+ "(非应用)");
		}
		item.setImage(SWTResourceManager.getImage(ServerList.class,
				"/server.png"));
		item.setData(server);
		if (groupItem != null
				&& groupItem.getData().toString().equals(selectionGroupId)) {
			tree_servers.setSelection(item);
			tree_servers.setSelection(groupItem);
		}
	}

	/**
	 * 创建“分组”节点，如果分组不存在则创建并返回。
	 * 
	 * @param groupName
	 * @return
	 */
	private TreeItem createGroupTreeItem(ServerModel server) {
		boolean exists = false;
		for (TreeItem ti : tree_servers.getItems()) {
			if (!(ti.getData() instanceof ServerModel)
					&& ti.getText().equals(server.getGroupName())) {
				return ti;
			}
		}
		if (!exists) {
			TreeItem groupItem = null;
			groupItem = new TreeItem(tree_servers, SWT.NONE);
			groupItem.setImage(SWTResourceManager.getImage(ServerList.class,
					"/computer.png"));
			groupItem.setText(server.getGroupName());
			groupItem.setData(server.getGroupId());
			return groupItem;
		}
		return null;
	}

	/**
	 * 根据ID，选中列表中的行 点击左边分组树时，自动关联右侧列表记录（选中）
	 * 
	 * @param serverId
	 */
	public void setSelectTableItem(String serverId) {
		Table table = checkboxTableViewer.getTable();
		for (TableItem item : table.getItems()) {
			if (item.getData() != null
					&& item.getData().toString().equals(serverId)) {
				table.setSelection(item);
				if (item.getChecked()) {
					item.setChecked(false);
				} else {
					item.setChecked(true);
				}
				return;
			}
		}
	}

	/**
	 * 检查状态
	 * 
	 * @param testType
	 *            0-测试网络;1-测URL地址
	 */
	public void testStatus(int testType) {
		List<TreeItem> items = new ArrayList<TreeItem>();
		getAllTreeItem(tree_servers, items);
		for (TreeItem ti : items) {
			if (ti.getData() instanceof ServerModel) {
				ti.setImage(SWTResourceManager.getImage(ServerList.class,
						"/warn.png"));
				// 由于Socket会造成I/O阻塞，因此放在线程中。
				TestConnect agentTh = new TestConnect(ti, testType);
				Thread agentThread = new Thread(agentTh);
				agentThread.start();
			}
		}
	}

	/**
	 * 获取分组树中的所有节点
	 * 
	 * @param tree
	 * @param items
	 */
	public void getAllTreeItem(Tree tree, List<TreeItem> items) {
		if (tree != null) {
			for (TreeItem root : tree.getItems()) {
				for (TreeItem i : root.getItems()) {
					items.add(i);
				}
			}
		}
	}

	// Thread回调函数
	public static void setText(final TableItem tableItem,
			final ResultModel result) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!tableItem.isDisposed()) {
					if (result.getCode() >= 0) {
						tableItem.setImage(SWTResourceManager.getImage(
								ServerList.class, "/green.png"));
					} else {
						tableItem.setImage(SWTResourceManager.getImage(
								ServerList.class, "/red.png"));
					}
					// 把内容显示在第"2"列
					tableItem.setText(1, result.getMsg());
				}
			}
		});
	}

	public void threadCallBack(final TreeItem treeItem,
			final boolean connectStatus) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!treeItem.isDisposed()) {
					if (connectStatus) {
						treeItem.setImage(SWTResourceManager.getImage(
								ServerList.class, "/green.png"));
					} else {

						treeItem.setImage(SWTResourceManager.getImage(
								ServerList.class, "/red.png"));
					}
				}
			}
		});

	}

	/**
	 * 开启线程，测试应用和Agent状态是否正常
	 * 
	 * @author FengGeGe
	 * 
	 */
	public class TestSocketThread implements Runnable {
		int timeout = 1000;
		boolean result = false;
		TableItem tableItem;
		String id;

		public TestSocketThread(TableItem tableItem, String id, int timeout) {
			this.tableItem = tableItem;
			this.timeout = timeout;
			this.id = id;
		}

		@Override
		public void run() {
			// 测试IP和端口是否能访问
			// 不要在子线程中进行UI控件操作，否则会卡死
			for (ServerModel server : serverList) {
				if (server.getId().equals(id)) {
					String pwd = server.getAgentPassword();
					if (pwd.equals("")) {
						pwd = Tool.EncoderByMd5(pwd);
					}
					ServerList.setText(tableItem, AgentTestConnect
							.connectAgent(server.getServerIp(),
									Integer.parseInt(server.getAgentPort()),
									pwd));
				}
			}

		}
	}

	protected class TestConnect implements Runnable {
		ServerModel server;
		TreeItem treeItem;
		int testType = 0;

		/**
		 * 测试连接
		 * 
		 * @param treeItem
		 * @param testType
		 *            0-Socket;1-url
		 */
		public TestConnect(TreeItem treeItem, int testType) {
			this.treeItem = treeItem;
			this.server = (ServerModel) treeItem.getData();
			this.testType = testType;
		}

		@Override
		public void run() {
			int timeout = 4000;
			if (testType == 0) {
				if (NetWorkUtil.TestConnectBySocket(
						new InetSocketAddress(server.getServerIp(), Integer
								.parseInt(server.getAgentPort())), timeout)) {
					threadCallBack(treeItem, true);
				} else {
					threadCallBack(treeItem, false);
				}
			} else {
				if (NetWorkUtil.TestConnectByUrl(server.getAppVerification(),
						timeout)) {
					threadCallBack(treeItem, true);
				} else {
					threadCallBack(treeItem, false);
				}
			}
		}
	}
}
