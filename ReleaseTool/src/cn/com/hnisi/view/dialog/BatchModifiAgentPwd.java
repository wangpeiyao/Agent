package cn.com.hnisi.view.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.component.MessageBoxUc;

/**
 * 批量修改本地应用的Agent密码
 * 
 * @author WenZhiFeng
 * 2018年3月15日
 */
public class BatchModifiAgentPwd extends Dialog {
	private Text txt_agentPassword;
	private Shell shell;
	private Object[] serverIds;
	SQLiteDatabase sql = SQLiteDatabase.getInstance();

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public BatchModifiAgentPwd(Shell parentShell, Object[] serverIds) {
		super(parentShell);
		this.shell = parentShell;
		this.serverIds = serverIds;
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

		txt_agentPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txt_agentPassword.setBounds(136, 26, 197, 23);

		CLabel lblNewLabel = new CLabel(container, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setImage(SWTResourceManager.getImage(
				BatchModifiAgentPwd.class, "/lock.png"));
		lblNewLabel.setBounds(3, 26, 127, 23);
		lblNewLabel.setText("\u5E94\u7528\u7684Agent\u5BC6\u7801");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnm = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		btnm.setText("\u4FEE\u6539(&M)");
		Button button_1 = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button_1.setText("\u53D6\u6D88(&C)");
	}

	@Override
	protected void okPressed() {
		if (serverIds != null) {
			try {
				sql.openDatabase();
				for (Object id : serverIds) {
					String sqlStr = "update servers set agentPassword='"
							+ Tool.EncoderByMd5(txt_agentPassword.getText())
							+ "' where id = '" + id + "'";

					sql.executeUpdate(sqlStr);
				}
				MessageBoxUc.OpenOk(shell, "批量修改成功!");
			} catch (Exception e) {
				MessageDialog.openInformation(shell, "操作提示",
						"修改Agent密码时发生错误!\n" + e.getMessage());
				return;
			} finally {
				sql.close();
			}
			
		}
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(351, 141);
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
		newShell.setText("批量修改应用的Agent密码");

	}
}
