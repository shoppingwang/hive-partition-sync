package com.gochinatv.cdn.util

import java.sql.{DriverManager, ResultSet}

import com.gochinatv.cdn.config.SQLConnConfig
import com.gochinatv.cdn.constant.DBOperType._

import scala.collection.mutable.ArrayBuffer

/**
  * 数据库SQL执行
  * <p/>
  * Author   : wangxp
  * <p/>
  * DateTime : 2016/4/11 10:34
  */
object DBExecuteUtil
{
  def execute[T](sqlConnConfig: SQLConnConfig
                 , executeSql: String
                 , handleRS: (ResultSet) => ArrayBuffer[T] = (rs: ResultSet) => ArrayBuffer[T]()
                 , dbOperType: DBOperType = OP_DML
                     ): ArrayBuffer[T] =
  {
    Class.forName(sqlConnConfig.driverName)

    val driverUrl: String = sqlConnConfig.connctionUrl
    val conn = DriverManager.getConnection(driverUrl, sqlConnConfig.username, sqlConnConfig.password)
    try
    {
      if (OP_DML == dbOperType)
      {
        val statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        val rs = statement.executeQuery(executeSql)

        handleRS(rs)
      }
      else
      {
        val statement = conn.createStatement()
        statement.execute(executeSql)

        handleRS(null)
      }
    }
    finally
    {
      if (null != conn) conn.close()
    }
  }
}
