package com.gochinatv.cdn.constant

/**
  * 数据库操作类型
  * <p/>
  * Author   : wangxp
  * <p/>
  * DateTime : 2016/4/11 14:02
  */
object DBOperType extends Enumeration
{
  type DBOperType = Value

  val OP_DDL = Value

  val OP_DML = Value
}
