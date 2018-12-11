package cn.com.hnisi.view.dialog;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.agent.services.function.DeleteServerFileHandler;
import cn.com.hnisi.agent.services.function.RemoteGetServerPath;
import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.model.FileModel;
import cn.com.hnisi.model.ResultModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.type.ShowType;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.component.MessageBoxUc;

/**
 * 管理、浏览服务器文件
 * 
 * @author WENZHIFENG
 * 
 */
public class ManageServerFile extends TitleAreaDialog {
	private Shell shell;
	private String path = new String();
	private ServerModel server;
	private Text text_selectPath;
	private Tree tree_serverPath;
	private MenuItem mntm_downloadFile;
	private final static String GO_BACK_STR = "...";

	/**
	 * 构造函数
	 * @param parentShell 父窗体
	 * @param server 应用实例
	 * @param path 目录
	 * @param isBrowser 是否为浏览操作，默认为True
	 */
	public ManageServerFile(Shell parentShell, ServerModel server, String path) {
		super(parentShell);
		setHelpAvailable(false);
		this.shell = parentShell;
		this.server = server;
		this.path = path;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		setMessage("已连接到 <"+server.getServerIp()+"> 服务器. ");
		setTitle("\u670D\u52A1\u5668\u6587\u4EF6/\u76EE\u5F55\u7684\u6D4F\u89C8\u3001\u4E0B\u8F7D\u3001\u5220\u9664.");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(null);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree_serverPath = new Tree(container, SWT.BORDER | SWT.MULTI);
		tree_serverPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==SWT.F5){
					getPath(text_selectPath.getText());
				}
			}
		});
		tree_serverPath.setLocation(3, 27);
		tree_serverPath.setSize(691, 331);

		Menu menu = new Menu(tree_serverPath);
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				if (tree_serverPath.getSelectionCount() > 0) {
					for (TreeItem ti : tree_serverPath.getSelection()) {
						if (ti.getData() instanceof FileModel) {
							FileModel fm = (FileModel) ti.getData();
							// 限制只能下载文件
							if (fm.isFile()) {
								mntm_downloadFile.setEnabled(true);
								return;
							}
						}
					}
				}
				mntm_downloadFile.setEnabled(false);
			}
		});
		tree_serverPath.setMenu(menu);

		mntm_downloadFile = new MenuItem(menu, SWT.NONE);
		mntm_downloadFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String downloadPath = "";
				for (TreeItem ti : tree_serverPath.getSelection()) {
					if (ti.getData() instanceof FileModel) {
						FileModel f = (FileModel) ti.getData();
						downloadPath = f.getPath();
						FileDialog fileDialog = new FileDialog(getShell());
						fileDialog.setFilterPath(Tool.getDesktopPath());
						fileDialog.setText("保存文件");
						fileDialog.setFileName(f.getName());
						String saveFilePath = fileDialog.open();
						boolean go = true;
						if (saveFilePath != null) {
							File existsFile = new File(saveFilePath);
							if (existsFile.exists()) {
								if (MessageBoxUc.OpenConfirm(shell, f.getName()
										+ " 文件已存在，是否要覆盖文件？")) {
									go = true;
								} else {
									go = false;
								}
							}
							if (go) {
								DownloadFile downloadFile = new DownloadFile(
										getShell(), downloadPath, saveFilePath,
										server);
								downloadFile.open();
							}
						}
						break;
					}
				}

			}
		});
		mntm_downloadFile.setImage(SWTResourceManager.getImage(
				ManageServerFile.class, "/download_file.png"));
		mntm_downloadFile.setText("\u4E0B\u8F7D\u6587\u4EF6(&L)");
		//删除文件
		MenuItem mntm_deleteFile = new MenuItem(menu, SWT.NONE);
		mntm_deleteFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tree_serverPath.getSelection().length>0){
					if(MessageBoxUc.OpenConfirm(getShell(), "是否要删除选中的文件/文件夹？")){
						final List<FileModel> fileList = new ArrayList<FileModel>();
						for (TreeItem ti : tree_serverPath.getSelection()) {
							if (ti.getData() instanceof FileModel) {
								FileModel fm=(FileModel)ti.getData();
								fileList.add(fm);
							}
						}
						if(fileList.size()>0){
							//删除服务器文件
							ResultModel result=deleteServerFile(fileList);
							if(result.getCode()<0){
								MessageBoxUc.OpenError(getShell(), "删除文件失败，原因: "+result.getMsg());
							}else{
								//删除文件成功后，刷新一下
								getPath(text_selectPath.getText());
							}
						}
					}
				}
			}
		});
		mntm_deleteFile.setImage(SWTResourceManager.getImage(ManageServerFile.class, "/clear.png"));
		mntm_deleteFile.setText("\u5220\u9664\u6587\u4EF6(&D)");
	
		new MenuItem(menu, SWT.SEPARATOR);
		//刷新
		MenuItem mntm_refresh = new MenuItem(menu, SWT.NONE);
		mntm_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPath(text_selectPath.getText());
			}
		});
		mntm_refresh.setText("\u5237\u65B0(&F5)");
		tree_serverPath.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				for (TreeItem item : tree_serverPath.getSelection()) {
					if (item.getData() instanceof FileModel) {
						FileModel fm = (FileModel) item.getData();
						getPath(fm.getPath());
					} else {
						getPath("");
					}
				}
			}
		});

		text_selectPath = new Text(container, SWT.BORDER);
		text_selectPath
				.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		text_selectPath.setBounds(70, 2, 624, 23);
		text_selectPath
				.setToolTipText("\u5F53\u524D\u670D\u52A1\u5668\u76EE\u5F55");
		text_selectPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 13) {
					getPath(text_selectPath.getText());
				}
			}
		});
		text_selectPath.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		
		CLabel label = new CLabel(container, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label.setBounds(6, 2, 60, 23);
		label.setText("\u5F53\u524D\u8DEF\u5F84");
		getPath(path);
		return area;
	}
	/**
	 * 删除服务器文件
	 * @param fileList
	 */
	private ResultModel deleteServerFile(List<FileModel> fileList){
		DeleteServerFileHandler deleteServerFileHandler=new DeleteServerFileHandler(this,server);
		return deleteServerFileHandler.delete(fileList);	
	}
	
	private void getPath(String path) {
		if(path==null){
			path="";
		}
		RemoteGetServerPath remote = new RemoteGetServerPath(this, server,
				path.replace("\\", "/"));

		List<FileModel> listFileModel = remote.getRemoteServerPath();
		if (listFileModel != null) {

			for (TreeItem item : tree_serverPath.getItems()) {
				item.dispose();
			}

			boolean goBackExists = false;
			TreeItem rootPath = null;
			TreeItem sonPath = null;
			for (FileModel fm : listFileModel) {
				if (fm.isDrive()) {
					sonPath = new TreeItem(tree_serverPath, SWT.NONE);
					sonPath.setImage(SWTResourceManager.getImage(
							ManageServerFile.class, "/drive.png"));
					sonPath.setText(fm.getName());
					sonPath.setData(fm);
				} else {

					if (!goBackExists) {
						// 返回上一级
						FileModel parentFileModel=new FileModel();
						File parent = new File(fm.getParentPath());//获取当前目录的上一级目录
						parentFileModel.setDirectory(true);
						parentFileModel.setFile(false);
						
						parentFileModel.setName(parent.getName());
						parentFileModel.setPath(parent.getParent()==null?"":parent.getParent());//传递“上一级目录”
						parentFileModel.setParentPath(parent.getParent()==null?"":parent.getParent());
						rootPath = new TreeItem(tree_serverPath, SWT.NONE);
						rootPath.setImage(SWTResourceManager.getImage(
								ManageServerFile.class, "/folder.png"));
						rootPath.setText(GO_BACK_STR);
						rootPath.setData(parentFileModel);
						goBackExists = true;
					}
					if (fm.getName() != null) {
						sonPath = new TreeItem(tree_serverPath, SWT.NONE);
						if (fm.isDirectory()) {
							sonPath.setImage(SWTResourceManager.getImage(
									ManageServerFile.class, "/folder.png"));
						} else {
							sonPath.setImage(SWTResourceManager.getImage(
									ManageServerFile.class, "/file.png"));
						}
						try {
							sonPath.setText(new String(fm.getName().getBytes("GBK"),"GBK"));
						} catch (UnsupportedEncodingException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						sonPath.setData(fm);
					}
					text_selectPath.setText(fm.getParentPath());
				}
			}
			text_selectPath.setSelection(text_selectPath.getText().length());
		}
	}

	/**
	 * 输出操作结果信息
	 * 
	 * @param server
	 * @param content
	 */
	public void showMessage(final ShowType type, final String content) {
		if(type==ShowType.ERROR){
			MessageBoxUc.OpenError(getShell(), content);
		}else{
			MessageBoxUc.OpenOk(getShell(), content);
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btns = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		btns.setImage(SWTResourceManager.getImage(ManageServerFile.class, "/tick.png"));
		btns.setText("\u9009\u62E9(&S)");
		Button btnc = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		btnc.setText("\u53D6\u6D88(&C)");
	}

	@Override
	protected void okPressed() {
		this.path = text_selectPath.getText();
		if (text_selectPath.isFocusControl()) {
			getPath(this.path);
		} else {
			GlobalConfig.disconnect();
			super.okPressed();
		}
	}

	/**
	 * 获取选中路径
	 * @return
	 */
	public String getSelectPath() {
		return path;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(703, 518);
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

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Dialog Title
		newShell.setText("远程服务器");

	}
}
