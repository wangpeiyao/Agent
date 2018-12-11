package cn.com.hnisi.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import cn.com.hnisi.database.SQLiteDatabase;
import cn.com.hnisi.model.ServerModel;

public class ServerModelDAO {
	final static SQLiteDatabase sql = SQLiteDatabase.getInstance();
	public final static String SELECT_ALL = "select * from (select a.id,a.groupId,b.name groupName,b.sortNum groupSortNum,a.name,a.serverName,a.serverIp,a.domainPort,"
			+ "(select c.name from dicts c where c.id=a.systemTypeId) systemType,a.systemTypeId,"
			+ "(select d.name from dicts d where d.id=a.middlewareTypeId) middlewareType,a.middlewareTypeId,"
			+ "appPath,appVerification,domainPath,backUpPath,backupType,a.status,agentPath,agentPort,"
			+ "agentPassword,releaseType,a.remark,a.username,a.password,a.isAdminServer,a.serverType"
			+ " from servers a,groups b where a.groupId=b.id order by groupSortNum desc)";

	/**
	 * 获取所有应用
	 * @return
	 * @throws Exception
	 */
	public static List<ServerModel> getAllServerModel() throws Exception {
		List<ServerModel> serverList = new ArrayList<ServerModel>();
		String sqlStr = SELECT_ALL;
		ResultSet rs = null;
		sql.openDatabase();
		try {
			// 获取需要发布的应用
			rs = sql.executeQuery(sqlStr);
			// 生成控件
			while (rs.next()) {
				ServerModel server = ServerModelDAO
						.getServerModelByResultSet(rs);
				serverList.add(server);// 保存查询结果
			}
		} catch (Exception e) {
			throw new Exception("获取应用集合出错：" + e.getMessage());
		} finally {
			sql.close();
		}
		return serverList;
	}

	/**
	 * 根据分组ID查找应用
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public static List<ServerModel> getServerModelListByGroupId(String groupId) throws Exception {
		if(groupId==null){
			return null;
		}
		List<ServerModel> serverList = new ArrayList<ServerModel>();
		String sqlStr = SELECT_ALL;
		if(groupId.trim().length()>0){
			sqlStr+=" where groupId ='"+groupId+"'";
		}
		ResultSet rs = null;
		sql.openDatabase();
		try {
			// 获取需要发布的应用
			rs = sql.executeQuery(sqlStr);
			// 生成控件
			while (rs.next()) {
				ServerModel server = ServerModelDAO
						.getServerModelByResultSet(rs);
				serverList.add(server);// 保存查询结果
			}
		} catch (Exception e) {
			throw new Exception("获取应用集合出错：" + e.getMessage());
		} finally {
			sql.close();
		}
		return serverList;
	}
	/**
	 * 根据ids获取对应的应用
	 * 
	 * @param ids
	 * @return
	 */
	public static List<ServerModel> getServerModelListByIds(String[] ids) {
		if (ids == null || ids.length <= 0) {
			return null;
		}
		List<ServerModel> serverList = new ArrayList<ServerModel>();
		String idStr = "";
		for (String obj : ids) {
			idStr += ",'" + obj.toString() + "'";
		}
		idStr=idStr.toString().substring(1);

		String sqlStr = ServerModelDAO.SELECT_ALL + " where id in ("
				+ idStr + ")";
		sqlStr += " order by groupName,serverIp,domainPort desc";

		ResultSet rs = null;
		try {
			sql.openDatabase();
			// 获取需要发布的应用
			rs = sql.executeQuery(sqlStr);
			// 生成控件
			while (rs.next()) {
				serverList.add(getServerModelByResultSet(rs));
			}
			return serverList;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			sql.close();
		}
		return null;
	}

	/**
	 * 根据Server的ID获取ServerModel实例
	 * 
	 * @param serverId
	 * @return
	 * @throws Exception
	 */
	public static ServerModel getServerModelById(String serverId)
			throws Exception {
		ResultSet rs = null;
		try {
			sql.openDatabase();
			String sqlStr = SELECT_ALL + " where id='" + serverId + "'";
			rs = sql.executeQuery(sqlStr);
			while (rs.next()) {
				return getServerModelByResultSet(rs);
			}
		} catch (Exception e) {
			throw new Exception("获取应用出错：" + e.getMessage());
		} finally {
			sql.close();
		}
		return null;
	}

	/**
	 * 获取ServerModel实例
	 * 
	 * @param rs
	 * @return
	 * @throws Exception
	 */
	public static ServerModel getServerModelByResultSet(ResultSet rs)
			throws Exception {
		ServerModel server = null;
		if (rs != null) {
			server = new ServerModel();
			server.setId(rs.getString("id"));// 主键ID
			server.setGroupId(rs.getString("groupId"));// 分组的ID
			server.setGroupName(rs.getString("groupName"));// 分组名称
			server.setName(rs.getString("name"));// 名称
			server.setServerName(rs.getString("serverName"));// 服务名称(WAS)
			server.setServerIp(rs.getString("serverIp"));// 服务器IP
			server.setDomainPort(rs.getString("domainPort"));// 应用端口号
			server.setSystemTypeId(rs.getString("systemTypeId"));// 系统类型ID
			server.setSystemType(rs.getString("systemType"));// 系统类型名称
			server.setMiddlewareType(rs.getString("middlewareType"));// 中间件类型名称
			server.setMiddlewareTypeId(rs.getString("middlewareTypeId"));// 中间件类型ID
			server.setDomainPath(rs.getString("domainPath"));// 应用路径
			server.setReleaseType(rs.getString("releaseType"));// 发布类型：0
																// -发布应用；1-文件上传
			server.setAppPath(rs.getString("appPath"));// 应用部署存放路径
			server.setAppVerification(rs.getString("appVerification"));// 应用验证地址
			server.setBackUpPath(rs.getString("backUpPath"));// 备份路径
			server.setBackupType(rs.getString("backupType"));// 备份类型：0-复制文件夹；1-压缩
			server.setAgentPath(rs.getString("agentPath"));// agent存放路径（暂时没用到）
			server.setAgentPort(rs.getString("agentPort"));// agent端口号
			server.setAgentPassword(rs.getString("agentPassword"));// agent密码
			server.setUsername(rs.getString("username"));// 中间件应用管理用户
			server.setPassword(rs.getString("password"));// 中间件应用管理密码
			server.setStatus(rs.getString("status"));// 数据记录状态；0-无效；1-有效
			server.setRemark(rs.getString("remark"));// 描述备注
			server.setIsAdminServer(rs.getString("isAdminServer"));
			// 是否为集群总控制：0-普通应用；1-管理总控制；2-集群服务; 
			if(rs.getString("serverType")==null||
					(!rs.getString("serverType").equals("1")&&
					!rs.getString("serverType").equals("2"))){
					server.setServerType("0");
			}else{
				server.setServerType(rs.getString("serverType"));
			}
			
		}
		return server;
	}

	/**
	 * 更新Server
	 * 
	 * @param server
	 * @return
	 * @throws Exception
	 */
	public static int updateServer(ServerModel server) throws Exception {
		if (server.getId() == null || server.getId().equals("")) {
			throw new Exception("新增应用服务时，主键ID不能为空");
		}
		if (server.getServerIp() == null || server.getServerIp().equals("")) {
			throw new Exception("新增应用服务时，服务器ID地址不能为空");
		}
		if (server.getAgentPort() == null || server.getAgentPort().equals("")) {
			throw new Exception("新增应用服务时，Agent端口号不能为空");
		}
		if (server.getAppPath() == null || server.getAppPath().equals("")) {
			throw new Exception("新增应用服务时，应用部署路径不能为空");
		}
		try {
			String updateSql = "update servers set " + "groupId='"
					+ server.getGroupId() + "'," + "serverIp='"
					+ server.getServerIp() + "'," + "name='" + server.getName()
					+ "'," + "serverName='" + server.getServerName() + "',"
					+ "domainPort='" + server.getDomainPort() + "',"
					+ "systemTypeId='" + server.getSystemTypeId() + "',"
					+ "middlewareTypeId='" + server.getMiddlewareTypeId()
					+ "'," + "appPath='" + server.getAppPath() + "',"
					+ "appVerification='" + server.getAppVerification() + "',"
					+ "domainPath='" + server.getDomainPath() + "',"
					+ "backUpPath='" + server.getBackUpPath() + "',"
					+ "backupType='" + server.getBackupType() + "',"
					+ "status='" + server.getStatus() + "'," 
					+ "agentPath='"+ server.getAgentPath() + "'," 
					+ "agentPort='"+ server.getAgentPort() + "'," 
					+ "agentPassword='"+ server.getAgentPassword() + "'," 
					+ "releaseType='"+ server.getReleaseType() + "'," 
					+ "username='"+ server.getUsername() + "'," 
					+ "password='"+ server.getPassword() + "'," 
					+ "remark='"+ server.getRemark() + "',"
					+ "isAdminServer='"+ server.getIsAdminServer()+ "',"
					+ "serverType='"+ server.getServerType()+"'"
					+ " where id='"
					+ server.getId() + "';";
			sql.openDatabase();
			return sql.executeUpdate(updateSql);
		} catch (Exception e) {
			throw new Exception("更新Server出错：" + e.getMessage());
		} finally {
			sql.close();
		}
	}

	/**
	 * 新增Server
	 * 
	 * @param server
	 * @return
	 * @throws Exception
	 */
	public static int insertServer(ServerModel server) throws Exception {
		if (server.getId() == null || server.getId().equals("")) {
			throw new Exception("新增应用服务时，主键ID不能为空");
		}
		try {
			String sqlStr = "insert into servers(id,groupId,name,serverName,serverIp,domainPort,"
					+ "systemTypeId,middlewareTypeId,domainPath,backUpPath,backupType,"
					+ "appPath,appVerification,agentPort,agentPassword,releaseType,"
					+ "status,remark,username,password,isAdminServer,serverType)"
					+ " values ('"
					+ server.getId()
					+ "','"
					+ sql.TransactSQLInjection(server.getGroupId())
					+ "','"
					+ sql.TransactSQLInjection(server.getName())
					+ "','"
					+ sql.TransactSQLInjection(server.getServerName())
					+ "','"
					+ sql.TransactSQLInjection(server.getServerIp())
					+ "','"
					+ sql.TransactSQLInjection(server.getDomainPort())
					+ "','"
					+ sql.TransactSQLInjection(server.getSystemTypeId())
					+ "','"
					+ sql.TransactSQLInjection(server.getMiddlewareTypeId())
					+ "','"
					+ sql.TransactSQLInjection(server.getDomainPath())
					+ "','"
					+ sql.TransactSQLInjection(server.getBackUpPath())
					+ "','"
					+ sql.TransactSQLInjection(server.getBackupType())
					+ "','"
					+ sql.TransactSQLInjection(server.getAppPath())
					+ "','"
					+ sql.TransactSQLInjection(server.getAppVerification())
					+ "','"
					+ sql.TransactSQLInjection(server.getAgentPort())
					+ "','"
					+ server.getAgentPassword()
					+ "','"
					+ server.getReleaseType()
					+ "','"
					+ sql.TransactSQLInjection("1")
					+ "','"
					+ sql.TransactSQLInjection("remark")
					+ "','"
					+ sql.TransactSQLInjection(server.getUsername())
					+ "','"
					+ sql.TransactSQLInjection(server.getPassword())
					+ "','"
					+ sql.TransactSQLInjection(server.getIsAdminServer())
					+ "','"
					+ sql.TransactSQLInjection(server.getServerType())
					+ "');";
			sql.openDatabase();
			String queryStr = "select * from servers where serverIp='"
					+ sql.TransactSQLInjection(server.getServerIp()) + "'"
					+ " and domainPort='"
					+ sql.TransactSQLInjection(server.getDomainPort()) + "'";
			if (sql.isExist(queryStr)) {
				throw new Exception("保存失败，已存在相同的IP和端口应用配置!");
			}
			return sql.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("新增Server出错：" + e.getMessage());
		} finally {
			sql.close();
		}

	}

	/**
	 * 删除Servers
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static int deleteServer(String id) throws Exception {
		if (id == null || id.trim().length() <= 0) {
			throw new Exception("删除Servers记录时，id不能为空");
		}
		try {
			sql.openDatabase();
			String sqlStr = "delete from servers where id='" + id + "';";
			return sql.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("删除Servers记录出错：" + e.getMessage());
		} finally {
			sql.close();
		}
	}

	/**
	 * 批量删除Servers
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static int deleteServer(List<String> idList) throws Exception {
		if (idList == null || idList.size() <= 0) {
			throw new Exception("删除Servers记录时，id不能为空");
		}
		try {
			String ids = "";
			for (int i = 0; i < idList.size();) {
				ids += "'" + idList.get(i) + "'";
				i++;
				// 判断是否为最后一个，最后一个不追加“,”
				if (i < idList.size()) {
					ids += ",";
				}
			}
			sql.openDatabase();
			String sqlStr = "delete from servers where id in (" + ids + ")";
			return sql.executeUpdate(sqlStr);
		} catch (Exception e) {
			throw new Exception("批量删除Servers记录出错：" + e.getMessage());
		} finally {
			sql.close();
		}
	}

}
