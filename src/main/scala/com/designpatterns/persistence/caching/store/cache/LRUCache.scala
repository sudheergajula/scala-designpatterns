package com.designpatterns.persistence.caching.store.cache

import com.designpatterns.persistence.caching.entity.UserAccount

import scala.collection.mutable


class Node(val id: Int, val user: UserAccount, var prev: Node = null, var next: Node = null)

trait Cache[K, V] {
  val cache: mutable.Map[K, V] = scala.collection.mutable.HashMap[K, V]()

  def add(obj: V): Unit

  def remove(obj: V): Unit

  def get(key: K): Option[V]

  def isFull(): Boolean

  def containsKey(key: Int): Boolean
}

class EmptyCache extends Cache[Int, UserAccount] {

  override def add(obj: UserAccount): Unit = {

  }

  override def remove(obj: UserAccount): Unit = {

  }

  override def get(key: Int): Option[UserAccount] = {
    None
  }

  override def isFull(): Boolean = false

  def containsKey(key: Int): Boolean = true
}

class LRUCache(capacity: Int) extends Cache[Int, UserAccount] {
  var head: Node = _
  var tail: Node = _

  def isFull(): Boolean = cache.size >= capacity

  def containsKey(key: Int): Boolean = cache.contains(key)

  def lastNode: Node = tail

  def add(user: UserAccount): Unit = {
    if (cache.isEmpty) {
      appendToHead(user)
      return
    }
    if (!cache.contains(user.id) && cache.size < capacity) {
      appendToHead(user)
    } else if (!cache.contains(user.id) && cache.size >= capacity) {
      removeFromTail()
      appendToHead(user)
    }
  }

  /*
    Add to beginning of list
   */
  private def appendToTail(user: UserAccount): Unit = {
    val node = new Node(user.id, user)
    cache.put(user.id, user)
    if (head == null && tail == null) {
      head = node
      tail = node
      return
    }
    node.prev = tail
    tail.next = node
    tail = node
  }

  /*
    Add to beginning of list
   */
  private def appendToHead(user: UserAccount): Unit = {
    val node = new Node(user.id, user)
    cache.put(user.id, user)
    if (head == null && tail == null) {
      head = node
      tail = node
      return
    }
    node.next = head
    head.prev = node
    head = node
  }

  /*
    Remove node from end of the list
   */
  private def removeFromTail(): Unit = {
    val prev = tail.prev
    println(s"dropping node ${tail.id}, ${tail.user} from end of list")
    cache.remove(tail.id)
    prev.next = null
    tail = prev
  }

  private def removeFromList(node: Node): Unit = {
    var curr = head
    while (curr.id != node.id && curr != null) {
      curr = curr.next
    }
  }

  def remove(user: UserAccount): Unit = {
    var curr = head
    if (curr.id == user.id) {
      cache.remove(user.id)
      head = curr.next
      return
    }
    while (curr.id != user.id && curr != null) {
      curr = curr.next
    }
    if (curr != null) {
      if (curr == tail) {
        tail = curr.prev
        tail.next = null
        return
      }
      curr.prev.next = curr.next
      curr.next.prev = curr.prev
    }
  }

  def get(id: Int): Option[UserAccount] = {
    if (cache.contains(id)) {
      val user = cache.get(id)
      remove(user.get)
      appendToHead(user.get)
      return user
    }
    None
  }

  def display(): Unit = {
    println(cache)
  }

}
