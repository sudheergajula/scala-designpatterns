package com.designpatterns.creational.caching

import com.designpatterns.creational.caching.entity.UserAccount
import com.designpatterns.creational.caching.store.cache.CachingPolicy.CachingPolicy
import com.designpatterns.creational.caching.store.cache._
import com.designpatterns.creational.caching.store.db.UserDao

import scala.io.Source

case class PersistenceManager(capacity: Int,
                              cachePolicy: CachingPolicy = CachingPolicy.WRITE_THROUGH) {

  val userDao = new UserDao
  val cache = new LRUCache(capacity)
  lazy val manager = cachePolicy match {
    case CachingPolicy.WRITE_THROUGH => new WriteThroughCache(cache, userDao)

    case CachingPolicy.WRITE_BEHIND => new WriteBehindCache(cache, userDao)

    case CachingPolicy.WRITE_AROUND => new WriteAroundCache(cache, userDao)

    case CachingPolicy.WRITE_BACK => new WriteBackCache(cache, userDao)
  }

  def put(user: UserAccount): Unit = {
    manager.add(user)
  }

  def get(userId: Int): Option[UserAccount] = {
    manager.get(userId)
  }
}

object App {

  def main(args: Array[String]): Unit = {
    val manager = PersistenceManager(10, cachePolicy = CachingPolicy.WRITE_BACK)

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
      }).foreach(user => manager.put(user))

    val user = manager.get(16)
    println(user)

    val user1 = manager.get(1)
    println(user1.get)

    val user11 = manager.get(1)
    println(user11.get)

  }
}
