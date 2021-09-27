package creational.dao.entity

object App {

  def main(args: Array[String]): Unit = {
    val userDao = new UserDao
    val userDb = userDao.addUser("Sudheer", "Gajula", 32, "Male")
    println(userDb.toString)
    val id = userDb.id

    val user = userDao.getUserById(id)
    println(user)


    userDao.removeUser(user)

    val user1 = userDao.getUserByName("Sudheer", "Gajula")
    println(user1)
  }


}
