package cn.com.hnisi.view.dialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.common.GlobalConfig;
import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.util.Tool;

/**
 * 修改密码
 * 
 * @author FengGeGe
 * 
 */
public class ModfiPassWord extends TitleAreaDialog {
	private Text txt_oldPassword;
	private Text txt_newPassword;
	private Text txt_confimPassword;
	private Shell shell;
	static Logger log=Logger.getLogger(ModfiPassWord.class);
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ModfiPassWord(Shell parentShell) {
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
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		setMessage("\u6210\u529F\u4FEE\u6539\u540E\uFF0C\u5C06\u5728\u4E0B\u6B21\u767B\u5F55\u751F\u6548.");
		setTitle("\u4FEE\u6539\u5DE5\u5177\u767B\u5F55\u5BC6\u7801");
		Composite area = (Composite) super.createDialogArea(parent);
		area.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Composite container = new Composite(area, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(45, 26, 61, 17);
		lblNewLabel.setText("\u65E7\u5BC6\u7801");

		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setBounds(45, 65, 61, 17);
		lblNewLabel_1.setText("\u65B0\u5BC6\u7801");

		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setBounds(45, 103, 61, 17);
		lblNewLabel_2.setText("\u786E\u8BA4\u5BC6\u7801");

		txt_oldPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txt_oldPassword.setBounds(112, 23, 217, 23);

		txt_newPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txt_newPassword.setBounds(112, 62, 217, 23);

		txt_confimPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txt_confimPassword.setBounds(112, 100, 217, 23);

		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(1, 141, 399, 2);

		return area;
	}

	@Override
	protected void okPressed() {
		
		if (GlobalConfig.getUser().equals("未登录")) {
			MessageDialog.openError(shell, "提示", "请先登录系统");
			return;
		} else {
			if(txt_oldPassword.getText().trim().length()<=0){
				MessageDialog.openInformation(shell, "提示", "请输入旧密码！");
				return;
			}
			if(txt_oldPassword.getText().trim().length()<=0){
				MessageDialog.openInformation(shell, "提示", "请输入旧密码！");
				txt_oldPassword.setFocus();
				return;
			}
			if(txt_newPassword.getText().trim().length()<=0){
				MessageDialog.openInformation(shell, "提示", "请输入新密码！");
				txt_newPassword.setFocus();
				return;
			}
			if(txt_confimPassword.getText().trim().length()<=0){
				MessageDialog.openInformation(shell, "提示", "请输入确认密码！");
				txt_confimPassword.setFocus();
				return;
			}
			
			if(!txt_confimPassword.getText().equals(txt_newPassword.getText())){
				MessageDialog.openInformation(shell, "提示", "新密码和确认密码不一致！");
				txt_confimPassword.setFocus();
				return;
			}
			SQLiteDatabase sql = SQLiteDatabase.getInstance();
			sql.openDatabase();
			try {
				//判断录入的旧密码跟登录用户的密码是否一致
				if (Tool.EncoderByMd5(txt_oldPassword.getText()).equals(
						GlobalConfig.getUser().getPassword())) {
					if (txt_newPassword.getText().equals(
							txt_confimPassword.getText())) {
						sql.executeUpdate("update users set password='"
								+ Tool.EncoderByMd5(txt_newPassword.getText())
								+ "' where id='" + GlobalConfig.getUser().getId()
								+ "'");
					}
					MessageDialog.openInformation(shell, "操作提示", "修改成功！");
				} else {
					MessageDialog.openInformation(shell, "操作提示", "旧密码录入错误!");
					return;
				}
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
				log.error("修改密码时出错，原因："+e.getMessage());
			}
		}
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		Button btnm = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		btnm.setText("\u4FDD\u5B58(&S)");
		btnm.setImage(SWTResourceManager.getImage(ModfiPassWord.class, "/javax/swing/plaf/metal/icons/ocean/floppy.gif"));
		Button button = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button.setText("\u53D6\u6D88(&C)");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(405, 300);
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
        super.configureShell(newShell);
        // Dialog Title
        newShell.setText("修改密码");
    }
}
