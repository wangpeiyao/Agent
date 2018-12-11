package cn.com.hnisi.view.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.view.component.MessageBoxUc;

public class CreateFolder extends Dialog {
	private Shell shell;
	private Text txt_folder;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public CreateFolder(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);

		CLabel lblNewLabel = new CLabel(container, SWT.NONE);
		lblNewLabel.setBounds(3, 21, 95, 23);
		lblNewLabel.setText("\u6587\u4EF6\u5939\u76EE\u5F55\u7ED3\u6784");

		txt_folder = new Text(container, SWT.BORDER);
		txt_folder.setToolTipText("\u4F8B\uFF1Aweb\\jsp\\test");
		txt_folder.setBounds(104, 21, 552, 23);

		Button btn_createFolder = new Button(container, SWT.NONE);
		btn_createFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (txt_folder.getText().isEmpty()) {
					MessageBoxUc.OpenOk(getShell(), "请先录入文件夹目录结构");
					txt_folder.setFocus();
					return;
				}
				createFolder(txt_folder.getText());
			}
		});
		btn_createFolder.setImage(SWTResourceManager.getImage(CreateFolder.class, "/folder_add.png"));
		btn_createFolder.setBounds(668, 20, 80, 27);
		btn_createFolder.setText("\u521B \u5EFA");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {

	}

	/**
	 * 选择需要发布的文件夹路径
	 */
	private void createFolder(String folderPath) {
		DirectoryDialog dirDialog = new DirectoryDialog(getShell());
		dirDialog.setText("浏览文件夹");
		dirDialog.setMessage("请选择需要创建文件夹的位置");
		// 获取上一次打开的目录
		dirDialog.setFilterPath(GlobalConfig.historySelectFolder);
		dirDialog.open();
		// 记录本次打开的目录
		
		String parentPath = dirDialog.getFilterPath();
		if(!parentPath.isEmpty()){
			File newFolder = new File(parentPath + File.separator + folderPath);
			if (!newFolder.exists()) {
				if(newFolder.mkdirs()){
					MessageBoxUc.OpenOk(getShell(), "创建成功!");
				}else{
					MessageBoxUc.OpenError(getShell(), "创建失败，文件夹不能包含特殊字符!");
					return;
				}
			} else {
				MessageBoxUc.OpenError(getShell(),  "文件夹已存在!!!");
			}
	
			try {
				Desktop.getDesktop().open(newFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.close();
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(778, 106);
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
		newShell.setImage(SWTResourceManager.getImage(CreateFolder.class, "/folder_add.png"));
		super.configureShell(newShell);
		// Dialog Title
		newShell.setText("创建文件夹");

	}
}
