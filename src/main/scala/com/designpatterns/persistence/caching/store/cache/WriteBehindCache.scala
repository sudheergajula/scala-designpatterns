package com.designpatterns.persistence.caching.store.cache

import com.designpatterns.persistence.caching.entity.UserAccount
import com.designpatterns.persistence.caching.store.db.UserDao

import scala.util.{Failure, Success, Try}

class WriteBehindCache(cache: LRUCache, userDao: UserDao) extends CacheStore[UserAccount] {


  override def add(user: UserAccount): Unit = {
    Try {
      if (cache.isFull && !cache.containsKey(user.id)) {
        val lastUsedUserAccount = cache.lastNode.user
        println(s"evicting user ${lastUsedUserAccount.toString}")
        userDao.updateUser(user)
      }
      user
    } match {
      case Success(value) =>
        cache.add(value)
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
