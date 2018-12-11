package cn.com.hnisi.view.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 提示窗口
 * @author FengGeGe
 *
 */
public class MessageBoxUc {
	/**
	 * 确认“是”、“否”
	 * @param shell
	 * @param content
	 * 需要显示的内容
	 * @return
	 * 是返回true，否返回false
	 */
	public static boolean OpenConfirm(Shell shell,String content){
    	MessageBox messagebox = new MessageBox(shell, SWT.ICON_INFORMATION
                | SWT.YES | SWT.NO);
        messagebox.setText("确认提示");
        messagebox.setMessage(content+"") ;
        if(messagebox.open()==SWT.YES){
        	return true;
        }
        return false;
	}
	
	/**
	 * 提示“OK”
	 * @param shell
	 * @param content
	 * 需要显示的内容
	 * @return
	 */
	public static void OpenOk(Shell shell,String content){
		    	MessageBox messagebox = new MessageBox(shell, SWT.ICON_INFORMATION
		                | SWT.OK);
		        messagebox.setText("操作提示");
		        messagebox.setMessage(content+"") ;
		        messagebox.open();
	}
	
	
	
	/**
	 * 提示错误 
	 * @param shell
	 * @param content
	 * 需要显示的内容
	 * @return
	 */
	public static int OpenError(Shell shell,String content){
    	MessageBox messagebox = new MessageBox(shell, SWT.ICON_ERROR
                | SWT.OK);
        messagebox.setText("错误提示");
        messagebox.setMessage(content+"") ;
        return messagebox.open();
	}
}
