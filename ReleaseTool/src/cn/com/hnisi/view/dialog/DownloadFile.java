package cn.com.hnisi.view.dialog;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.agent.services.function.DownloadFileHandler;
import cn.com.hnisi.model.ServerModel;
import cn.com.hnisi.util.Tool;

/**
 * 文件下载
 * 
 * @author FengGeGe
 * 
 */
public class DownloadFile extends TitleAreaDialog {
	static Logger log = Logger.getLogger(DownloadFile.class);
	private String saveFilePath;
	private String downloadPath;
	private ServerModel server;
	SocketChannel socketChannel = null;
	SocketAddress endpoint = null;

	private ProgressBar progressBar;
	private CLabel lbl_downloadFileName;
	private CLabel lbl_fileSize;
	private CLabel lbl_downloadSize;
	private CLabel lbl_saveFile;
	private CLabel lbl_speed;

	/**
	 * 
	 * @param parentShell
	 * @param downloadPath
	 *            下载文件
	 * @param saveFilePath
	 *            保存路径
	 * @param server
	 */
	public DownloadFile(Shell parentShell, String downloadPath,
			String saveFilePath, ServerModel server) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.MIN | SWT.CLOSE);
		this.downloadPath = downloadPath;
		this.saveFilePath = saveFilePath;
		this.server = server;

	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(SWTResourceManager.getImage(DownloadFile.class,
				"/download_file.png"));
		super.configureShell(newShell);
		if (server != null) {
			newShell.setText("下载文件");
		}

	}

	/** 处理窗口关闭事件 */
	@Override
	public void handleShellCloseEvent() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				close();
			}
		});

	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("\u5173\u95ED\u7A97\u53E3\u53EF\u4EE5\u505C\u6B62\u4E0B\u8F7D.");
		setTitle("\u6587\u4EF6\u4E0B\u8F7D");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		CLabel label = new CLabel(container, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setBounds(10, 10, 67, 23);
		label.setText("\u8FDC\u7A0B\u6587\u4EF6");

		lbl_downloadFileName = new CLabel(container, SWT.BORDER);
		lbl_downloadFileName.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		lbl_downloadFileName.setBounds(85, 11, 574, 23);
		lbl_downloadFileName.setText("");

		CLabel label_1 = new CLabel(container, SWT.NONE);
		label_1.setAlignment(SWT.RIGHT);
		label_1.setBounds(10, 135, 67, 23);
		label_1.setText("\u4E0B\u8F7D\u8FDB\u5EA6");

		progressBar = new ProgressBar(container, SWT.NONE);
		progressBar.setBounds(85, 134, 574, 25);

		CLabel lblNewLabel_1 = new CLabel(container, SWT.NONE);
		lblNewLabel_1.setAlignment(SWT.RIGHT);
		lblNewLabel_1.setBounds(10, 92, 67, 23);
		lblNewLabel_1.setText("\u6587\u4EF6\u5927\u5C0F");

		lbl_fileSize = new CLabel(container, SWT.SHADOW_IN);
		lbl_fileSize.setBounds(85, 92, 84, 23);
		lbl_fileSize.setText("0.0 B");

		progressBar.setSelection(0);

		CLabel label_2 = new CLabel(container, SWT.NONE);
		label_2.setAlignment(SWT.RIGHT);
		label_2.setBounds(175, 92, 52, 23);
		label_2.setText("\u5DF2\u4E0B\u8F7D");

		lbl_downloadSize = new CLabel(container, SWT.SHADOW_IN);
		lbl_downloadSize.setBounds(233, 92, 84, 23);
		lbl_downloadSize.setText("0.0 B");

		CLabel label_3 = new CLabel(container, SWT.NONE);
		label_3.setAlignment(SWT.RIGHT);
		label_3.setBounds(10, 49, 67, 23);
		label_3.setText("\u672C\u5730\u6587\u4EF6");

		lbl_saveFile = new CLabel(container, SWT.SHADOW_IN);
		lbl_saveFile.setBounds(85, 49, 574, 23);
		lbl_saveFile.setText("");

		// 本地文件
		lbl_saveFile.setText(saveFilePath);

		CLabel lblNewLabel = new CLabel(container, SWT.NONE);
		lblNewLabel.setAlignment(SWT.RIGHT);
		lblNewLabel.setBounds(323, 92, 77, 23);
		lblNewLabel.setText("\u4E0B\u8F7D\u901F\u5EA6/\u79D2");
		// 下载速度
		lbl_speed = new CLabel(container, SWT.SHADOW_IN);
		lbl_speed.setBounds(406, 92, 84, 23);
		lbl_speed.setText("");

		download();
		return area;
	}

	long startTime, endTime;

	private void download() {
		startTime = System.currentTimeMillis();
		this.lbl_downloadFileName.setText(this.downloadPath);
		DownloadFileHandler dowloadFileThread = new DownloadFileHandler(
				this.downloadPath, this.saveFilePath, server, this);
		Thread th = new Thread(dowloadFileThread);
		th.start();

	}
	/**
	 * 弹窗提示下载信息
	 * @param message
	 */
	public void showDownloadInfo(final String message){
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openInformation(getShell(), "下载提示", message);
				close();
			}
		});
	}
	/**
	 * 设置显示文件大小
	 * 
	 * @param fileLength
	 */
	public void setFileSize(final long fileLength) {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if (!lbl_fileSize.isDisposed()) {
					lbl_fileSize.setText(Tool.getFileSize(fileLength));
					progressBar.setMaximum((int) fileLength);
				}
			}
		});
	}

	/**
	 * 显示进度条
	 */
	public void setProgressBar(final int selection) {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				if (!progressBar.isDisposed()) {
					endTime = System.currentTimeMillis();
					long time = endTime - startTime;
					progressBar.setSelection(selection);
					lbl_downloadSize.setText(Tool.getFileSize(selection));
					// 计算下载速度
					long speed = progressBar.getSelection() / time;// 计算每毫秒或秒的下载量
					lbl_speed.setText(Tool.getSpeed(speed * 1024));

					if (selection == progressBar.getMaximum()) {
						MessageDialog.openInformation(getShell(), "提示", "下载完毕");
						handleShellCloseEvent();
					}
				}
			}
		});
	}

	/**
	 * 显示下载状态
	 * 
	 * @param content
	 */
	public void downloadStatus(final String content) {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openInformation(getShell(), "提示", content);
			}
		});
	}

	/**
	 * Create contents of the button bar.
	 * 
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

		return new Point(675, 336);
	}

//	public class DownloadFileThread implements Runnable {
//		DownloadFile downloadModel;
//		ServerModel server;
//		SocketChannel socketChannel = null;
//		SocketAddress endpoint = null;
//		String downloadpath = null;
//		String saveFilePath = null;
//
//		public DownloadFileThread(String downloadPath, String saveFilePath,
//				ServerModel server, DownloadFile downloadModel) {
//			this.server = server;
//			this.downloadpath = downloadPath;
//			this.saveFilePath = saveFilePath;
//			this.downloadModel = downloadModel;
//		}
//
//		public void run() {
//			FileOutputStream out = null;
//			try {
//				endpoint = new InetSocketAddress(server.getServerIp(),
//						Integer.parseInt(server.getAgentPort()));
//				if (NetWorkUtil.TestConnectBySocket(endpoint, 3500)) {
//					socketChannel = SocketChannel.open();
//					socketChannel.connect(endpoint);
//					// 开始请求
//					byte[] byteServer = Tool.objectToByte(server);// ServerModel
//					// (请求类型)+(server对象字节长度+server对象字节)+(4+下载文件目标)
//					// fileHeadBuffer.put(ByteBuffer.wrap(Tool.utfToIso(serverFolder).getBytes("ISO-8859-1")));//
//					// 文件路径内容
//					int countSize = (4)
//							+ (4 + byteServer.length)
//							+ (4 + Tool.utfToIso(downloadPath).getBytes(
//									"ISO-8859-1").length);
//					ByteBuffer buf = ByteBuffer.allocate(countSize);
//
//					// =====================
//					// 请求类型为“命令类型”(命令：700 下载日志)
//					buf.putInt(RequestType.DOWNLOAD_FILE.getValue());
//
//					// 发送server对象长度+内容
//					buf.putInt(byteServer.length);
//					buf.put(ByteBuffer.wrap(byteServer));
//
//					// 发送下载文件路径
//					buf.putInt(Tool.utfToIso(downloadPath).getBytes(
//							"ISO-8859-1").length);
//					buf.put(ByteBuffer.wrap(Tool.utfToIso(downloadPath)
//							.getBytes("ISO-8859-1")));
//
//					buf.flip();
//					while (buf.hasRemaining()) {
//						socketChannel.write(buf);
//					}
//					buf.clear();
//					// ================================
//
//					Thread.sleep(1000);
//
//					// 开始接收文件
//					int size = 0;
//					// 接收状态码
//					buf = ByteBuffer.allocate(4);
//					int statusCode = 0;
//					size = socketChannel.read(buf);
//					if (size > 0) {
//						buf.flip();
//						statusCode = buf.getInt();
//						buf.clear();
//					}
//					// 接收状态信息长度值
//					buf = ByteBuffer.allocate(4);
//					int statusInfoLength = 0;
//					size = socketChannel.read(buf);
//					if (size > 0) {
//						buf.flip();
//						statusInfoLength = buf.getInt();
//						buf.clear();
//					}
//					byte[] statusInfo = null;
//					buf = ByteBuffer.allocate(statusInfoLength);
//					size = socketChannel.read(buf);
//					if (size > 0) {
//						statusInfo = new byte[size];
//						buf.flip();
//						buf.get(statusInfo);
//						buf.clear();
//					}
//					if (statusCode != 0) {
//						//如果状态不为0，则提示报错
//						downloadModel.showDownloadInfo(new String(statusInfo,"utf-8"));
//						return;
//					} else {
//						buf = ByteBuffer.allocate(4);
//						int nameLength = 0;
//						byte[] fileName = null;
//						size = socketChannel.read(buf);
//						if (size > 0) {
//							buf.flip();
//							nameLength = buf.getInt();
//							buf.clear();
//							buf = ByteBuffer.allocate(nameLength);
//							size = socketChannel.read(buf);
//							fileName = new byte[size];
//							buf.flip();
//							buf.get(fileName);
//							buf.clear();
//						}
//
//						buf = ByteBuffer.allocate(8);
//						long fileLength = 0L;
//
//						size = socketChannel.read(buf);
//						if (size == 8) {
//							buf.flip();
//							fileLength = buf.getLong();// 文件长度
//							downloadModel.setFileSize(fileLength);
//							buf.clear();
//							int bufSize = 1024000;
//							long remainingSize = fileLength;
//							// 获取文件内容
//							byte[] fileByte = null;
//
//							long writedSize = 0;
//							File file = new File(saveFilePath);
//							if (!file.exists()) {
//								file.createNewFile();
//							}
//							out = new FileOutputStream(file);
//
//							while (remainingSize > 0) {
//								bufSize = 1024 * 1024;
//								if (remainingSize < bufSize) {
//									bufSize = (int) remainingSize;
//								}
//								buf = ByteBuffer.allocate(bufSize);
//								size = socketChannel.read(buf);
//								if (size >= 0) {
//									buf.flip();
//									fileByte = new byte[size];
//									buf.get(fileByte);
//									writedSize += size;
//									out.write(fileByte);
//									out.flush();
//									buf.clear();
//									remainingSize -= size;// 计算剩余可读取的字节数
//									downloadModel
//											.setProgressBar((int) writedSize);
//								}
//							}
//						}
//					}
//				} else {
//					downloadModel.downloadStatus("下载失败，无法连接到Agent代理器，请检查");
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				downloadModel.downloadStatus("下载失败，原因:" + e.getMessage());
//			} finally {
//				if (out != null) {
//					try {
//						out.close();
//						socketChannel.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//
//			}
//			downloadModel.handleShellCloseEvent();
//		}
//	}
}
