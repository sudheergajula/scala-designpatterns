package com.designpatterns.creational.caching.store.cache

import com.designpatterns.creational.caching.entity.UserAccount
import com.designpatterns.creational.caching.store.db.UserDao

import scala.util.{Failure, Success, Try}

class WriteThroughCache(cache: LRUCache, userDao: UserDao) extends CacheStore[UserAccount] {


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

  override def get(id: Int): Option[UserAccount] = {
    val user = cache.get(id).getOrElse({
      println("********* CACHE MISS *********")
      println("======== fetching from database ======== ")
      userDao.getUserById(id)
    })
    Some(user)
  }
}
