package cn.com.hnisi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * SQLite数据库操作类
 * 
 * @author FengGeGe
 * 
 */
public class SQLiteDatabase {
	final static Logger log=Logger.getLogger(SQLiteDatabase.class.getName());
	/**
	 * 连接数据库语句
	 */
	final static String CONNECTION_STR = "jdbc:sqlite:db/AppData.db";
	Connection conn = null;
	Statement stmt = null;
	PreparedStatement ps = null;
	
	
	private static class SQLiteDatabaseHolder {
		private static final SQLiteDatabase INSTANCE = new SQLiteDatabase();
	}

	/**
	 * 获取数据库操作实例对象
	 * @return
	 */
	public static final SQLiteDatabase getInstance() {
		return SQLiteDatabaseHolder.INSTANCE;
	}

	public SQLiteDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");// 加载驱动
		} catch (Exception e) {
			log.error("数据库驱动加载失败，原因："+ e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * 打开数据库连接
	 */
	public void openDatabase() {
		try {
			if (conn == null || conn.isClosed()) {
				conn = DriverManager.getConnection(CONNECTION_STR);
				conn.setAutoCommit(false);				
				stmt = conn.createStatement();
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
	}

	/**
	 * 单条执行数据更新和插入
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public int executeUpdate(String sql) throws Exception {
		try {
			//log.info(sql);
			stmt.executeUpdate(sql);
			conn.commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new Exception("SQL异常：数据库单条执行更新或插入出错，原因：" + e.getMessage());
		}
	}

	/**
	 * 批量执行数据更新和插入
	 * 
	 * @param sqls
	 * 
	 * @return
	 * @throws Exception
	 */
	public int executeUpdate(List<String> sqls) throws Exception {
		try {
			for (String sql : sqls) {
				stmt.executeUpdate(sql);
			}
			conn.commit();
			return 1;
		} catch (Exception e) {
			log.error("执行数据库脚本出错："+e.getMessage());
			e.printStackTrace();
			throw new Exception("SQL异常：数据库批量执行更新或插入出错，原因：" + e.getMessage());
		}
	}

	/**
	 * 判断表是否存在
	 * 
	 * @param tableName
	 *            = 表名
	 * @return 如果已存在则返回true
	 */
	public boolean tableIsExist(String tableName) throws Exception {
		ResultSet rsTables = null;
		try {
			rsTables = conn.getMetaData()
					.getTables(null, null, tableName, null);
			if (rsTables.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error("执行数据库脚本出错："+e.getMessage());
			throw new Exception("SQL异常：检查数据库表是否存在时出错，原因：" + e.getMessage());
		} finally {
			if (rsTables != null) {
				rsTables.close();
			}
		}
	}

	/**
	 * 执行数据库查询
	 * 
	 * @param sql
	 *            查询SQL语句
	 * @return
	 * @throws Exception
	 */
	public ResultSet executeQuery(String sql) throws Exception {
		try {
			//log.info(sql);
			return stmt.executeQuery(sql);
		} catch (Exception e) {
			log.error("执行数据库脚本出错："+e.getMessage());
			e.printStackTrace();
			throw new Exception("SQL异常：数据库执行查询出错，原因：" + e.getMessage());
		}
	}
	
	/**
	 * 检查是否已存在记录
	 * @param queryStr
	 * @return
	 * @throws Exception
	 */
	public boolean isExist(String queryStr) throws Exception {
		ResultSet rs=null;
		try{
			rs = stmt.executeQuery(queryStr);
			while(rs.next()){
				return true;
			}
		}catch(Exception e){
			log.error("执行数据库脚本出错："+e.getMessage());
			throw new Exception("SQL异常：数据库执行查询出错，原因：" + e.getMessage());
		}finally{
			if(rs!=null)
				rs.close();
		}
		return false;
	}

	/**
	 * 防止sql注入
	 * 
	 * @param sql
	 * @return
	 */
	public String TransactSQLInjection(String sql) {
		if(sql==null){
			return "";
		}
		return sql.replace("'", "''");
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		try {
			if (stmt != null&& !stmt.isClosed()) {
				stmt.close();
			}
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
