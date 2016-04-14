package com.gochinatv.cdn

import java.io.InputStream
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.{Calendar, Properties}

import com.gochinatv.cdn.config.{HiveQLConnConfig, SQLConnConfig, MySQLConnConfig}
import com.gochinatv.cdn.constant.{DBOperType, OstarProp}
import com.gochinatv.cdn.util.DBExecuteUtil
import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  * Ostar日志分区信息同步
  * <p/>
  * Author   : wangxp
  * <p/>
  * DateTime : 2016/4/8 13:42
  */
object OstarPartition
{
  val logger = LoggerFactory.getLogger(getClass)

  /**
    * 日志日期分区格式
    */
  val sdf = new SimpleDateFormat("yyyyMMdd")

  def main(args: Array[String]): Unit =
  {
    //获取日期分区信息
    val datePartition = if (args.length == 0 || args(0).trim().length == 0) sdf.format(Calendar.getInstance().getTime) else args(0).trim()
    logger.info(s"The date partition value is $datePartition")

    //加载属性文件配置
    val prop: Properties = loadProperties

    //获取管理库MYSQL配置
    val mysqlConnConfig = new MySQLConnConfig
    fillSQLConnConfig(mysqlConnConfig, OstarProp.MNG_DB_CONF_PREFIX, prop)

    //查询MYSQL数据库，获取分区键信息
    val domainNames = DBExecuteUtil.execute[String](mysqlConnConfig, "select domain_name from domain_info", (rs: ResultSet) =>
    {
      val results = ArrayBuffer[String]()
      while (rs.next())
      {
        results += rs.getString(1)
      }

      results
    })

    //获取HIVE数据库连接配置
    val hiveConnConfig = new HiveQLConnConfig
    fillSQLConnConfig(hiveConnConfig, OstarProp.HIVE_DB_CONF_PREFIX, prop)

    //HIVE分区表根路径
    val cdnLogRootPath = prop.getProperty(OstarProp.CDN_LOG_ROOT_PATH_KEY)

    //执行增加HIVE分区操作
    domainNames.foreach((domainName: String) =>
    {
      val addPartitionSQL = s"ALTER TABLE ostar_cdn_log ADD IF NOT EXISTS PARTITION (log_date='$datePartition', domain_name='$domainName') LOCATION '$cdnLogRootPath/$datePartition/$domainName'"
      DBExecuteUtil.execute[String](hiveConnConfig, addPartitionSQL, dbOperType = DBOperType.OP_DDL)

      logger.info(s"Added partition info => $addPartitionSQL")
    })
  }


  /**
    * 加载默认配置文件属性
    *
    * @return 配置文件属性对象
    */
  def loadProperties: Properties =
  {
    val prop = new Properties()

    val in: InputStream = OstarPartition.getClass.getClassLoader.getResourceAsStream("config.properties")
    try
    {
      prop.load(in)
    }
    finally
    {
      if (null != in) in.close()
    }

    prop
  }

  /**
    * 获取数据库连接信息
    *
    * @param sqlConnConfig 具体数据连接信息对象
    * @param confPrefix    配置前缀
    * @param prop          配置信息对象
    */
  def fillSQLConnConfig(sqlConnConfig: SQLConnConfig, confPrefix: String, prop: Properties) =
  {
    sqlConnConfig.host = prop.getProperty(confPrefix + OstarProp.DB_HOST_KEY)
    sqlConnConfig.port = prop.getProperty(confPrefix + OstarProp.DB_PORT_KEY)
    sqlConnConfig.dbName = prop.getProperty(confPrefix + OstarProp.DB_NAME_KEY)
    sqlConnConfig.username = prop.getProperty(confPrefix + OstarProp.DB_USER_KEY)
    sqlConnConfig.password = prop.getProperty(confPrefix + OstarProp.DB_PASSWORD_KEY)
  }
}
