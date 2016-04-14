package com.gochinatv.cdn.config

/**
  * 数据库连接基础信息类
  * <p/>
  * Author   : wangxp
  * <p/>
  * DateTime : 2016/4/11 9:37
  */
abstract class SQLConnConfig()
{
  var host: String = _
  var port: String = _
  var dbName: String = _
  var username: String = _
  var password: String = _

  def connctionUrl: String

  def driverName: String
}
