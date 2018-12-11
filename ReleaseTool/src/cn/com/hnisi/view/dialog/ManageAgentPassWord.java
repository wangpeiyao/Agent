package cn.com.hnisi.view.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.agent.services.function.ChangeAgentPassword;
import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.util.TableViewThread;
import cn.com.hnisi.util.Tool;

/**
 * 修改服务端Agent密码
 * 
 * @author WenZhiFeng 2018年1月24日
 */
public class ManageAgentPassWord extends TitleAreaDialog {
	private Shell shell;
	private Text txt_oldPwd;
	private Text txt_newPwd;
	private Text txt_confimPwd;
	private Text txt_ip;
	private Text txt_port;
	private String ip = "";
	private String agentPort = "";
	private String oldPassword, newPassword;
	SQLiteDatabase sql = SQLiteDatabase.getInstance();
	private Table table_serverList;
	private Text txt_search;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public ManageAgentPassWord(Shell parentShell, String ip, String agentPort) {
		super(parentShell);
		setShellStyle(SWT.MAX);
		setHelpAvailable(false);
		this.shell = parentShell;
		this.ip = ip;
		this.agentPort = agentPort;
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setTitleImage(null);
		setMessage("\u8BBE\u7F6E\u670D\u52A1\u5668\u4E0A\u7684Agent\u8BBF\u95EE\u5BC6\u7801\uFF0C\u9632\u6B62\u4ED6\u4EBA\u5BF9Agent\u8FDB\u884C\u8BBF\u95EE\u64CD\u4F5C\u3002");
		setTitle("Agent\u5B89\u5168\u8BBE\u7F6E");
		Composite area = (Composite) super.createDialogArea(parent);
		area.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(2, 319, 744, 2);

		Group group = new Group(container, SWT.NONE);
		group.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		group.setText("\u4FEE\u6539\u5BC6\u7801");
		group.setBounds(294, 10, 299, 303);

		CLabel lblNewLabel = new CLabel(group, SWT.NONE);
		lblNewLabel.setBounds(9, 94, 87, 23);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setText("\u65E7\u5BC6\u7801");

		CLabel lblNewLabel_1 = new CLabel(group, SWT.NONE);
		lblNewLabel_1.setBounds(9, 146, 87, 23);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setText("\u65B0\u5BC6\u7801");

		CLabel lblNewLabel_2 = new CLabel(group, SWT.NONE);
		lblNewLabel_2.setBounds(9, 179, 87, 23);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setText("\u786E\u8BA4\u65B0\u5BC6\u7801");
		txt_ip = new Text(group, SWT.BORDER);
		txt_ip.setBounds(107, 29, 160, 23);

		txt_ip.setText(ip);

		txt_oldPwd = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txt_oldPwd.setBounds(107, 94, 160, 23);

		txt_newPwd = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txt_newPwd.setBounds(107, 146, 160, 23);

		txt_confimPwd = new Text(group, SWT.BORDER | SWT.PASSWORD);
		txt_confimPwd.setBounds(107, 179, 160, 23);

		CLabel lblNewLabel_3 = new CLabel(group, SWT.NONE);
		lblNewLabel_3.setBounds(9, 29, 87, 23);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setText("\u670D\u52A1\u5668IP\u5730\u5740");

		CLabel lblNewLabel_4 = new CLabel(group, SWT.NONE);
		lblNewLabel_4.setBounds(9, 63, 87, 23);
		lblNewLabel_4.setAlignment(SWT.RIGHT);
		lblNewLabel_4.setText("Agent\u7AEF\u53E3");

		txt_port = new Text(group, SWT.BORDER);
		txt_port.setBounds(107, 63, 73, 23);
		txt_port.setText("7788");
		txt_port.setText(agentPort);

		Label lblNewLabel_5 = new Label(group, SWT.NONE);
		lblNewLabel_5
				.setFont(SWTResourceManager.getFont("微软雅黑", 8, SWT.NORMAL));
		lblNewLabel_5.setBounds(107, 118, 125, 17);
		lblNewLabel_5.setForeground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		lblNewLabel_5
				.setText("\u5982\u672A\u8BBE\u7F6E\uFF0C\u4E3A\u7A7A\u5373\u53EF\u3002");

		Button cbn_updateServer = new Button(group, SWT.CHECK);
		cbn_updateServer.setBounds(107, 220, 170, 17);
		cbn_updateServer.setSelection(true);
		cbn_updateServer
				.setText("\u540C\u6B65\u5230\u672C\u5730\u5E94\u7528agent\u5BC6\u7801");

		Button btn_use = new Button(group, SWT.NONE);
		btn_use.setImage(SWTResourceManager.getImage(ManageAgentPassWord.class,
				"/tick.png"));
		btn_use.setBounds(107, 258, 87, 27);
		btn_use.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				useModification();
			}
		});
		btn_use.setText("\u5E94\u7528(S)");

		Group group_1 = new Group(container, SWT.NONE);
		group_1.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		group_1.setText("\u5DF2\u914D\u7F6E\u670D\u52A1\u5668\u5217\u8868");
		group_1.setBounds(10, 10, 278, 303);

		CLabel lblNewLabel_6 = new CLabel(group_1, SWT.NONE);
		lblNewLabel_6.setBounds(4, 29, 55, 22);
		lblNewLabel_6.setAlignment(SWT.CENTER);
		lblNewLabel_6.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		lblNewLabel_6.setImage(SWTResourceManager.getImage(
				ManageAgentPassWord.class, "/search.png"));
		lblNewLabel_6.setText("\u641C\u7D22");

		txt_search = new Text(group_1, SWT.BORDER);
		txt_search.setBounds(66, 29, 208, 23);

		TableViewer tableViewer = new TableViewer(group_1, SWT.BORDER
				| SWT.FULL_SELECTION);
		table_serverList = tableViewer.getTable();
		table_serverList.setBounds(4, 57, 270, 242);
		table_serverList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 1) {
					int index = table_serverList.getSelectionIndex();
					// 防止getItem(x)时超出索引范围报错
					if (index >= 0 && index <= table_serverList.getItemCount()) {
						TableItem item = table_serverList.getItem(index);
						txt_ip.setText(item.getText(1));
						txt_port.setText(item.getText(2));
						txt_oldPwd.setText("");
						txt_newPwd.setText("");
						txt_confimPwd.setText("");
					}
				}
			}
		});
		table_serverList.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn col_seq = tableViewerColumn.getColumn();
		col_seq.setText("\u5E8F\u53F7");
		col_seq.setWidth(50);

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn col_serverIp = tableViewerColumn_1.getColumn();
		col_serverIp.setWidth(110);
		col_serverIp.setText("\u670D\u52A1\u5668IP");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn col_agentPort = tableViewerColumn_2.getColumn();
		col_agentPort.setWidth(80);
		col_agentPort.setText("Agent\u7AEF\u53E3");
		txt_search.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				loadServerList(txt_search.getText());
			}
		});

		loadServerList(null);
		return area;
	}

	private void loadServerList(String ip) {

		try {

			String sqlStr = "select \"seq\",serverIp,agentPort from servers ";
			if (ip != null) {
				sqlStr += " where serverIp like '%" + ip + "%' ";
			}
			sqlStr += " group by serverIp,agentPort";
			// 要显示的列
			TableViewThread tableViewThread = new TableViewThread(
					Display.getDefault(), sqlStr, table_serverList,
					new String[] { "seq", "serverIp", "agentPort" },
					"serverIp", false, false);
			Thread th = new Thread(tableViewThread);
			th.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnc = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		btnc.setText("\u5173\u95ED(&C)");
	}

	/**
	 * 应用修改
	 */
	protected void useModification() {

		if (txt_ip.getText().trim().length() <= 0) {
			MessageDialog.openInformation(shell, "操作提示", "“服务器地址”不能为空!");
			txt_ip.setFocus();
			return;
		} else {
			if (!Tool.checkIp(txt_ip.getText())) {
				MessageDialog.openInformation(shell, "操作提示", "请输入合法的IP地址格式!");
				txt_ip.setFocus();
				return;
			}
		}

		int port = 0;
		if (txt_port.getText().trim().length() <= 0) {
			MessageDialog.openInformation(shell, "操作提示", "“Agent端口号”不能为空!");
			txt_port.setFocus();
			return;
		} else {
			try {
				port = Integer.parseInt(txt_port.getText());
				if (port < 0 || port > 65535) {
					MessageDialog.openInformation(shell, "操作提示",
							"“Agent端口号”请输入一个1~65535的正整数，例：8888");
					txt_port.setFocus();
					return;
				}
			} catch (Exception e) {
				MessageDialog.openInformation(shell, "操作提示",
						"“Agent端口号”请输入一个1~65535的正整数，例：8888");
				txt_port.setFocus();
				return;
			}
		}

		if (txt_newPwd.getText().trim().length() <= 0) {
			MessageDialog.openInformation(shell, "操作提示", "“新密码”不能为空!");
			txt_newPwd.setFocus();
			return;
		}
		if (txt_confimPwd.getText().trim().length() <= 0) {
			MessageDialog.openInformation(shell, "操作提示", "“确认密码”不能为空!");
			txt_confimPwd.setFocus();
			return;
		}

		if (!txt_confimPwd.getText().equals(txt_newPwd.getText())) {
			MessageDialog.openInformation(shell, "操作提示", "“新密码”和“确认密码”不一致!");
			txt_confimPwd.setFocus();
			return;
		}
		ChangeAgentPassword changePwd = new ChangeAgentPassword(
				txt_ip.getText(), port);
		//进行MD5密码加密
		oldPassword = Tool.EncoderByMd5(txt_oldPwd.getText());
		newPassword = Tool.EncoderByMd5(txt_newPwd.getText());
		String result = changePwd.change(oldPassword, newPassword);
		MessageDialog.openInformation(getShell(), "提示", result);
		//同步到本地应用配置
		SQLiteDatabase sql = SQLiteDatabase.getInstance();
		sql.openDatabase();
		try {
			sql.executeUpdate("update servers set agentPassword='"
					+ newPassword + "' where serverIp='"
					+ txt_ip.getText() + "'");
		} catch (Exception e) {
			MessageDialog.openInformation(getShell(), "提示",
					"同步本地应用agent密码时出错，原因：" + e.getMessage());
		}
		// super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(611, 481);
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
		newShell.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		newShell.setImage(SWTResourceManager.getImage(
				ManageAgentPassWord.class, "/shield16.png"));
		super.configureShell(newShell);
		// Dialog Title
		newShell.setText("修改Agent访问密码");

	}
}
