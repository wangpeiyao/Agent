package cn.com.hnisi.view.dialog;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import cn.com.hnisi.model.UserModel;
import cn.com.hnisi.util.Tool;

/**
 * 登录界面
 * @author FengGeGe
 *
 */
public class Login extends TitleAreaDialog {
	private Text txt_userName;
	private Text txt_passWord;
	private Shell shell;
	private boolean isLockScreen=false;//如果是锁屏，则不能取消，必需给登录。
	static Logger log=Logger.getLogger(Login.class);
	private String shellTitle="安全登录";
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public Login(Shell parentShell) {
		super(parentShell);
		this.shell=parentShell;

		log.info("正在登录...");
	}
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public Login(Shell parentShell,boolean isLockScreen) {
		super(parentShell);
		setHelpAvailable(false);
		if(isLockScreen){
			//SWT.APPLICATION_MODAL 模态窗口，不可对此对话框以外的对话框进行操作。
			setShellStyle(SWT.TITLE|SWT.APPLICATION_MODAL );
		}
		this.shell=parentShell;
		this.isLockScreen=isLockScreen;
		log.info("已锁屏，需要重新登录");
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setMessage("\u6B22\u8FCE\u4F7F\u7528\u672C\u5DE5\u5177\uFF0C\u4F7F\u7528\u524D\u9700\u8981\u5148\u767B\u5F55.");
		setTitle("用户登录");
		Composite area = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(area, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		CLabel lblNewLabel = new CLabel(container, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		lblNewLabel.setBounds(74, 20, 67, 23);
		lblNewLabel.setText("\u8D26  \u53F7(&U)");
		
		CLabel lblNewLabel_1 = new CLabel(container, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		lblNewLabel_1.setBounds(74, 63, 67, 23);
		lblNewLabel_1.setText("\u5BC6  \u7801(&P)");
		
		txt_userName = new Text(container, SWT.BORDER);
		txt_userName.setText("admin");
		txt_userName.showSelection();
		txt_userName.setBounds(147, 20, 183, 23);
		
		txt_passWord = new Text(container, SWT.BORDER | SWT.PASSWORD);
		txt_passWord.setBounds(147, 63, 183, 23);
		txt_passWord.setToolTipText("初始密码为“admin”，登录后请尽快修改。");
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 134, 412, 2);
		
		Label lblNewLabel_2 = new Label(container, SWT.NONE);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		lblNewLabel_2.setImage(SWTResourceManager.getImage(Login.class, "/user.png"));
		lblNewLabel_2.setBounds(20, 20, 67, 72);
		
		txt_passWord.setFocus();
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		button.setText("登录(&L)");
		if(!isLockScreen){
		Button button_1 = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button_1.setText("取消(&C)");
		}
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(406, 257);
	}
	
	@Override
	protected void okPressed(){
		if(txt_userName.getText().trim().length()<=0){
			MessageDialog.openInformation(shell, "登录提示", "用户名不能为空！");
			txt_userName.setFocus();
			return;
		}
		
		if(txt_passWord.getText().trim().length()<=0){
			MessageDialog.openInformation(shell, "登录提示", "密码不能为空！");
			txt_passWord.setFocus();
			return;
		}
		ResultSet rs=null;
		SQLiteDatabase sql=SQLiteDatabase.getInstance();
		try{
			String sqlStr="select * from users where username='"
					+sql.TransactSQLInjection(txt_userName.getText())+
					"' and password='"+Tool.EncoderByMd5(txt_passWord.getText())+"';";
			sql.openDatabase();

			rs = sql.executeQuery(sqlStr);
			
			if(rs.next()){
				UserModel user=new UserModel();
				user.setId(rs.getString("id"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setName(rs.getString("name"));
				user.setRole(rs.getString("role"));
				user.setStatus(rs.getString("status"));
				user.setRemark(rs.getString("remark"));
				GlobalConfig.setUser(user);
				log.info("登录成功，系统正在运行中...");
			}else{
				MessageDialog.openError(shell, "登录提示", "用户或密码错误！");
				txt_passWord.setFocus();
				txt_passWord.selectAll();
				return;
			}

		}catch(Exception ex){
			ex.printStackTrace();
			log.error(ex.getMessage());
			MessageDialog.openInformation(shell, "登录失败", "登录出错，原因："+ex.getMessage());
			return;
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				
			}
			sql.close();
		}
		
		super.okPressed();
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
		newShell.setImage(SWTResourceManager.getImage(Login.class, "/lock.png"));
        super.configureShell(newShell);
        // Dialog Title
        newShell.setText(shellTitle);
        newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
    }
	/**
	 * 设置窗口标题
	 */
	public void setShellTitle(String shellTitle) {
		this.shellTitle = shellTitle;
	}
}
