package utils

object MapUtils {

  implicit class MapWithAdjust[K, V](underlying: Map[K, V]) {
    def adjust[K1 <: K](key: K1)(f: V => V): Map[K, V] = underlying + (key -> f(underlying(key)))

    def adjustWithValue[K1 <: K](key: K1)(f: V => V): (Map[K, V], V) = {
      val newValue = f(underlying(key))
      (underlying + (key -> newValue), newValue)
    }
  }
}
