package creational.caching.policy

object CachingPolicy extends Enumeration {
  type CachingPolicy = Value
  val WRITE_THROUGH, WRITE_BEHIND, WRITE_AROUND, WRITE_ASIDE = Value
}
