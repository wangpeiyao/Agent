package cn.com.hnisi.view.composites;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.dao.ClusterServicesDAO;
import cn.com.hnisi.dao.ServerModelDAO;
import cn.com.hnisi.model.ClusterServicesModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.StepType;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.MainForm;
import cn.com.hnisi.view.component.MessageBoxUc;
import cn.com.hnisi.view.dialog.SeeFolder;

/**
 * 第3步：发布应用窗口
 * 
 * @author FengGeGe
 * 
 */
public class ServerRelease extends Composite {
	static Logger log = Logger.getLogger(ServerRelease.class);
	private CLabel lbl_step3;
	private CTabFolder tab_release;
	private Tree tree_servers;
	private SashForm sashForm;
	private Composite composite_tree;
	private Composite composite_replease;
	private CTabFolder tabFolder_serverList;
	private Text txt_selectFolder;

	public void setReleaseText(String releaseFolder) {
		txt_selectFolder.setText(releaseFolder);
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ServerRelease(Composite parent, int style) {
		super(parent, SWT.NONE);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		setLayout(new FormLayout());
		
		Composite composite_2 = new Composite(this, SWT.NONE);
		FormData fd_composite_2 = new FormData();
		fd_composite_2.bottom = new FormAttachment(0, 56);
		fd_composite_2.left = new FormAttachment(0);
		fd_composite_2.right = new FormAttachment(100);
		fd_composite_2.top = new FormAttachment(0, 2);
		composite_2.setLayoutData(fd_composite_2);
		
				lbl_step3 = new CLabel(composite_2, SWT.NONE);
				lbl_step3.setBounds(0, 8, 172, 38);
				lbl_step3.setImage(SWTResourceManager.getImage(ServerRelease.class,
						"/direction.png"));
				lbl_step3.setBackground(SWTResourceManager
						.getColor(SWT.COLOR_WIDGET_BACKGROUND));
				lbl_step3.setText("\u7B2C2\u6B65\uFF1A\u53D1\u5E03\u5E94\u7528");
				lbl_step3.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.NORMAL));

		Composite composite = new Composite(this, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(composite_2, 1);
		fd_composite.left = new FormAttachment(0);
		fd_composite.right = new FormAttachment(100);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new FormLayout());

		CLabel label_1 = new CLabel(composite, SWT.NONE);
		FormData fd_label_1 = new FormData();
		fd_label_1.bottom = new FormAttachment(100, -4);
		fd_label_1.right = new FormAttachment(0, 154);
		fd_label_1.left = new FormAttachment(0);
		label_1.setLayoutData(fd_label_1);
		label_1.setText("\u9009\u62E9\u53D1\u5E03\u6587\u4EF6\u5939(\u7248\u672C)");
		label_1.setImage(SWTResourceManager.getImage(ServerRelease.class,
				"/folder.png"));
		label_1.setAlignment(SWT.RIGHT);

		txt_selectFolder = new Text(composite, SWT.BORDER);
		txt_selectFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				GlobalConfig.ReleaseFolder = txt_selectFolder.getText();
			}
		});
		FormData fd_txt_selectFolder = new FormData();
		fd_txt_selectFolder.left = new FormAttachment(label_1, 6);
		fd_txt_selectFolder.bottom = new FormAttachment(100, -4);
		txt_selectFolder.setLayoutData(fd_txt_selectFolder);
		txt_selectFolder.setFont(SWTResourceManager
				.getFont("微软雅黑", 9, SWT.BOLD));

		Button btn_selectFolder = new Button(composite, SWT.NONE);
		fd_txt_selectFolder.right = new FormAttachment(btn_selectFolder, -6);
		FormData fd_btn_selectFolder = new FormData();
		fd_btn_selectFolder.top = new FormAttachment(0, 4);
		fd_btn_selectFolder.right = new FormAttachment(100, -92);
		btn_selectFolder.setLayoutData(fd_btn_selectFolder);
		btn_selectFolder.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		btn_selectFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectReleaseFolder();
			}
		});
		btn_selectFolder.setImage(SWTResourceManager.getImage(
				ServerRelease.class, "/folder_explore.png"));
		btn_selectFolder.setText("\u9009\u62E9...");

		Button btn_seeFolder = new Button(composite, SWT.NONE);
		FormData fd_btn_seeFolder = new FormData();
		fd_btn_seeFolder.top = new FormAttachment(0, 4);
		fd_btn_seeFolder.right = new FormAttachment(100, -12);
		fd_btn_seeFolder.bottom = new FormAttachment(btn_selectFolder, 0, SWT.BOTTOM);
		fd_btn_seeFolder.left = new FormAttachment(btn_selectFolder, 6);
		btn_seeFolder.setLayoutData(fd_btn_seeFolder);
		btn_seeFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SeeFolder sf = new SeeFolder(getShell(), txt_selectFolder
						.getText());
				sf.open();
			}
		});
		btn_seeFolder.setImage(SWTResourceManager.getImage(ServerRelease.class,
				"/list.png"));
		btn_seeFolder.setText("\u67E5\u770B(&L)");
		
				composite_tree = new Composite(this, SWT.NONE);
				fd_composite.bottom = new FormAttachment(composite_tree, -2);
				composite_tree.setLayout(new FillLayout(SWT.HORIZONTAL));
				FormData fd_composite_tree = new FormData();
				fd_composite_tree.left = new FormAttachment(0);
				fd_composite_tree.right = new FormAttachment(100);
				composite_tree.setLayoutData(fd_composite_tree);
				
						sashForm = new SashForm(composite_tree, SWT.SMOOTH);
						sashForm.setSashWidth(4);
						
								Composite composite_treeview = new Composite(sashForm, SWT.NONE);
								composite_treeview.setLayout(new FillLayout(SWT.HORIZONTAL));
								
										tabFolder_serverList = new CTabFolder(composite_treeview, SWT.BORDER
												| SWT.FLAT);
										tabFolder_serverList.setTabHeight(30);
										tabFolder_serverList.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
										tabFolder_serverList.setSelectionBackground(SWTResourceManager
												.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
										
												CTabItem tabItem_serverList = new CTabItem(tabFolder_serverList,
														SWT.NONE);
												tabItem_serverList.setImage(SWTResourceManager.getImage(
														ServerRelease.class, "/list.png"));
												tabItem_serverList
														.setText("\u9700\u53D1\u5E03\u5E94\u7528\u5217\u8868");
												
														tree_servers = new Tree(tabFolder_serverList, SWT.NONE);
														tree_servers.addKeyListener(new KeyAdapter() {
															@Override
															public void keyPressed(KeyEvent e) {
																if (e.keyCode == SWT.F5) {
																	TestServer();
																	return;
																}

																if (e.keyCode == SWT.F6) {
																	TestAgent();
																	return;
																}

															}
														});
														tabItem_serverList.setControl(tree_servers);
														// 选中节点，自动显示对应选项卡
														tree_servers.addSelectionListener(new SelectionAdapter() {
															@Override
															public void widgetSelected(SelectionEvent e) {
																for (TreeItem ti : tree_servers.getSelection()) {
																	if (!ti.getData().toString().equals("group")) {
																		ServerModel serverItem = (ServerModel) ti.getData();
																		for (CTabItem cti : tab_release.getItems()) {
																			if (serverItem.getId().equals(
																					cti.getData().toString())) {
																				tab_release.setSelection(cti);
																				break;
																			}
																		}
																	} else {
																		ti.setExpanded(!ti.getExpanded());
																	}
																}
															}
														});
														tree_servers.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
														
																Menu menu = new Menu(tree_servers);
																tree_servers.setMenu(menu);
																// 验证所有应用状态(&S)
																MenuItem mntm_checkServerStatus = new MenuItem(menu, SWT.NONE);
																mntm_checkServerStatus.setImage(SWTResourceManager.getImage(
																		ServerRelease.class, "/server_connect.png"));
																mntm_checkServerStatus.addSelectionListener(new SelectionAdapter() {
																	@Override
																	public void widgetSelected(SelectionEvent e) {
																		TestServer();
																	}
																});
																mntm_checkServerStatus
																		.setText("\u68C0\u6D4B\u5E94\u7528\u72B6\u6001(F5)");
																// 检测所有Agent状态(A)
																MenuItem mntm_checkAgentStatus = new MenuItem(menu, SWT.NONE);
																mntm_checkAgentStatus.setImage(SWTResourceManager.getImage(
																		ServerRelease.class, "/check.png"));
																mntm_checkAgentStatus.addSelectionListener(new SelectionAdapter() {
																	@Override
																	public void widgetSelected(SelectionEvent e) {
																		TestAgent();
																	}
																});
																mntm_checkAgentStatus.setText("\u68C0\u6D4BAgent\u72B6\u6001(F6)");
																
																		new MenuItem(menu, SWT.SEPARATOR);
																		
																				MenuItem mntm_copyUrl = new MenuItem(menu, SWT.NONE);
																				mntm_copyUrl.addSelectionListener(new SelectionAdapter() {
																					@Override
																					public void widgetSelected(SelectionEvent e) {
																						for (TreeItem ti : tree_servers.getSelection()) {
																							if (!ti.getData().toString().equals("group")) {
																								ServerModel serverItem = (ServerModel) ti.getData();
																								Clipboard clip = Toolkit.getDefaultToolkit()
																										.getSystemClipboard();
																								Transferable tText = new StringSelection(serverItem
																										.getAppVerification());
																								clip.setContents(tText, null);
																							}
																						}
																					}
																				});
																				mntm_copyUrl.setText("\u590D\u5236\u5730\u5740(&C)");
																				
																				MenuItem mntm_openInBrowser = new MenuItem(menu, SWT.NONE);
																				mntm_openInBrowser.addSelectionListener(new SelectionAdapter() {
																					@Override
																					public void widgetSelected(SelectionEvent e) {
																						for (TreeItem ti : tree_servers.getSelection()) {
																							if (!ti.getData().toString().equals("group")) {
																								ServerModel server= (ServerModel) ti.getData();
																								Tool.RunCommand(new String[]{"cmd.exe","/c","start "+server.getAppVerification()});
																							}
																						}
																					}
																				});
																				mntm_openInBrowser.setImage(SWTResourceManager.getImage(ServerRelease.class, "/ie.png"));
																				mntm_openInBrowser.setText("\u5728\u6D4F\u89C8\u5668\u4E2D\u6253\u5F00\u5730\u5740");
																				
																						composite_replease = new Composite(sashForm, SWT.NONE);
																						composite_replease.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
																						composite_replease.setLayout(new FormLayout());
																						
																								tab_release = new CTabFolder(composite_replease, SWT.BORDER | SWT.FLAT);
																								tab_release.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
																								tab_release.setMaximizeVisible(true);
																								tab_release.setMRUVisible(true);
																								FormData fd_tab_release = new FormData();
																								fd_tab_release.top = new FormAttachment(0);
																								fd_tab_release.left = new FormAttachment(0);
																								fd_tab_release.right = new FormAttachment(100, -1);
																								tab_release.setLayoutData(fd_tab_release);
																								tab_release.setMinimumCharacters(30);
																								tab_release.setTabHeight(30);
																								tab_release.setBackground(SWTResourceManager
																										.getColor(SWT.COLOR_WIDGET_BACKGROUND));
																								tab_release.addCTabFolder2Listener(new CTabFolder2Adapter() {
																									@Override
																									public void close(CTabFolderEvent event) {
																										if (MessageDialog
																												.openConfirm(getShell(), "关闭提示",
																														"关闭前，请确认已发布完成！\r\n确定退出请按“ 确认或OK ”，取消退出请按“ 取消或Cancel ”。")) {
																											if (tab_release.getSelectionIndex() == 0
																													&& tab_release.getItemCount() == 1) {
																												MainForm.SetTopControl(StepType.STEP1);
																											}
																											event.doit = true;
																										} else {
																											event.doit = false;
																										}

																									}

																									@Override
																									public void maximize(CTabFolderEvent event) {
																										tab_release.setMaximized(true);
																										sashForm.setMaximizedControl(composite_replease);
																										layout(true);
																									}

																									@Override
																									public void restore(CTabFolderEvent event) {
																										tab_release.setMaximized(false);
																										sashForm.setMaximizedControl(null);
																										layout(true);
																									}
																								});
																								tab_release.setSelectionBackground(SWTResourceManager
																										.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
																								
																										Composite composite_toolBar = new Composite(composite_replease,
																												SWT.BORDER);
																										fd_tab_release.bottom = new FormAttachment(100, -39);
																										composite_toolBar.setLayout(new FillLayout(SWT.HORIZONTAL));
																										FormData fd_composite_toolBar = new FormData();
																										fd_composite_toolBar.top = new FormAttachment(100, -37);
																										fd_composite_toolBar.bottom = new FormAttachment(100, -2);
																										fd_composite_toolBar.right = new FormAttachment(100, -1);
																										fd_composite_toolBar.left = new FormAttachment(0);
																										composite_toolBar.setLayoutData(fd_composite_toolBar);
																										
																												ToolBar toolBar = new ToolBar(composite_toolBar, SWT.FLAT | SWT.RIGHT);
																												toolBar.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
																												toolBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
																												
																														ToolItem tltm_batchStopServer = new ToolItem(toolBar, SWT.NONE);
																														tltm_batchStopServer.addSelectionListener(new SelectionAdapter() {
																															@Override
																															public void widgetSelected(SelectionEvent e) {
																																MessageBox messagebox = new MessageBox(getShell(),
																																		SWT.ICON_WARNING | SWT.YES | SWT.NO);
																																messagebox.setText("操作提示");
																																messagebox.setMessage("是否要批量停止应用？");
																																int message = messagebox.open();
																																if (message == SWT.YES) {
																																	for (Control con : tab_release.getChildren()) {
																																		if (con instanceof ReleaseInterface) {
																																			ReleaseInterface re = (ReleaseInterface) con;
																																			re.stopServer();
																																		}
																																	}
																																}
																															}
																														});
																														tltm_batchStopServer.setImage(SWTResourceManager.getImage(
																																ServerRelease.class, "/red.png"));
																														tltm_batchStopServer.setText("\u6279\u91CF\u505C\u6B62");
																														
																																ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
																																tltmNewItem.setEnabled(false);
																																tltmNewItem.setText("\u2192");
																																
																																		ToolItem tltm_batchBackUp = new ToolItem(toolBar, SWT.NONE);
																																		tltm_batchBackUp.addSelectionListener(new SelectionAdapter() {
																																			@Override
																																			public void widgetSelected(SelectionEvent e) {
																																				MessageBox messagebox = new MessageBox(getShell(),
																																						SWT.ICON_WARNING | SWT.YES | SWT.NO);
																																				messagebox.setText("操作提示");
																																				messagebox.setMessage("是否要批量备份应用？");
																																				int message = messagebox.open();
																																				if (message == SWT.YES) {
																																					for (Control con : tab_release.getChildren()) {
																																						if (con instanceof ReleaseInterface) {
																																							ReleaseInterface re = (ReleaseInterface) con;
																																							re.backUp();
																																						}
																																					}
																																				}
																																			}
																																		});
																																		tltm_batchBackUp.setImage(SWTResourceManager.getImage(ServerRelease.class, "/winrar.png"));
																																		tltm_batchBackUp.setText("\u6279\u91CF\u5907\u4EFD");
																																		
																																				ToolItem tltmNewItem_1 = new ToolItem(toolBar, SWT.NONE);
																																				tltmNewItem_1.setEnabled(false);
																																				tltmNewItem_1.setText("\u2192");
																																				
																																						ToolItem tltm_batchUploadFile = new ToolItem(toolBar, SWT.NONE);
																																						tltm_batchUploadFile.addSelectionListener(new SelectionAdapter() {
																																							@Override
																																							public void widgetSelected(SelectionEvent e) {
																																								MessageBox messagebox = new MessageBox(getShell(),
																																										SWT.ICON_WARNING | SWT.YES | SWT.NO);
																																								messagebox.setText("操作提示");
																																								messagebox.setMessage("是否要批量上传  " + txt_selectFolder.getText());
																																								int message = messagebox.open();
																																								if (message == SWT.YES) {
																																									for (Control con : tab_release.getChildren()) {
																																										if (con instanceof ReleaseInterface) {
																																											ReleaseInterface re = (ReleaseInterface) con;
																																											re.uploadFile();
																																										}
																																									}
																																								}
																																							}
																																						});
																																						tltm_batchUploadFile.setImage(SWTResourceManager.getImage(ServerRelease.class, "/copy.png"));
																																						tltm_batchUploadFile.setText("\u6279\u91CF\u4E0A\u4F20");
																																						
																																								ToolItem tltmNewItem_2 = new ToolItem(toolBar, SWT.NONE);
																																								tltmNewItem_2.setEnabled(false);
																																								tltmNewItem_2.setText("\u2192");
																																								
																																										ToolItem tltm_batchStartServer = new ToolItem(toolBar, SWT.NONE);
																																										tltm_batchStartServer.addSelectionListener(new SelectionAdapter() {
																																											@Override
																																											public void widgetSelected(SelectionEvent e) {
																																												if(MessageBoxUc.OpenConfirm(getShell(), "是否要批量启动应用，提醒：如果有数据脚本，请先执行脚本后再启动。\n继续启动应用请按“是”，取消请按“否”。")){
																																													for (Control con : tab_release.getChildren()) {
																																														if (con instanceof ReleaseInterface) {
																																															ReleaseInterface re = (ReleaseInterface) con;
																																															re.startServer();
																																														}
																																													}
																																												}
																																											}
																																										});
																																										tltm_batchStartServer.setImage(SWTResourceManager.getImage(
																																												ServerRelease.class, "/green.png"));
																																										tltm_batchStartServer.setText("\u6279\u91CF\u542F\u52A8");
																																										
																																												ToolItem tltmNewItem_4 = new ToolItem(toolBar, SWT.SEPARATOR);
																																												tltmNewItem_4.setText("New Item");
																																												
																																														ToolItem tltm_clearAll = new ToolItem(toolBar, SWT.NONE);
																																														tltm_clearAll.addSelectionListener(new SelectionAdapter() {
																																															@Override
																																															public void widgetSelected(SelectionEvent e) {
																																																for (Control con : tab_release.getChildren()) {
																																																	if (con instanceof ReleaseInterface) {
																																																		ReleaseInterface re = (ReleaseInterface) con;
																																																		re.clearConsole();
																																																	}
																																																}
																																															}
																																														});
																																														tltm_clearAll.setImage(SWTResourceManager.getImage(ServerRelease.class,
																																																"/clear.png"));
																																														tltm_clearAll.setText("\u6279\u91CF\u6E05\u7A7A\u8F93\u51FA");
																																														tabFolder_serverList.setSelection(tabItem_serverList);
																																														sashForm.setWeights(new int[] { 155, 705 });
																																														fd_composite_tree.top = new FormAttachment(0, 94);
																																														fd_composite_tree.bottom = new FormAttachment(100, -40);
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new FormLayout());
		FormData fd_composite_1 = new FormData();
		fd_composite_1.left = new FormAttachment(0);
		fd_composite_1.right = new FormAttachment(100);
		fd_composite_1.bottom = new FormAttachment(100);
		fd_composite_1.top = new FormAttachment(100, -40);
		
		composite_1.setLayoutData(fd_composite_1);
		
				Button btn_finish = new Button(composite_1, SWT.NONE);
				FormData fd_btn_finish = new FormData();
				fd_btn_finish.top = new FormAttachment(0, 5);
				fd_btn_finish.right = new FormAttachment(100, -19);
				fd_btn_finish.left = new FormAttachment(100, -114);
				fd_btn_finish.bottom = new FormAttachment(100, -5);
				btn_finish.setLayoutData(fd_btn_finish);
				btn_finish.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
				btn_finish.setText("\u8FD4 \u56DE(&R)");
				btn_finish.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (MessageBoxUc.OpenConfirm(getShell(),
								"如果正在上传文件，请等待上传完成后再返回，否会中断上传!\r\n确定返回请按“是”，继续发布请按“否”。")) {
							GlobalConfig.disconnect();
							MainForm.SetTopControl(StepType.STEP1);
							disposeControl();
						} else {
							e.doit = false;
						}
					}
				});

	}

	/**
	 * 选择需要发布的文件夹路径
	 */
	private void selectReleaseFolder() {
		DirectoryDialog dirDialog = new DirectoryDialog(getShell());
		dirDialog.setText("浏览文件夹");
		dirDialog.setMessage("请选择需要上传发布的文件夹");
		// 获取上一次打开的目录
		dirDialog.setFilterPath(txt_selectFolder.getText());
		dirDialog.open();
		// 记录本次打开的目录
		GlobalConfig.historySelectFolder = dirDialog.getFilterPath();
		GlobalConfig.ReleaseFolder = dirDialog.getFilterPath();
		txt_selectFolder.setText(GlobalConfig.ReleaseFolder);

	}

	private void disposeControl() {
		// 先销毁历史控件，防止内存溢出
		for (Control item : tab_release.getChildren()) {
			if (item instanceof ReleaseInterface) {
				item.dispose();
			}
		}
		// 先销毁历史控件，防止内存溢出
		for (CTabItem item : tab_release.getItems()) {
			item.dispose();
		}

		for (TreeItem ti : tree_servers.getItems()) {
			ti.dispose();
		}
	}

	/**
	 * 获取应用的ID查询数据库，再动态生成控件
	 * 
	 * @param objs
	 * @param releaseFolder
	 *            文件夹路径
	 */
	public void createServerTab(String[] ids) {
		// TODO 生成控件
		disposeControl();
		if (ids != null && ids.length > 0) {
			try {
				List<ServerModel> serverList=ServerModelDAO.getServerModelListByIds(ids);
				// 生成控件
				for(ServerModel server:serverList) {
					CTabItem tabItem = new CTabItem(tab_release, SWT.NONE);
					tabItem.setImage(SWTResourceManager.getImage(
							ServerRelease.class, "/server.png"));
					// 发布类型:0-应用发布1-文件上传
					// 显示选项卡标题名称
					if (server.getReleaseType() == null
							|| server.getReleaseType().equals("0")) {
						tabItem.setText(server.getServerIp() + ":"
								+ server.getDomainPort() + " ");
					} else {
						tabItem.setText(server.getServerIp() + "（非应用）");
					}
					// 这里将会用来跟分组树进行联动
					tabItem.setData(server.getId());
					//获取集群
					List<ClusterServicesModel> clusterServicesList= ClusterServicesDAO.getClusterServerModelList(server.getId());
					server.setClusterServers(clusterServicesList);
					
					// 自定义控件
					ReleaseInterface composite = new ReleaseInterface(
							tab_release, SWT.NONE, server, tabItem);
					tabItem.setControl(composite);
					// 生成分组树
					createTree(server);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageBoxUc.OpenError(getShell(),"生成控件时出错，原因：" + ex.getMessage());
			} 
		}
		if (tab_release.getItemCount() > 0) {
			tab_release.setSelection(0);
		}
	}

	/**
	 * 创建分组下的“节点”
	 * 
	 * @param parentItem
	 * @param server
	 */
	private void createTree(ServerModel server) {
		TreeItem groupItem = createGroupTreeItem(server.getGroupName());
		TreeItem item;
		if (groupItem != null) {
			item = new TreeItem(groupItem, SWT.NONE);
		} else {
			item = new TreeItem(tree_servers, SWT.NONE);
		}
		if (server.getReleaseType() == null
				|| server.getReleaseType().equals("0")) {
			item.setText(server.getServerIp() + ":" + server.getDomainPort()
					+ "   地址:" + server.getAppVerification());
			//获取集群服务
			List<ClusterServicesModel> clusterServicesList=server.getClusterServers();
			if(clusterServicesList!=null&&clusterServicesList.size()>0){
				server.setClusterServers(clusterServicesList);
				TreeItem treeItemCluster=new TreeItem(item, SWT.NONE);
				treeItemCluster.setText("集群服务");
				treeItemCluster.setData(server);
				treeItemCluster.setImage(SWTResourceManager.getImage(ServerList.class,
						"/chart_organisation.png"));
				for(ClusterServicesModel csm:clusterServicesList){
					TreeItem treeItemServices=new TreeItem(treeItemCluster, SWT.NONE);
					treeItemServices.setText(csm.getName());
					treeItemServices.setData(server);
					treeItemServices.setImage(SWTResourceManager.getImage(ServerList.class,
							"/plugin.png"));
				}
			}
		} else {
			item.setText(server.getServerIp() + "（非应用）");
		}
		item.setImage(SWTResourceManager.getImage(ServerRelease.class,
				"/server.png"));
		item.setData(server);
		// tree_servers.showItem(item);
	}

	/**
	 * 创建“分组”节点
	 * 
	 * @param groupName
	 * @return
	 */
	private TreeItem createGroupTreeItem(String groupName) {
		boolean exists = false;
		for (TreeItem ti : tree_servers.getItems()) {
			if (ti.getData().toString().equals("group")
					&& ti.getText().equals(groupName)) {
				return ti;
			}
		}
		if (!exists) {
			TreeItem groupItem = null;
			groupItem = new TreeItem(tree_servers, SWT.NONE);
			groupItem.setImage(SWTResourceManager.getImage(ServerRelease.class,
					"/computer.png"));
			groupItem.setText(groupName);
			groupItem.setData("group");
			return groupItem;
		}
		return null;
	}

	public void getAllTreeItem(Tree tree, List<TreeItem> items) {
		if (tree != null) {
			for (TreeItem root : tree.getItems()) {
				for (TreeItem i : root.getItems()) {
					items.add(i);
				}
			}
		}
	}

	/**
	 * 检测Agent
	 */
	public void TestAgent() {
		List<TreeItem> items = new ArrayList<TreeItem>();
		getAllTreeItem(tree_servers, items);
		for (TreeItem ti : items) {
			if (!ti.getData().toString().equals("group")) {
				ti.setImage(SWTResourceManager.getImage(ServerRelease.class,
						"/warn.png"));
				// 由于Socket会造成I/O阻塞，因此放在线程中。
				TestConnect agentTh = new TestConnect(ti, 0);
				Thread agentThread = new Thread(agentTh);
				agentThread.start();
			}
		}
	}

	/**
	 * 检测Server
	 */
	public void TestServer() {
		List<TreeItem> items = new ArrayList<TreeItem>();
		getAllTreeItem(tree_servers, items);
		for (TreeItem ti : items) {
			if (!ti.getData().toString().equals("group")) {
				ti.setImage(SWTResourceManager.getImage(ServerRelease.class,
						"/warn.png"));
				TestConnect agentTh = new TestConnect(ti, 1);
				Thread agentThread = new Thread(agentTh);
				agentThread.start();
			}
		}
	}

	public void threadCallBack(final TreeItem treeItem,
			final boolean connectStatus) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!treeItem.isDisposed()) {
					if (connectStatus) {
						treeItem.setImage(SWTResourceManager.getImage(
								ServerRelease.class, "/green.png"));
					} else {
						treeItem.setImage(SWTResourceManager.getImage(
								ServerRelease.class, "/red.png"));
					}
				}
			}
		});

	}

	protected class TestConnect implements Runnable {
		ServerModel server;
		TreeItem treeItem;
		int testType = 0;

		public TestConnect(TreeItem treeItem, int testType) {
			this.treeItem = treeItem;
			this.server = (ServerModel) treeItem.getData();
			this.testType = testType;
		}

		@Override
		public void run() {
			int timeout = 3000;
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
