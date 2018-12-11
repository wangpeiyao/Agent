package cn.com.hnisi.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.hnisi.util.Tool;

/**
 * 初始化系统数据库
 * 
 * @author FengGeGe
 * 
 */
public class InitEnvironment {
	static Logger log = Logger.getLogger(InitEnvironment.class);
	final static SQLiteDatabase sql = SQLiteDatabase.getInstance();

	/**
	 * 创建应用服务器数据表 servers
	 * 
	 * @throws Exception
	 */
	private static void createServersTable() throws Exception {
	
		sql.openDatabase();
		String createSql = "create table servers(id,groupId,name,serverName,serverIp,"
				+ "domainPort,systemTypeId,middlewareTypeId,domainPath,backUpPath,"
				+ "backupType,appPath,appVerification,agentPath,agentPort,agentPassword,"
				+ "status,remark,username,password,isAdminServer,serverType)";
		if (!sql.tableIsExist("servers")) {
			log.info("Create table servers");
			sql.executeUpdate(createSql);
		}
		sql.close();
	}

	/**
	 * 创建字典表 dicts
	 * 
	 * @throws Exception
	 */
	private static void createDictsTable() throws Exception {
		sql.openDatabase();
		// 字典表dicts
		String createSql = "create table dicts(id,dicName,name,value,status)";
		if (!sql.tableIsExist("dicts")) {
			log.info("创建表:dicts");
			sql.executeUpdate(createSql);
			List<String> sqls = new ArrayList<String>();
			// 系统类型
			sqls.add("insert into dicts(id,dicName,name,value,status) "
					+ "values('" + Tool.getUUID()
					+ "','systemType','Windows','0','1');");
			sqls.add("insert into dicts(id,dicName,name,value,status) "
					+ "values('" + Tool.getUUID()
					+ "','systemType','Aix','1','1');");
			sqls.add("insert into dicts(id,dicName,name,value,status) "
					+ "values('" + Tool.getUUID()
					+ "','systemType','Linux','2','1');");
			// 中间件类型
			sqls.add("insert into  dicts(id,dicName,name,value,status) "
					+ "values('" + Tool.getUUID()
					+ "','middlewareType','WebLogic','0','1');");
			sqls.add("insert into dicts(id,dicName,name,value,status) "
					+ "values('" + Tool.getUUID()
					+ "','middlewareType','WebSphere','1','1');");
			sql.executeUpdate(sqls);
		}
		sql.close();
	}

	/**
	 * 创建应用服务器群组数据表 groups
	 * 
	 * @throws Exception
	 */
	private static void createGroupsTable() throws Exception {
		
		sql.openDatabase();
		// 不能被删除；status默认为1有效,canEdit=0不能编辑,canDelete=0,不能删除，canShow不显示
		String createSql = "create table groups(id,name,status,canDelete,canEdit,canShow,sortNum int(4),remark)";
		if (!sql.tableIsExist("groups")) {
			log.info("创建表:groups");
			sql.executeUpdate(createSql);
			String insertStr = "insert into groups(id,name,status,canDelete,canEdit,canShow,sortNum,remark) values('"
					+ Tool.getUUID()
					+ "','默认','1','0','0','1',999,'默认组，不能删除!');";
			sql.executeUpdate(insertStr);
		}
		sql.close();
	}

	/**
	 * 创建用户数据表users
	 * 
	 * @throws Exception
	 */
	private static void createUsersTable() throws Exception {
		
		sql.openDatabase();
		String createSql = "create table users(id VARCHAR(50),username VARCHAR(500),password VARCHAR(100),name VARCHAR(50),role VARCHAR(50),status INT,remark VARCHAR(500))";
		if (!sql.tableIsExist("users")) {
			log.info("创建表:users");
			sql.executeUpdate(createSql);
			String insertStr = "insert into users(id,username,password,name) values('"
					+ Tool.getUUID()
					+ "','admin','"
					+ Tool.EncoderByMd5("admin") + "','超级管理员');";
			sql.executeUpdate(insertStr);
		}

		sql.close();
	}

	/**
	 * 创建集群表
	 * 
	 * @throws Exception
	 */
	public static void createClusterServer() throws Exception {

		sql.openDatabase();
		String createSql = "create table ClusterServices(id VARCHAR(50),sid VARCHAR(50),name VARCHAR(255),adminUrl VARCHAR(500),stagePath VARCHAR(500),isUploadFile INT,isAutoStart INT,port INT,userName VARCHAR(500),passWord VARCHAR(100),status INT)";
		if (!sql.tableIsExist("ClusterServices")) {
			log.info("创建表:clusterServer");
			sql.executeUpdate(createSql);
		}
		sql.close();
	}


	/**
	 * 快捷标签表
	 * 
	 * @throws Exception
	 */
	public static void createFastLabel() throws Exception {
		sql.openDatabase();
		String createSql = "create table fastlabel(id VARCHAR(50),name VARCHAR(100),lasttime VARCHAR(20),clicks INT)";
		if (!sql.tableIsExist("fastlabel")) {
			log.info("创建表:fastlabel");
			sql.executeUpdate(createSql);
		}
		sql.close();
	}
	
	/**
	 * 快捷标签与应用对照表
	 * @throws Exception
	 */
	public static void createFastLabelServer() throws Exception {
		sql.openDatabase();
		String createSql = "create table fastlabel_server(id VARCHAR(50),label_id VARCHAR(50),server_id name VARCHAR(50))";
		if (!sql.tableIsExist("fastlabel_server")) {
			log.info("创建表:fastlabel_server");
			sql.executeUpdate(createSql);
		}
		sql.close();
	}
	/**
	 * 删除表
	 * 
	 * @param tableName
	 * @throws Exception
	 */
	public static void dropTable(String tableName) throws Exception {
		log.info("Drop table " + tableName);
		sql.openDatabase();
		String createSql = "drop table " + tableName;
		sql.executeUpdate(createSql);
		sql.close();
	}

	public static void updateData() throws Exception{
		sql.openDatabase();
		String createSql = "update fastlabel set clicks=1 where id in ('1dd42d78-dcdb-43e7-abf0-9852e01ae390','48a33054-7a57-4e90-8c71-e828f714fa19','e493792b-6f9c-4d2a-9fc7-50a61a1e715f','ba0ef3f5-5a49-4c71-9caa-9ed408a7b677')";
		sql.executeUpdate(createSql);
		sql.close();
	}
	public static void main(String[] args) {
		try {
			//updateData();
		    //alterTable();
			//dropTable("fastlabel");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public static void alterTable() {
		String alterSql = "ALTER TABLE fastlabel add COLUMN clicks int;";
		sql.openDatabase();
		try {
			sql.executeUpdate(alterSql);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		sql.close();
	}

	/**
	 * 初始化数据库参数
	 * 
	 * @throws Exception
	 */
	public static void Init() throws Exception {
		createDictsTable();//字典表
		createUsersTable();//用户表
		createGroupsTable();//分组表
		createServersTable();//应用表
		createClusterServer();//WebLogic集群表
		createFastLabel();//快捷标签表
		createFastLabelServer();//标签与应用对照表
		log.info("成功加载系统数据.");
	}
}
