package cn.com.hnisi.view.component;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class PopupMenuUc {
	private StyledText textControl;
	public PopupMenuUc(StyledText textControl){
		this.textControl=textControl;
		init();
	}
	
	private void init(){
		Menu menu = new Menu(textControl);
		textControl.setMenu(menu);
		// 剪切
		MenuItem mntm_cut = new MenuItem(menu, SWT.CASCADE);
		mntm_cut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				Transferable tText = new StringSelection(textControl
						.getSelectionText());
				clip.setContents(tText, null);
				textControl.setText("");
			}
		});
		mntm_cut.setText("剪切(&U)");
		// 复制
		MenuItem mntm_copy = new MenuItem(menu, SWT.CASCADE);
		mntm_copy.setImage(SWTResourceManager.getImage(PopupMenuUc.class,
				"/copy.png"));
		mntm_copy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				Transferable tText = new StringSelection(textControl
						.getSelectionText());
				clip.setContents(tText, null);

			}
		});
		mntm_copy.setText("复制(&C)");

		new MenuItem(menu, SWT.SEPARATOR);

		MenuItem mntm_clear = new MenuItem(menu, SWT.CASCADE);
		mntm_clear.setImage(SWTResourceManager.getImage(PopupMenuUc.class,
				"/clear.png"));
		mntm_clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textControl.setText("");

			}
		});
		mntm_clear.setText("清除(&R)");
	}
}
