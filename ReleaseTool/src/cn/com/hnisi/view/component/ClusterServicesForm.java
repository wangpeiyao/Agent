package cn.com.hnisi.view.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.model.ClusterServicesModel;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.util.NetWorkUtil;
import cn.com.hnisi.util.Tool;
import cn.com.hnisi.view.dialog.ManageServerFile;

/**
 * 集群录入界面
 * 
 * agent会校验集群管理应用config.xml中的server/name和server/listen-port(不为空)
 * 
 * @author FengGeGe
 * 
 */
public class ClusterServicesForm extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text txt_servicesName;
	private Text txt_adminUrl;
	private Text txt_stagePath;
	private Button cbx_updateFile;
	private CLabel lbl_number;
	private ServerModel server = null;
	private String clusterId = null;
	private String sid = "";
	private ClusterServicesModel clusterModel = new ClusterServicesModel();

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ClusterServicesForm(Composite parent, int style, ServerModel server) {
		super(parent, SWT.BORDER);
		this.server = server;
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(null);

		CLabel label = new CLabel(this, SWT.NONE);
		label.setBounds(36, 11, 89, 23);
		label.setAlignment(SWT.RIGHT);
		toolkit.adapt(label);
		toolkit.paintBordersFor(label);
		label.setText("\u670D\u52A1\u540D\u79F0");

		txt_servicesName = new Text(this, SWT.BORDER);
		txt_servicesName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				clusterModel.setName(txt_servicesName.getText());
			}
		});
		txt_servicesName.setBounds(132, 11, 199, 23);
		toolkit.adapt(txt_servicesName, true, true);

		CLabel label_1 = new CLabel(this, SWT.NONE);
		label_1.setBounds(355, 11, 59, 23);
		label_1.setAlignment(SWT.RIGHT);
		toolkit.adapt(label_1);
		toolkit.paintBordersFor(label_1);
		label_1.setText("\u7BA1\u7406\u5730\u5740");

		txt_adminUrl = new Text(this, SWT.BORDER);
		txt_adminUrl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				clusterModel.setAdminUrl(txt_adminUrl.getText());
			}
		});
		txt_adminUrl.setBounds(420, 11, 199, 23);
		txt_adminUrl.setText("http://");
		toolkit.adapt(txt_adminUrl, true, true);

		CLabel label_2 = new CLabel(this, SWT.NONE);
		label_2.setBounds(36, 44, 89, 23);
		label_2.setAlignment(SWT.RIGHT);
		toolkit.adapt(label_2);
		toolkit.paintBordersFor(label_2);
		label_2.setText("\u9879\u76EE\u5B58\u653E\u8DEF\u5F84");

		txt_stagePath = new Text(this, SWT.BORDER);
		txt_stagePath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				clusterModel.setStagePath(txt_stagePath.getText());
			}
		});
		txt_stagePath.setBounds(132, 44, 487, 23);
		txt_stagePath
				.setToolTipText("\u4F8B\uFF1AD:\\Oracle\\Middleware\\user_projects\\domains\\admindomain7001\\servers\\server7101\\stage\\web\\web");
		toolkit.adapt(txt_stagePath, true, true);

		cbx_updateFile = new Button(this, SWT.CHECK);
		cbx_updateFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterModel
						.setIsUploadFile(cbx_updateFile.getSelection() == true ? 1
								: 0);// 0-不同步；1-同步
			}
		});
		cbx_updateFile.setBounds(132, 81, 141, 17);
		cbx_updateFile.setSelection(true);
		toolkit.adapt(cbx_updateFile, true, true);
		cbx_updateFile.setText("\u540C\u6B65\u90E8\u7F72\u6587\u4EF6");

		lbl_number = new CLabel(this, SWT.NONE);
		lbl_number.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		lbl_number.setAlignment(SWT.CENTER);
		lbl_number.setBounds(-1, 44, 40, 22);
		lbl_number.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_LIST_SELECTION));
		toolkit.adapt(lbl_number);
		toolkit.paintBordersFor(lbl_number);
		lbl_number.setText("0");

		Link link_browser = new Link(this, SWT.NONE);
		link_browser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browserPath();
			}
		});
		link_browser.setBounds(622, 47, 69, 17);
		toolkit.adapt(link_browser, true, true);
		link_browser.setText("<a>\u6D4F\u89C8\u670D\u52A1\u5668</a>");

		CLabel label_3 = new CLabel(this, SWT.NONE);
		label_3.setText("\u540C\u6B65\u8BBE\u7F6E");
		label_3.setAlignment(SWT.RIGHT);
		label_3.setBounds(36, 78, 89, 23);
		toolkit.adapt(label_3);
		toolkit.paintBordersFor(label_3);
		setTabList(new Control[] { txt_servicesName, txt_adminUrl,
				txt_stagePath, cbx_updateFile, label_1, label, label_2 });
	}

	public void browserPath() {
		if(server==null){
			return;
		}
		try{
			Tool.checkPort(server.getAgentPort());
		}catch(Exception e){
			MessageBoxUc.OpenError(getShell(), "请先在“应用参数配置”中录入正确的“Agent端口号”！");
			return;
		}
		if (!Tool.checkIp(server.getServerIp())) {
			MessageBoxUc.OpenError(getShell(), "请先在“应用参数配置”中录入正确的“服务器IP地址”!");
			return;
		}
		
		if (NetWorkUtil.TestConnectBySocket(server.getServerIp(),
				Integer.parseInt(server.getAgentPort()), 3000)) {
			ManageServerFile bsp = new ManageServerFile(getShell(), server,
					server.getDomainPath());
			if (bsp.open() == 0) {
				txt_stagePath.setText(bsp.getSelectPath());
				clusterModel.setStagePath(txt_stagePath.getText());
			}
		} else {
			MessageBoxUc.OpenError(getShell(), "连接Agent失败，请检查!");
		}
	}

	private String getId() {
		return clusterId;
	}

	/**
	 * 设置控件显示的“序号”
	 * 
	 * @param num
	 */
	public void setNumber(String num) {
		lbl_number.setText(num);
	}

	public void setId(String id) {
		this.clusterId = id;
	}

	public void setServerName(String serverName) {
		txt_servicesName.setText(serverName);
		clusterModel.setName(txt_servicesName.getText());
	}

	public void setAdminUrl(String adminUrl) {
		txt_adminUrl.setText(adminUrl);
		clusterModel.setAdminUrl(txt_adminUrl.getText());

	}

	public void setStagePath(String stagePath) {
		txt_stagePath.setText(stagePath);
		clusterModel.setStagePath(txt_stagePath.getText());

	}

	public void setIsUploadFile(int flag) {
		if (flag == 1) {
			cbx_updateFile.setSelection(true);
		} else {
			cbx_updateFile.setSelection(false);
		}
		clusterModel.setIsUploadFile(cbx_updateFile.getSelection() == true ? 1
				: 0);

	}

	/**
	 * 获取集群服务实例
	 * 
	 * @return
	 */
	public ClusterServicesModel getClusterServerModel() {
		clusterModel.setId(getId());
		clusterModel.setSid(getSid());
		return clusterModel;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
}
