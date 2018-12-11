package cn.com.hnisi.view.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.common.GlobalConfig;

/**
 * 帮助-关于我们，弹出窗口。
 * @author FengGeGe
 *
 */
public class AboutUs extends TitleAreaDialog {

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AboutUs(Shell parentShell) {
		super(parentShell);
		setBlockOnOpen(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("版权所有：广州华资软件技术有限公司(www.sinobest.cn)");
		setTitle("\u670D\u52A1\u5668\u4E2D\u95F4\u4EF6\u5E94\u7528\u7A0B\u5E8F\u53D1\u5E03\u7BA1\u7406\u5DE5\u5177");
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(149, 48, 212, 17);
		lblNewLabel.setText("\u7248\u672C\u53F7: "+GlobalConfig.Version());
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setBounds(149, 22, 212, 17);
		lblNewLabel_1.setText("\u4F5C\u8005: \u6E29\u5FD7\u950B  QQ:522134398");
		
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setBounds(149, 74, 212, 17);
		lblNewLabel_2.setText("JDK\u7248\u672C\u8981\u6C42: 1.6\u53CA\u4EE5\u4E0A");
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(1, 125, 453, 2);
		
		Label lblNewLabel_3 = new Label(container, SWT.NONE);
		lblNewLabel_3.setImage(SWTResourceManager.getImage(AboutUs.class, "/Dropbox.png"));
		lblNewLabel_3.setBounds(5, 5, 119, 103);

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(459, 286);
	}
	
	@Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        // Dialog Title
        newShell.setText("关于我们");
    }
}
