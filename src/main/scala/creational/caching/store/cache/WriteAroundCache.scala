package creational.caching.store.cache

import creational.caching.entity.UserAccount
import creational.caching.store.db.UserDao

import scala.util.{Failure, Success, Try}

class WriteAroundCache(cache: Cache[Int, UserAccount], userDao: UserDao) extends CacheStore[UserAccount] {


  override def add(user: UserAccount): Unit = {
    Try {
      if (cache.containsKey(user.id)) {
        userDao.update(user)
        cache.remove(user)
      } else {
        userDao.addUser(user)
      }
      user
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

  override def get(id: Int): Option[UserAccount] = {
    val user = cache.get(id).getOrElse({
      println("********* CACHE MISS *********")
      println("======== fetching from database ======== ")
      userDao.getUserById(id)
    })
    Some(user)
  }
}
