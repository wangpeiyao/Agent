package cn.com.hnisi.view.component;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import cn.com.hnisi.dao.FastLabelDAO;
import cn.com.hnisi.dao.FastLabelServerDAO;
import cn.com.hnisi.model.FastLabelModel;
import cn.com.hnisi.util.Tool;

/**
 * 快捷标签控件
 * @author WenZhiFeng
 * 2018年2月1日
 */
public class FastLabelUc extends Composite {
	static Logger log = Logger.getLogger(FastLabelUc.class);
	//设置鼠标手势
	static Cursor handCursor=new Cursor(Display.getDefault(), SWT.CURSOR_HAND); 
	
	private Composite parent;
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	/**
	 * 标签名称
	 */
	public CLabel lbl_name;
	//原标签名称
	private String sourceLabelName="";
	//默认名称
	private final String defaultName="新建标签";
	/**
	 * 删除
	 */
	public CLabel lbl_delete;
	
	/**
	 * 判断是否被选中
	 */
	public boolean isSelected=false;
	private Text txt_edit;
	//快捷标签对象
	private FastLabelModel labelModel=null;
	/**
	 * 创建标签控件
	 * @param parent 父容器
	 * @param style 样式
	 * @param labelName 标签名称
	 * @param id 标签数据库主键，如果为NULL视为新增
	 */
	public FastLabelUc(final Composite parent, int style,FastLabelModel labelModel) {
		super(parent, SWT.BORDER);
		
		this.parent=parent;	
		this.labelModel=labelModel;
		setBackgroundMode(SWT.INHERIT_FORCE);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new FormLayout());
		
		lbl_name = new CLabel(this, SWT.NONE);
		lbl_name.setImage(SWTResourceManager.getImage(FastLabelUc.class, "/bright_star.png"));
		FormData fd_lbl_name = new FormData();
		fd_lbl_name.bottom = new FormAttachment(100);
		fd_lbl_name.right = new FormAttachment(100, -25);
		fd_lbl_name.left = new FormAttachment(0);
		lbl_name.setLayoutData(fd_lbl_name);
		lbl_name.setToolTipText("\u53CC\u51FB\u7F16\u8F91");
		lbl_name.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_FOREGROUND));
		lbl_name.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		lbl_name.setCursor(handCursor);
		lbl_name.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		toolkit.adapt(lbl_name, true, true);
		//删除标签
		lbl_delete = new CLabel(this, SWT.NONE);
		fd_lbl_name.top = new FormAttachment(lbl_delete, 0, SWT.TOP);
		
		Menu menu = new Menu(lbl_name);
		lbl_name.setMenu(menu);
		//重置
		MenuItem menuItem_reset = new MenuItem(menu, SWT.NONE);
		menuItem_reset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetLabel();
			}
		});
		menuItem_reset.setImage(SWTResourceManager.getImage(FastLabelUc.class, "/document_redirect.png"));
		menuItem_reset.setText("\u91CD\u7F6E\u6807\u7B7E\u5E94\u7528");
		FormData fd_lbl_delete = new FormData();
		fd_lbl_delete.top = new FormAttachment(0);
		fd_lbl_delete.right = new FormAttachment(100);
		fd_lbl_delete.bottom = new FormAttachment(100);
		lbl_delete.setLayoutData(fd_lbl_delete);
		lbl_delete.setToolTipText("\u5220\u9664\u6807\u7B7E");
		lbl_delete.setAlignment(SWT.CENTER);
		lbl_delete.setCursor(handCursor);
		lbl_delete.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		lbl_delete.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BORDER));
		toolkit.adapt(lbl_delete);
		toolkit.paintBordersFor(lbl_delete);
		lbl_delete.setText("\u00D7");
		
		txt_edit = new Text(this, SWT.BORDER);
		FormData fd_txt_edit = new FormData();
		fd_txt_edit.bottom = new FormAttachment(100);
		fd_txt_edit.right = new FormAttachment(100, -20);
		fd_txt_edit.top = new FormAttachment(0);
		fd_txt_edit.left = new FormAttachment(0);
		txt_edit.setLayoutData(fd_txt_edit);
		txt_edit.setToolTipText("按Esc退出 | 按Enter键保存");
		txt_edit.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				//失去焦点时
				cancelEdit();//退出编辑
				createOrUpdateLabel();//创建或更新标签
			}
		});
		txt_edit.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode==SWT.ESC){					
					cancelEdit();
				}else if(e.keyCode==13){
					//保存修改
					log.info("保存修改");
					cancelEdit();
					createOrUpdateLabel();
				}
			}
		});
		toolkit.adapt(txt_edit, true, true);
		txt_edit.setVisible(false);
		
		if(labelModel!=null){
			sourceLabelName=labelModel.getName();
		}else{
			createOrUpdateLabel();
		}
		setLabelName(labelModel);
		setData(this.labelModel);
		setBounds(parent);
		parent.layout();
	}

	/**
	 * 进行编辑
	 */
	public void setEdit(){
		txt_edit.setVisible(true);
		txt_edit.moveAbove(lbl_name);
		if(labelModel!=null){
			txt_edit.setText(labelModel.getName());
		}		
		txt_edit.setSelection(0,txt_edit.getText().length());
		txt_edit.setFocus();
	}
	
	/**
	 * 取消编辑
	 */
	public void cancelEdit(){
		txt_edit.setVisible(false);	
	}
	/**
	 * 创建或更新标签
	 */
	private void createOrUpdateLabel(){
		if(this.labelModel!=null){
			if(txt_edit.getText().trim().equals("")){
				setLabelName(sourceLabelName);
			}else{
				setLabelName(txt_edit.getText());
			}
		}else{
			setLabelName(defaultName);
		}
		try {
			//创建新的标签
			if(this.labelModel==null){
				this.labelModel=new FastLabelModel(Tool.getUUID(),getLabelName(),Tool.getNowDateSSS());
				this.labelModel.setClicks(1);
				this.labelModel.setServerCount(0);
				FastLabelDAO.insertFastLabel(labelModel);
			}else{
				//修改原有标签
				this.labelModel=FastLabelDAO.getFastLabelByModel(labelModel);
				this.labelModel.setName(getLabelName());
				this.labelModel.setLasttime(Tool.getNowDateSSS());
				FastLabelDAO.updateFastLabel(labelModel);
			}
			setLabelName(labelModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setBounds(parent);
	}
	/**
	 * 重置标签
	 */
	public void resetLabel(){
		if(labelModel!=null){
			try {
				FastLabelServerDAO.deleteLabelServer(labelModel.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 删除标签
	 */
	public void deleteLabel(){
		if(labelModel!=null){
			try {
				FastLabelDAO.deleteFastLabel(labelModel);
				log.info("删除成功");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置标签名称
	 * @param name
	 */
	public void setLabelName(String name){
		if(lbl_name!=null){
			lbl_name.setText(name);
		}
	}
	
	/**
	 * 获取标签名称
	 * @return
	 */
	public String getLabelName(){
		return lbl_name.getText();
	}
	
	public void setLabelName(FastLabelModel labelModel){
		if(labelModel!=null){
			lbl_name.setText(labelModel.getName()+" ( "+labelModel.getServerCount()+" ) ");
		}
	}
	/**
	 * 设置选中状态
	 */
	public void setSelectedStyle(){
		if(isSelected){
			//不选中
			isSelected=false;
			lbl_name.setImage(SWTResourceManager.getImage(FastLabelUc.class, "/bright_star.png"));
			//lbl_name.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.NORMAL));
		}else{
			//被选中
			isSelected=true;
			lbl_name.setImage(SWTResourceManager.getImage(FastLabelUc.class, "/tick.png"));	
			//lbl_name.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		}
		parent.layout();
	}
	
	/**
	 * 根据文本宽度设置控件大小
	 */
	private void setBounds(Composite parent){
		int charWidth=16;//每个字符的宽度
		int height=23;
		int width=0;
		width=lbl_name.getText().length()*charWidth;
		setBounds(1, 1, width+50, height);
		parent.layout();
	}

	/**
	 * 获取快捷标签ID
	 * @return
	 */
	public String getLabelId() {
		if(labelModel==null){
			return null;
		}else{
			return labelModel.getId();
		}
	}

	/**
	 *  获取快捷标签
	 * @return
	 */
	public FastLabelModel getLabelModel() {
		return labelModel;
	}
}
