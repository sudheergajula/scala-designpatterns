package com.designpatterns.creational

object SingletonRegistry {

  private val userMap = scala.collection.mutable.HashMap[String, String]()

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
    println(userMap.values.toList.mkString("=>"))
  }

}

object App {
  def main(args: Array[String]): Unit = {
    SingletonRegistry.addUserToCache("1", "Sudheer")
    SingletonRegistry.addUserToCache("2", "foo")
    SingletonRegistry.addUserToCache("3", "bar")
    SingletonRegistry.printUsers()
  }
}
