package creational.caching

import creational.caching.entity.UserAccount
import creational.caching.store.cache.{CachingPolicy, WriteThroughCache}
import creational.caching.store.db.UserDao

import scala.io.Source

case class PersistenceManager(capacity: Int,
                              cachePolicy: CachingPolicy = CachingPolicy.WRITE_THROUGH) {

  val userDao = new UserDao
  val cache = new LRUCache(capacity)

  def persist(user: UserAccount): Unit = {
    val manager = cachePolicy match {
      case CachingPolicy.WRITE_THROUGH => new WriteThroughCache(cache, userDao)
    }
    manager.add(user)
  }

  def get(userId: Int): Option[UserAccount] = {
    val manager = cachePolicy match {
      case CachingPolicy.WRITE_THROUGH => new WriteThroughCache(cache, userDao)
    }
    manager.get(userId)
  }
}

object App {

  def main(args: Array[String]): Unit = {
    val manager = PersistenceManager(10)

    val source = Source.fromFile("src/main/resources/test_data.txt")
    source.getLines()
      .map(line => {
        val token = line.split(",")
        val user = new UserAccount
        user
          .setId(Integer.valueOf(token(0)))
          .setFirstName(token(1))
          .setLastName(token(2))
          .setGender(token(3))
          .setAge(Integer.valueOf(token(4)))
      }).foreach(user => manager.persist(user))

    val user = manager.get(16)
    println(user)

    val user1 = manager.get(1)
    println(user1.get)

  }
}
