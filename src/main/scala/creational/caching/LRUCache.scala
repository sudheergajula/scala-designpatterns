package creational.caching


import creational.caching.entity.UserAccount

import scala.collection.mutable


class Node(val id: Int, val user: UserAccount, var prev: Node = null, var next: Node = null)

class LRUCache(capacity: Int) {
  val cache: mutable.Map[Int, UserAccount] = scala.collection.mutable.HashMap[Int, UserAccount]()
  var head: Node = _
  var tail: Node = _

  def isFull: Boolean = cache.size >= capacity

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
      head = null
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
