package com.gochinatv.cdn.config

/** HiveQL数据库配置对象
  * <p/>
  * Author   : wangxp
  * <p/>
  * DateTime : 2016/4/11 9:33
  */
class HiveQLConnConfig() extends SQLConnConfig()
{
  override def connctionUrl: String = s"jdbc:hive2://$host:$port/$dbName?user=$username&password=$password&useUnicode=true&characterEncoding=utf-8"

  override def driverName: String = "org.apache.hive.jdbc.HiveDriver"
}
