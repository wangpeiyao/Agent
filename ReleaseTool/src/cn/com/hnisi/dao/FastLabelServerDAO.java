package cn.com.hnisi.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.util.Tool;

public class FastLabelServerDAO {
	static Logger log = Logger.getLogger(FastLabelServerDAO.class);
	final static SQLiteDatabase dao = SQLiteDatabase.getInstance();
	final static String sqlStr="select id,label_id,server_id from fastlabel_server";
	
	/**
	 * 根据快捷标签ID集合，查询应用ID集合
	 * @param labelIds
	 * @return
	 * @throws Exception
	 */
	public static List<String> getServerIds(Object[] labelIds) throws Exception{
		if(labelIds==null || labelIds.length<=0){
			throw new Exception("查询条件：标签ID为空");
		}
		ResultSet rs = null;
		List<String> server_ids=null;
		try {
			//log.info("获取标签应用");
			String idStr = "";
			for (Object id : labelIds) {
				idStr += ",'" + id.toString() + "'";
			}
			
			idStr=idStr.toString().substring(1);
			dao.openDatabase();
			rs = dao.executeQuery(sqlStr + " where label_id in ("+idStr+")");
			server_ids=new ArrayList<String>();
			while (rs.next()) {
				server_ids.add(rs.getString("server_id"));
			}
			return server_ids;
		} catch (Exception e) {
			throw new Exception("获取快捷标签应用ID出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 根据快捷标签ID，查找应用ID
	 * @param labelId
	 * @return
	 * @throws Exception
	 */
	public static List<String> getServerIds(String labelId) throws Exception{
		if(labelId==null){
			throw new Exception("查询条件：标签ID为空");
		}
		ResultSet rs = null;
		List<String> server_ids=null;
		try {
			//log.info("获取标签应用");
			dao.openDatabase();
			rs = dao.executeQuery(sqlStr + " where label_id in ('"+labelId+"')");
			server_ids=new ArrayList<String>();
			while (rs.next()) {
				server_ids.add(rs.getString("server_id"));
			}
			return server_ids;
		} catch (Exception e) {
			throw new Exception("获取快捷标签应用ID出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 插入对照表
	 * @param labelId
	 * @param serverIds
	 * @return
	 * @throws Exception
	 */
	public static int insertLabelServer(String labelId,String[] serverIds) throws Exception{
		if (labelId == null || serverIds==null) {
			throw new Exception("新增快捷标签应用出错，标签ID和应用ID不能为空");
		}
		try{
			dao.openDatabase();
			List<String> sqls=new ArrayList<String>();
			for(String sid:serverIds){
				String sqlStr = "insert into fastlabel_server(id,label_id,server_id) values('"+Tool.getUUID()+"','"+ labelId + "','" + sid +"')";
				sqls.add(sqlStr);
			}
			return dao.executeUpdate(sqls);
		} catch (Exception e) {
			throw new Exception("新增快捷标签出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 删除标签应用
	 * @param labelId
	 * @return
	 * @throws Exception
	 */
	public static int deleteLabelServer(String labelId) throws Exception{
		if (labelId==null) {
			throw new Exception("删除标签时，主键ID不能为空");
		}
		try{
			//log.info("同步删除标签应用对照表");
			dao.openDatabase();
			String sqlStr = "delete from fastlabel_server where label_Id='"+labelId+"'";
			return dao.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("删除快捷标签出错：" + e.getMessage());
		} finally {
			dao.close();
		}
	}
}
