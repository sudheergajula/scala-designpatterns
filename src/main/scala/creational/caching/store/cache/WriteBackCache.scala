package creational.caching.store.cache

import creational.caching.entity.UserAccount
import creational.caching.store.db.UserDao

import scala.util.{Failure, Success, Try}

class WriteBackCache(cache: LRUCache, userDao: UserDao) extends CacheStore[UserAccount] {


  override def add(user: UserAccount): Unit = {
    Try {
      cache.add(user)
      userDao.addUser(user)
    } match {
      case Success(value) =>
        println(s"add user to db ${value.id}")
      case Failure(exception) =>
        throw exception
    }

  }

  override def remove(obj: UserAccount): Unit = {
    Try {
      cache.remove(obj)
      userDao.removeUser(obj)
    } match {
      case Success(value) =>
        println(s"add user to db ${value.id}")
      case Failure(exception) =>
        throw exception
    }

  }

  /**
   * If hits cache miss then add value to cache.
   *
   * @param id
   * @return
   */
  override def get(id: Int): Option[UserAccount] = {
    val user = cache.get(id).getOrElse({
      println("********* CACHE MISS *********")
      println("======== fetching from database ======== ")
      val user = userDao.getUserById(id)
      cache.add(user)
      user
    })
    Some(user)
  }
}
