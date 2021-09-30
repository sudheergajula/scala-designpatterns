package com.designpatterns.persistence.dao.entity1

object App {

  def main(args: Array[String]): Unit = {
    val dao = new UserDao
    val user = dao.createUser("Sudheer", "Gajula", 32, "male")
    println(user)
    val user1 = dao.getUserByName("Sudheer", "Gajula")
    println(user1)
  }


}
