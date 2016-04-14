package com.gochinatv.cdn.config

/** MySQL数据库配置对象
  * <p/>
  * Author   : wangxp
  * <p/>
  * DateTime : 2016/4/11 9:33
  */
class MySQLConnConfig() extends SQLConnConfig()
{
  override def connctionUrl: String = s"jdbc:mysql://$host:$port/$dbName?user=$username&password=$password&useUnicode=true&characterEncoding=utf-8"

  override def driverName: String = "com.mysql.jdbc.Driver"
}
