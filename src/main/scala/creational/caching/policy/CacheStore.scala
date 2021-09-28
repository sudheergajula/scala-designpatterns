package creational.caching.policy

trait CacheStore[T] {

  def add(obj: T): Unit

  def remove(obj: T): Unit

  def get(id: Int): Option[T]

}
