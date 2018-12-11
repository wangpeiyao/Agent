package cn.com.hnisi.view.dialog;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.util.Tool;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 应用组新增、编辑界面
 * @author FengGeGe
 *
 */
public class GroupAdd extends Dialog {
	private Text txt_serverGroupName;
	private StyledText txt_remark;
	private Shell shell;
	SQLiteDatabase sql = SQLiteDatabase.getInstance();
	static Logger log=Logger.getLogger(GroupAdd.class);
	/**
	 * 应用群的数据库记录ID
	 */
	private String groupId = null;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public GroupAdd(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public GroupAdd(Shell parentShell, String groupId) {
		super(parentShell);
		this.shell = parentShell;
		this.groupId = groupId;
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
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(20, 17, 75, 23);
		lblNewLabel.setText("*\u5206\u7EC4\u540D\u79F0");

		txt_serverGroupName = new Text(container, SWT.BORDER);
		txt_serverGroupName.setBounds(99, 17, 270, 23);

		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 174, 391, 2);

		CLabel lblNewLabel_1 = new CLabel(container, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setBounds(20, 51, 75, 23);
		lblNewLabel_1.setText(" \u5907\u6CE8\u8BF4\u660E");

		txt_remark = new StyledText(container, SWT.BORDER | SWT.V_SCROLL);
		txt_remark.setWordWrap(true);
		txt_remark.setBounds(99, 52, 270, 108);

		if (groupId != null) {
			ResultSet rs = null;
			try {
				sql.openDatabase();
				rs = sql.executeQuery("select * from groups where id='" + groupId+ "'");
				while (rs.next()) {
					txt_serverGroupName.setText(rs.getString("name"));
					txt_remark.setText(rs.getString("remark"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("加载应用组数据时出错，原因："+e.getMessage());
				MessageDialog.openError(getShell(), "错误提示", e.getMessage());
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					sql.close();
				} catch (SQLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.IGNORE_LABEL, true);
		button.setImage(SWTResourceManager.getImage(GroupAdd.class, "/save.png"));
		button.setText("\u4FDD\u5B58(&S)");
		Button btnc = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		btnc.setText("\u53D6\u6D88(&C)");
	}

	/**
	 * 点击“保存”时执行操作
	 */
	protected void okPressed() {
		try {
			sql.openDatabase();
			if(txt_serverGroupName.getText().replace(" ", "").equals("默认")){
				MessageDialog.openInformation(shell, "操作提示", "应用群名称不能为“默认”，请使用别的名称!");
				txt_serverGroupName.setText("");
				txt_serverGroupName.setFocus();
				return;
			}
			// groupId不为空则根据groupId更新记录。
			if (groupId != null) {
				sql.executeUpdate(("update groups set name='" + sql.TransactSQLInjection(txt_serverGroupName.getText()) + "',"
						+ " remark='"+sql.TransactSQLInjection(txt_remark.getText())+"' "
						+ " where id='"+groupId+"'"));
			} else {
				// 新增记录
				if (txt_serverGroupName.getText().trim().length() > 0) {
					sql.executeUpdate("insert into groups(id,name,status,canDelete,canEdit,canShow,remark) values ('"
							+ Tool.getUUID() + "','"
							+ sql.TransactSQLInjection(txt_serverGroupName.getText()) + "','1','1','1','1','"
							+ sql.TransactSQLInjection(txt_remark.getText()) + "');");

				} else {
					MessageDialog.openInformation(shell, "操作提示", "应用群名称不能为空！");
					txt_serverGroupName.setFocus();
					return;
				}
			}
		} catch (Exception e) {
			log.error("保存应用组数据时出错，原因："+e.getMessage());
			MessageDialog.openWarning(shell, "错误提示",
					"程序发生异常，原因：" + e.getMessage());
			txt_serverGroupName.setFocus();
		} finally {
			sql.close();
		}
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(397, 263);
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
		newShell.setImage(SWTResourceManager.getImage(GroupAdd.class, "/chart_organisation.png"));
        super.configureShell(newShell);
        // Dialog Title
        newShell.setText("管理分组类别");

    }
}
