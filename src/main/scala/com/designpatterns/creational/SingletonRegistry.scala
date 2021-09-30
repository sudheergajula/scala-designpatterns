package com.designpatterns.creational

object SingletonRegistry {

  val userMap = scala.collection.mutable.HashMap[String, String]()

  def addUserToCache(userId: String, userName: String): Unit = {
    userMap.put(userId, userName)
  }

  def removeUser(userId: String): Unit = {
    userMap.remove(userId)
  }

  def getUser(userId: String): Option[String] = {
    userMap.get(userId)
  }

  def printUsers(): Unit = {
    println(userMap.values.toList.mkString(","))
  }


}
