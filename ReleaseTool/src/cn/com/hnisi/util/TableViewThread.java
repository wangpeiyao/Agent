package cn.com.hnisi.util;

import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import cn.com.hnisi.database.SQLiteDatabase;

public class TableViewThread implements Runnable {
	private Display display;
	private String sql;
	private Table table;
	private String[] columns;
	private String setDateColumns;
	private boolean isCheckboxTableViewer = false;
	private boolean isSelectAll=false;
	static Logger log=Logger.getLogger(TableViewThread.class);
	/**
	 * 
	 * @param display
	 * @param sql
	 * @param table
	 *            Table
	 * @param columns
	 * @param setDateColumns
	 * @param isCheckboxTableViewer
	 *            是否为CheckboxTableViewer
	 */
	public TableViewThread(Display display, String sql, Table table,
			String[] columns, String setDateColumns,
			boolean isCheckboxTableViewer) {
		this.display = display;
		this.sql = sql;
		this.table = table;
		this.columns = columns;
		this.setDateColumns = setDateColumns;
		this.isCheckboxTableViewer = isCheckboxTableViewer;
		new Thread(this).start();
	}

	/**
	 * 
	 * @param display
	 * @param sql
	 * @param table
	 *            Table
	 * @param columns
	 * @param setDateColumns
	 * @param isCheckboxTableViewer
	 *            是否为CheckboxTableViewer
	 * @param isSelectAll
	 * 是否全选
	 */
	public TableViewThread(Display display, String sql, Table table,
			String[] columns, String setDateColumns,
			boolean isCheckboxTableViewer,boolean isSelectAll) {
		this.display = display;
		this.sql = sql;
		this.table = table;
		this.columns = columns;
		this.setDateColumns = setDateColumns;
		this.isCheckboxTableViewer = isCheckboxTableViewer;
		this.isSelectAll=isSelectAll;
	}
	@Override
	public void run() {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {

				SQLiteDatabase db = SQLiteDatabase.getInstance();
				ResultSet rs = null;
				try {
					db.openDatabase();
					if (!table.isDisposed()) {
						
						table.removeAll();
						rs = db.executeQuery(sql);
						String[] cs = new String[columns.length];
						int lineNumber=1;
						while (rs.next()) {
							TableItem record = new TableItem(table, SWT.NONE);
							for (int i = 0; i < cs.length; i++) {
								//如果是非CheckboxTableViewer第一列显示为行号
								if(i==0&&!isCheckboxTableViewer){
									cs[i]=lineNumber+"";
									lineNumber++;
								}else{
									cs[i] = rs.getString(columns[i]);
								}
								
							}
							
							record.setText(cs);
							if (isCheckboxTableViewer) {
								//如果是CheckBoxTableViewer这一列显示勾选框，不是内容
								record.setText("");
							}
							record.setData(rs.getString(setDateColumns));// 存储主键id
						}
						//是否自动全选
						if(isSelectAll){
							TableItem[] items = table.getItems();
							for (TableItem tableItem : items) {
									tableItem.setChecked(true);
							}
						}
					}
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					log.error("获取数据线程出错"+e.getMessage());
				} finally {
					db.close();
				}
			}
		});
	}

}
