package cn.com.hnisi.view.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import cn.com.hnisi.model.ConsoleAccountModel;
import cn.com.hnisi.view.component.MessageBoxUc;
import org.eclipse.wb.swt.SWTResourceManager;

public class MiddlewareConsoleUser extends TitleAreaDialog {
	private Text txt_username;
	private Text txt_password;
	private Shell shell;
	private ConsoleAccountModel consoleAccount = new ConsoleAccountModel();

	private String username;
	private String password;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public MiddlewareConsoleUser(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
		this.shell = parentShell;
	}
	public MiddlewareConsoleUser(Shell parentShell,String username,String password) {
		super(parentShell);
		setHelpAvailable(false);
		this.shell = parentShell;
		this.username=username;
		this.password=password;
	}
	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("\u4E2D\u95F4\u4EF6\u7BA1\u7406\u7528\u6237\u4FE1\u606F");
		setTitle("\u914D\u7F6E\u63A7\u5236\u53F0\u7BA1\u7406\u8D26\u53F7");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		CLabel label = new CLabel(container, SWT.NONE);
		label.setBounds(26, 10, 67, 23);
		label.setText("\u7BA1\u7406\u8D26\u53F7");

		CLabel label_1 = new CLabel(container, SWT.NONE);
		label_1.setBounds(26, 52, 67, 23);
		label_1.setText("\u7BA1\u7406\u5BC6\u7801");

		txt_username = new Text(container, SWT.BORDER);
		txt_username.setBounds(99, 10, 192, 23);
		if (username != null) {
			txt_username.setText(username);
		}
		txt_password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txt_password.setBounds(99, 52, 192, 23);
		if (password != null) {
			txt_password.setText(password);
		}
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button_1 = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		button_1.setImage(SWTResourceManager.getImage(MiddlewareConsoleUser.class, "/tick.png"));
		button_1.setText("\u786E\u5B9A(&Y)");
		Button btnc = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		btnc.setText("\u5173\u95ED(&C)");
	}

	@Override
	protected void okPressed() {
		if (txt_username.getText().trim().length() > 0) {
			if (txt_password.getText().trim().length() < 0) {
				MessageBoxUc.OpenError(getShell(), "请录入管理密码!");
			} else {
				consoleAccount = new ConsoleAccountModel(txt_username.getText(),
						txt_password.getText());
				super.okPressed();
			}
		} else {
			MessageBoxUc.OpenError(getShell(), "请录入管理用户名!");
		}
	}

	/**
	 * 返回管理账号对象实例
	 * 
	 * @return
	 */
	public ConsoleAccountModel getConsoleAccount() {
		return consoleAccount;
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(349, 251);
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
		newShell.setText("控制台-账号管理");

	}
}
