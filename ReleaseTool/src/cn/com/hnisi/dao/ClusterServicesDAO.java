package cn.com.hnisi.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.model.ClusterServicesModel;

/**
 * 数据库表：ClusterServer 操作类
 * 
 * @author FengGeGe
 * 
 */
public class ClusterServicesDAO {
	static Logger log = Logger.getLogger(ClusterServicesDAO.class);
	final static SQLiteDatabase sql = SQLiteDatabase.getInstance();
	final static String TABLE_NAME = "ClusterServices";

	/**
	 * 根据SID获取所属集群记录
	 * 
	 * @param serverId
	 * @return
	 * @throws Exception
	 */
	public static List<ClusterServicesModel> getClusterServerModelList(
			String serverId){
		List<ClusterServicesModel> clusterServerList = new ArrayList<ClusterServicesModel>();
		ResultSet rs= null;
		try {
			sql.openDatabase();
			String sqlStr = "select * from " + TABLE_NAME + " where sid='"
					+ serverId + "'";
			rs = sql.executeQuery(sqlStr);
			while (rs.next()) {
				clusterServerList.add(getClusterModelByResultSet(rs));
			}
			return clusterServerList;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			sql.close();
		}
		return null;
	}

	/**
	 * 根据ID获取ClusterServer
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static ClusterServicesModel getClusterServerMode(String id)
			throws Exception {
		ResultSet rs = null;
		try {
			sql.openDatabase();
			String sqlStr = "select * from " + TABLE_NAME + " where id='" + id
					+ "'";
			rs = sql.executeQuery(sqlStr);
			while (rs.next()) {
				return getClusterModelByResultSet(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			sql.close();
		}
		return null;
	}

	/**
	 * 获取ClusterServerModel实例
	 * 
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public static ClusterServicesModel getClusterModelByResultSet(ResultSet rs)
			throws Exception {
		ClusterServicesModel server = null;
		if (rs != null) {
			// 列名:
			// id,sid,serverName,adminUrl,stagePath,isUploadFile,port,userName,passWord
			server = new ClusterServicesModel();
			server.setId(rs.getString("id"));
			server.setSid(rs.getString("sid"));
			server.setName(rs.getString("name"));
			server.setAdminUrl(rs.getString("adminUrl"));
			server.setStagePath(rs.getString("stagePath"));
			server.setIsUploadFile(rs.getInt("isUploadFile"));// 0-不同步；1-同步
			server.setIsAutoStart(rs.getInt("isAutoStart"));// 0-不同步；1-同步
			server.setPort(rs.getInt("port"));
			server.setUserName(rs.getString("userName"));
			server.setPassWord(rs.getString("passWord"));
			server.setStatus(rs.getInt("status"));
		}

		return server;
	}

	private static String getInsertSql(ClusterServicesModel csm)
			throws Exception {
		if (csm.getId() == null || csm.getId().trim().length() <= 0) {
			throw new Exception("插入集群表记录主键ID不能为空");
		}
		String insertSql = "insert into "
				+ TABLE_NAME
				+ "(id,sid,name,adminUrl,"
				+ "stagePath,isUploadFile,isAutoStart,port,userName,passWord,status) "
				+ "values(" + "'" + sql.TransactSQLInjection(csm.getId())
				+ "'," + "'" + sql.TransactSQLInjection(csm.getSid()) + "',"
				+ "'" + sql.TransactSQLInjection(csm.getName()) + "'," + "'"
				+ sql.TransactSQLInjection(csm.getAdminUrl()) + "'," + "'"
				+ sql.TransactSQLInjection(csm.getStagePath()) + "',"
				+ csm.getIsUploadFile() + "," + csm.getIsAutoStart() + ","
				+ csm.getPort() + "," + "'"
				+ sql.TransactSQLInjection(csm.getUserName()) + "'," + "'"
				+ sql.TransactSQLInjection(csm.getPassWord()) + "',"
				+ csm.getStatus() + ");";

		return insertSql;
	}

	private static String getUpdateSql(ClusterServicesModel csm)
			throws Exception {
		if (csm.getId() == null || csm.getId().trim().length() <= 0) {
			throw new Exception("更新集群表记录主键ID不能为空");
		}
		String updateSql = "update " + TABLE_NAME + " set " + "sid='"
				+ csm.getSid() + "'," + "name='"
				+ sql.TransactSQLInjection(csm.getName()) + "'," + "adminUrl='"
				+ sql.TransactSQLInjection(csm.getAdminUrl()) + "',"
				+ "stagePath='" + sql.TransactSQLInjection(csm.getStagePath())
				+ "'," + "isUploadFile=" + csm.getIsUploadFile() + ","
				+ "isAutoStart=" + csm.getIsAutoStart() + "," + "port="
				+ csm.getPort() + "," + "userName='"
				+ sql.TransactSQLInjection(csm.getUserName()) + "',"
				+ "passWord='" + sql.TransactSQLInjection(csm.getPassWord())
				+ "'," + "status=" + csm.getStatus() + "" + " where id='"
				+ csm.getId() + "';";

		return updateSql;
	}

	private static String getDeleteSql(String id) throws Exception {
		if (id == null || id.trim().length() <= 0) {
			throw new Exception("删除集群表记录主键ID不能为空");
		}
		return "delete from " + TABLE_NAME + " where id='" + id + "'";
	}

	/**
	 * 插入ClusterServer
	 * 
	 * @param server
	 * @return
	 */
	public static int insertClusterServer(ClusterServicesModel csm) {
		sql.openDatabase();
		try {
			return sql.executeUpdate(getInsertSql(csm));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			sql.close();
		}
	}

	/**
	 * 更新ClusterServer
	 * 
	 * @param server
	 * @return
	 */
	public static int updateClusterServer(ClusterServicesModel csm) {
		sql.openDatabase();
		try {
			return sql.executeUpdate(getUpdateSql(csm));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			sql.close();
		}
	}

	/**
	 * 删除ClusterServer
	 * 
	 * @param csm
	 * @return
	 */
	public static int deleteClusterServer(ClusterServicesModel csm) {
		sql.openDatabase();
		try {
			return sql.executeUpdate(getDeleteSql(csm.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			sql.close();
		}
	}

	public static int deleteClusterServerBySID(String sid) {
		sql.openDatabase();
		try {
			return sql.executeUpdate("delete from " + TABLE_NAME
					+ " where sid='" + sid + "'");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			sql.close();
		}
	}

	/**
	 * 插入或更新:如果已存在则更新,不存在则插入.
	 * 
	 * @param server
	 * @return
	 */
	public static int insertOrUpdate(ClusterServicesModel csm) {
		sql.openDatabase();
		try {
			if (sql.isExist("select * from " + TABLE_NAME + " where id='"
					+ csm.getId() + "'")) {
				// 如果已存在则更新
				return updateClusterServer(csm);
			} else {
				// 不存在则插入
				return insertClusterServer(csm);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
