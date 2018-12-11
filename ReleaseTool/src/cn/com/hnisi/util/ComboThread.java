package cn.com.hnisi.util;

import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;

import cn.com.hnisi.database.SQLiteDatabase;

/**
 * 将数据绑定到CCombo控件下拉项
 * 
 * @author FengGeGe
 * 
 */
public class ComboThread implements Runnable {

	private String sql;
	private CCombo comBo;
	private Display display;
	static Logger log=Logger.getLogger(ComboThread.class);
	/**
	 * 
	 * @param display
	 *            它是swt与操作系统沟通的一座桥梁
	 * @param comBo
	 *            CCombo控件
	 * @param sql
	 *            数据库查询SQL语句
	 */
	public ComboThread(Display display, CCombo comBo, String sql) {
		this.comBo = comBo;
		this.sql = sql;
		this.display = display;
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
					if (comBo!=null && !comBo.isDisposed()) {
						comBo.removeAll();
						rs = db.executeQuery(sql);
						int i = 0;
						while (rs.next()) {
							comBo.add(rs.getString("name"),i);
							comBo.setData(i + "", rs.getString("id"));
							i++;
						}
						if(i>0){
							comBo.setVisibleItemCount(i);
							comBo.select(0);
						}
					}

				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					log.error("获取数据出错"+e.getMessage());
					e.printStackTrace();
				} finally {
					db.close();
				}

			}
		});
	}
}
