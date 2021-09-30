package com.designpatterns.persistence.dao.entity2

import com.designpatterns.persistence.dao.entity.User

object App {

  def main(args: Array[String]): Unit = {
    val dao = new UserDao
    val user = new User
    val dbuser= user
      .setFirstName("Sudheer")
      .setLastName("Gajula")
      .setGender("male")
      .setAge(32)
    dao.insert(user)
    println(dbuser)
    val newUser = user.setLastName("manika")
    val updatedUser = dao.update(newUser)
    val dbUser2 = dao.getUserById(updatedUser.id)
    println(dbUser2)
  }

}
