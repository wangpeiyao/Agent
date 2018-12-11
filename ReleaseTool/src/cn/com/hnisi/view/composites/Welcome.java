package cn.com.hnisi.view.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 登录欢迎界面
 * @author FengGeGe
 *
 */
public class Welcome extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Welcome(Composite parent, int style) {
		super(parent, style);
		setFont(SWTResourceManager.getFont("幼圆", 26, SWT.NORMAL));
		setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		setLayout(new FormLayout());
		setSize(1000,650);
		CLabel lbl_logo = new CLabel(this, SWT.NONE);
		FormData fd_lbl_logo = new FormData();
		fd_lbl_logo.bottom = new FormAttachment(100, -10);
		lbl_logo.setLayoutData(fd_lbl_logo);
		lbl_logo.setAlignment(SWT.RIGHT);
		lbl_logo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lbl_logo.setBottomMargin(0);
		lbl_logo.setTopMargin(0);
		lbl_logo.setRightMargin(0);
		lbl_logo.setLeftMargin(0);
		lbl_logo.setImage(SWTResourceManager.getImage(Welcome.class, "/logo.gif"));
		lbl_logo.setText("");
		
		final Label lblNewLabel = new Label(this, SWT.NONE);
		fd_lbl_logo.right = new FormAttachment(lblNewLabel, 0, SWT.RIGHT);
		fd_lbl_logo.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.right = new FormAttachment(100);
		fd_lblNewLabel.top = new FormAttachment(0);
		fd_lblNewLabel.left = new FormAttachment(0);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		final Image image=SWTResourceManager.getImage(Welcome.class, "/welcome.png");
		lblNewLabel.setImage(image);
		
		Label label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(lbl_logo, -12, SWT.TOP);
		fd_label.right = new FormAttachment(lbl_logo, 0, SWT.RIGHT);
		fd_label.left = new FormAttachment(lbl_logo, 0, SWT.LEFT);
		fd_label.bottom = new FormAttachment(lbl_logo, -10);
		label.setLayoutData(fd_label);
		
		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_1.setImage(SWTResourceManager.getImage(Welcome.class, "/world.jpg"));
		FormData fd_lblNewLabel_1 = new FormData();
		fd_lblNewLabel_1.top = new FormAttachment(0, 257);
		fd_lblNewLabel_1.left = new FormAttachment(0);
		fd_lblNewLabel_1.right = new FormAttachment(100);
		fd_lblNewLabel_1.bottom = new FormAttachment(100, -115);
		lblNewLabel_1.setLayoutData(fd_lblNewLabel_1);
		

		lblNewLabel.addPaintListener(new PaintListener() {
			    @Override
			    public void paintControl(PaintEvent e) {
			     Point size = lblNewLabel.getSize();
			     Point p = lblNewLabel.getLocation();
			     //1000，216为图片尺寸大小，不能大小图片尺寸
			     e.gc.drawImage(image, 0, 0, 1000, 216, p.x, p.y, size.x, size.y);
			    }
			   });
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
