package cn.com.hnisi.view.dialog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.view.component.MessageBoxUc;
import org.eclipse.swt.widgets.Label;

public class Group extends TitleAreaDialog {
	static Logger log = Logger.getLogger(TitleAreaDialog.class);
	
	private Shell shell;
	private Table table_serverGroup;
	SQLiteDatabase sql = SQLiteDatabase.getInstance();
	private CheckboxTableViewer checkboxTableViewer_serverGroup;
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public Group(Shell parentShell) {
		super(parentShell);
		this.shell=parentShell;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("\u5E94\u7528\u5206\u7EC4\uFF0C\u65B9\u4FBF\u7BA1\u7406\u4E0D\u540C\u7684\u5E94\u7528\u3002");
		setTitle("\u7BA1\u7406\u5206\u7EC4");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FormLayout());
		
		checkboxTableViewer_serverGroup = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table_serverGroup = checkboxTableViewer_serverGroup.getTable();
		table_serverGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (table_serverGroup.getItemCount() > 0
						&& table_serverGroup.getSelectionIndex() >= 0) {
					boolean checkState = table_serverGroup.getItem(
							table_serverGroup.getSelectionIndex()).getChecked();
					table_serverGroup.getItem(
							table_serverGroup.getSelectionIndex()).setChecked(
							!checkState);
				}
			}
		});
		table_serverGroup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = table_serverGroup.getSelectionIndex();
				// 防止getItem(x)时超出索引范围报错
				if (index >= 0 && index <= table_serverGroup.getItemCount()) {
					String id = table_serverGroup.getItem(index).getData()
							.toString();
					GroupAdd sg = new GroupAdd(getShell(), id);
					sg.open();
					// 编辑完之后，刷新应用列表
					loadServerGroupDataFilterDefault();
				}
			}
		});
		table_serverGroup.setHeaderVisible(true);
		FormData fd_table_serverGroup = new FormData();
		fd_table_serverGroup.top = new FormAttachment(0);
		fd_table_serverGroup.right = new FormAttachment(100);
		fd_table_serverGroup.left = new FormAttachment(0);
		table_serverGroup.setLayoutData(fd_table_serverGroup);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(checkboxTableViewer_serverGroup, SWT.NONE);
		TableColumn tbc_serverGroupId = tableViewerColumn.getColumn();
		tbc_serverGroupId.setWidth(60);
		tbc_serverGroupId.setText("\u9009\u62E9");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(checkboxTableViewer_serverGroup, SWT.NONE);
		TableColumn tbc_serverGroupName = tableViewerColumn_1.getColumn();
		tbc_serverGroupName.setWidth(230);
		tbc_serverGroupName.setText("\u5E94\u7528\u7FA4\u540D\u79F0");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(checkboxTableViewer_serverGroup, SWT.NONE);
		TableColumn tbc_serverGroupRemark = tableViewerColumn_2.getColumn();
		tbc_serverGroupRemark.setWidth(200);
		tbc_serverGroupRemark.setText("\u5907\u6CE8\u8BF4\u660E");
		
		Button btn_addGroup = new Button(composite, SWT.NONE);
		fd_table_serverGroup.bottom = new FormAttachment(100, -37);
		btn_addGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				GroupAdd sg = new GroupAdd(shell);
				// 如果点击了“保存”，则刷新列表
				if (sg.open() == 0) {
					loadServerGroupDataFilterDefault();
				}
			}
		});
		btn_addGroup.setText("\u65B0\u589E(&N)");
		btn_addGroup.setImage(SWTResourceManager.getImage(Group.class, "/chart_organisation_add.png"));
		FormData fd_btn_addGroup = new FormData();
		fd_btn_addGroup.top = new FormAttachment(table_serverGroup, 3);
		fd_btn_addGroup.left = new FormAttachment(0, 6);
		fd_btn_addGroup.bottom = new FormAttachment(100, -7);
		fd_btn_addGroup.right = new FormAttachment(0, 89);
		btn_addGroup.setLayoutData(fd_btn_addGroup);
		
		Button btn_editGroup = new Button(composite, SWT.NONE);
		btn_editGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 获取所有checkbox被选中的列 返回一个Object数组
				Object[] objects = checkboxTableViewer_serverGroup
						.getCheckedElements();
				if (objects != null && objects.length > 1) {
					MessageDialog.openInformation(shell, "操作提示",
							"不能同时编辑多条，只能选中一条记录进行编辑！");
				} else if (objects != null && objects.length == 1) {
					String id = objects[0].toString();// 获取到group的Id
					GroupAdd sg = new GroupAdd(shell, id);
					// 如果点击了“保存”，则刷新列表
					if (sg.open() == 0) {
						loadServerGroupDataFilterDefault();
					}

				} else {
					MessageDialog
							.openInformation(shell, "操作提示", "请先选中记录再进行编辑！");
					e.doit = false;
				}
			}
		});
		btn_editGroup.setText("\u7F16\u8F91(&E)");
		btn_editGroup.setImage(SWTResourceManager.getImage(Group.class, "/edit.png"));
		FormData fd_btn_editGroup = new FormData();
		fd_btn_editGroup.top = new FormAttachment(table_serverGroup, 3);
		fd_btn_editGroup.left = new FormAttachment(0, 95);
		fd_btn_editGroup.right = new FormAttachment(0, 178);
		btn_editGroup.setLayoutData(fd_btn_editGroup);
		
		Button btn_deleteGroup = new Button(composite, SWT.NONE);
		btn_deleteGroup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteServerGroup();// 删除选中记录
				loadServerGroupDataFilterDefault();// 删除完，刷新列表
			}
		});
		btn_deleteGroup.setText("\u5220\u9664(&D)");
		btn_deleteGroup.setImage(SWTResourceManager.getImage(Group.class, "/delete.png"));
		FormData fd_btn_deleteGroup = new FormData();
		fd_btn_deleteGroup.top = new FormAttachment(table_serverGroup, 3);
		fd_btn_deleteGroup.left = new FormAttachment(0, 185);
		fd_btn_deleteGroup.right = new FormAttachment(0, 268);
		btn_deleteGroup.setLayoutData(fd_btn_deleteGroup);
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(btn_addGroup, 4);
		fd_label.bottom = new FormAttachment(100, -1);
		fd_label.left = new FormAttachment(0);
		fd_label.right = new FormAttachment(100);
		label.setLayoutData(fd_label);
		loadServerGroupDataFilterDefault();
		return area;
	}
	/**
	 * 获取数据库中的server_group所有记录，排除“默认”。
	 */
	private void loadServerGroupDataFilterDefault() {
		ResultSet rs = null;
		try {
			table_serverGroup.removeAll();
			sql.openDatabase();
			rs = sql.executeQuery("select * from groups where canDelete='1' and canEdit='1'");
			while (rs.next()) {
				TableItem record = new TableItem(table_serverGroup, SWT.NONE);
				record.setText(new String[] { rs.getString("id"),
						rs.getString("name"), rs.getString("remark") });
				record.setText("");
				record.setData(rs.getString("id"));// 存储主键id
			}

		} catch (Exception e) {
			log.error("获取应用组出错，原因：" + e.getMessage());
			MessageBoxUc.OpenError(shell,
					"获取应用组出错，原因：" + e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				sql.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void deleteServerGroup() {

		Object[] obj = checkboxTableViewer_serverGroup.getCheckedElements();
		if (obj != null && obj.length > 0) {
			if (MessageBoxUc.OpenConfirm(shell,"如果应用群下面已登记有应用服务，将会被移到“默认”组下。\n是否要删除选中的" + obj.length
							+ "记录？")) {
				List<String> sqls = new ArrayList<String>();
				ResultSet rs = null;
				try {
					sql.openDatabase();
					rs = sql.executeQuery("select id from groups where name='默认'");
					String defaultId = null;
					while (rs.next()) {
						defaultId = rs.getString("id");
					}
					for (Object object : obj) {
						sqls.add("delete from groups where id ='"
								+ object.toString() + "';");
						// 把被删除应用组下面登记的应用记录，更新“默认”组下面。
						if (defaultId != null && !defaultId.equals("")) {
							sqls.add("update servers set groupId='" + defaultId
									+ "' where groupId='" + object.toString()
									+ "';");
						}
					}
					sql.executeUpdate(sqls);
				} catch (Exception e) {
					log.error("删除出错，原因：" + e.getMessage());
					MessageDialog.openError(shell, "错误提示",
							"删除出错，原因：" + e.getMessage());
				} finally {
					if (rs != null) {
						try {
							rs.close();
							sql.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}

				}
			}
		} else {
			MessageDialog.openInformation(shell, "操作确认", "请先选中需要删除的记录！");
		}

	}
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button.setText("\u5173\u95ED(&C)");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(517, 507);
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
		newShell.setImage(SWTResourceManager.getImage(Group.class, "/chart_organisation.png"));
        super.configureShell(newShell);
        // Dialog Title
        newShell.setText("管理分组");

    }
}
