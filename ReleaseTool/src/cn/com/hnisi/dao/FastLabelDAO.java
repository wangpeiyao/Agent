package cn.com.hnisi.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.model.FastLabelModel;

/**
 * 快捷标签数据库表访问对象
 * 
 * @author WenZhiFeng 2018年2月4日
 */
public class FastLabelDAO {
	static Logger log = Logger.getLogger(FastLabelDAO.class);
	final static SQLiteDatabase dao = SQLiteDatabase.getInstance();
	final static String sqlStr = "select a.*,(select count(1) from fastlabel_server b where a.id=b.label_id) serverCount from fastlabel a";

	/**
	 * 获取所有标签
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<FastLabelModel> getAllFastLabel() throws Exception {
		ResultSet rs = null;
		try {
			//log.info("获取所有标签");
			List<FastLabelModel> fastLableList = new ArrayList<FastLabelModel>();
			dao.openDatabase();
			rs = dao.executeQuery(sqlStr + " order by clicks desc");
			while (rs.next()) {
				fastLableList.add(getFastLabel(rs));
			}
			return fastLableList;
		} catch (Exception e) {
			throw new Exception("获取快捷标签出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 根据ID获取快捷标签
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static FastLabelModel getFastLabelById(String id) throws Exception{
		ResultSet rs = null;
		FastLabelModel labelModel = null;
		try {
			//log.info("获取标签 id="+id);
			dao.openDatabase();
			rs = dao.executeQuery(sqlStr + " where id='"+id+"'");
			while (rs.next()) {
				labelModel = getFastLabel(rs);
			}
		} catch (Exception e) {
			throw new Exception("获取快捷标签出错：" + e.getMessage());
		} finally {
			dao.close();
		}
		return labelModel;
	}
	
	/**
	 * 根据FastLabelModel对象获取快捷标签
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static FastLabelModel getFastLabelByModel(FastLabelModel labelModel) throws Exception{
		if(labelModel==null){
			throw new Exception("快捷对象为空，获取失败！");
		}
		return getFastLabelById(labelModel.getId());
	}
	
	
	public static FastLabelModel getFastLabel(ResultSet rs) throws SQLException {
		FastLabelModel labelModel = null;
		if (rs != null) {
			labelModel = new FastLabelModel();
			labelModel.setId(rs.getString("id"));// 标签主键ID
			labelModel.setName(rs.getString("name"));// 标签名称
			labelModel.setLasttime(rs.getString("lasttime"));// 最后一次的修改时间
			labelModel.setClicks(rs.getInt("clicks"));//使用次数
			labelModel.setServerCount(rs.getInt("serverCount"));//关联查询字段(非表字段)
		}
		return labelModel;
	}

	/**
	 * 最新快捷标签
	 * @param labelModel
	 * @return
	 * @throws Exception
	 */
	public static int insertFastLabel(FastLabelModel labelModel)
			throws Exception {
		if (labelModel.getId() == null || labelModel.getId().equals("")) {
			throw new Exception("新增快捷标签出错，ID不能为空！");
		}
		try{
			dao.openDatabase();
			String sqlStr = "insert into fastlabel(id,name,lasttime,clicks) values('"
				+ labelModel.getId() + "','" +  dao.TransactSQLInjection(labelModel.getName()) + "','"
				+ labelModel.getLasttime() + "',"+labelModel.getClicks()+")";
			return dao.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("新增快捷标签出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 更新快捷标签
	 * @param labelModel
	 * @return
	 * @throws Exception
	 */
	public static int updateFastLabel(FastLabelModel labelModel)
			throws Exception {
		if (labelModel.getId() == null || labelModel.getId().equals("")) {
			throw new Exception("更新标签时，主键ID不能为空");
		}
		try{
			dao.openDatabase();
			String sqlStr = "update fastlabel set name='"+dao.TransactSQLInjection(labelModel.getName())+"',lasttime='"+labelModel.getLasttime()+"' where id='"+labelModel.getId()+"'";
			return dao.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("新增快捷标签出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 更新使用次数
	 * @param labelModel
	 * @return
	 * @throws Exception
	 */
	public static int updateClicks(String label_id) throws Exception{
		if (label_id == null || label_id.equals("")) {
			throw new Exception("更新标签时，主键ID不能为空");
		}
		try{
			dao.openDatabase();
			String sqlStr = "update fastlabel set clicks=clicks+1 where id='"+label_id+"'";
			return dao.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("更新标签使有次数出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
	/**
	 * 删除快捷标签
	 * @param labelModel
	 * @return
	 * @throws Exception
	 */
	public static int deleteFastLabel(FastLabelModel labelModel)
			throws Exception {
		if (labelModel.getId() == null || labelModel.getId().equals("")) {
			throw new Exception("删除标签时，主键ID不能为空");
		}
		try{
			dao.openDatabase();
			String sqlStr = "delete from  fastlabel where id='"+labelModel.getId()+"'";
			return dao.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("新增快捷标签出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
}
