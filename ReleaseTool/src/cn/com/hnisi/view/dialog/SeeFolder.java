package cn.com.hnisi.view.dialog;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.util.Tool;

/**
 * 查看所属文件夹下所有的子文件夹和文件
 * @author FengGeGe
 *
 */
public class SeeFolder extends Dialog {
	protected Shell shell;
	private String folderPath="文件夹预览";
	Image imageFolder=SWTResourceManager.getImage(SeeFolder.class, "/folder.png");
	Image imageFile=SWTResourceManager.getImage(SeeFolder.class, "/file.png");
	private Tree tree;
	private int folderCount=0;
	private int fileCount=0;
	private int fileSizeCount=0;
	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public SeeFolder(Shell parentShell) {
		super(parentShell);

		this.shell=parentShell;
	}

	public SeeFolder(Shell parentShell,String folderPath) {
		super(parentShell);
		this.shell=parentShell;
		this.folderPath=folderPath;
	}
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		tree=new Tree(container, SWT.BORDER|SWT.Expand|SWT.V_SCROLL|SWT.H_SCROLL);
		tree.setBounds(0, 0, 491, 434);
		showFolder(this.folderPath,tree,null);
		
		Label lbl_count = new Label(container, SWT.NONE);
		lbl_count.setBounds(10, 441, 426, 17);
		lbl_count.setText("包含:  "+fileCount+"  个文件，"+folderCount+"  个文件夹.  总大小:  "+Tool.getFileSize(fileSizeCount));
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(-1, 463, 492, 2);
		
		
		return container;
	}

	/**
	 * 递归文件夹，把文件夹路径在Tree中显示
	 * @param dirPath
	 * 文件夹路径
	 * @param tr
	 * Tree控件
	 * @param parentItem
	 * 父节点
	 */
	public void showFolder(String dirPath,Tree tr,TreeItem parentItem){
		File file=new File(dirPath);
		if(file.isDirectory()){
			TreeItem item=null;
			String path=file.getPath().replace("\\", "/");
			if(parentItem==null){
				item=new TreeItem(tr,SWT.Expand);
			}else{
				item=new TreeItem(parentItem,SWT.Expand);
			}
			item.setImage(imageFolder);
			item.setText(path.substring(path.lastIndexOf("/")+1));
			folderCount++;
			if(file!=null){
				for(String s:file.list()){
					File f=new File(dirPath+"\\"+s);
					if(f.isDirectory()){
						showFolder(f.getPath(),null,item);
					}else if(f.isFile()){
						TreeItem file_item=new TreeItem(item,SWT.NONE);
						file_item.setImage(imageFile);
						file_item.setText(f.getName()+" ("+Tool.getFileSize(f.length())+")");
						fileSizeCount+=f.length();
						fileCount++;
						//自动展开
						tree.showItem(file_item);
					}
				}
			}
		}
	}
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btne = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		btne.setText("\u786E\u5B9A(&E)");
	}
	@Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        // Dialog Title
        newShell.setText("位置: "+this.folderPath);

    }
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(497, 549);
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
}
