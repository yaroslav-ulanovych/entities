package xtract



trait Diver {
  def dive[T](key: String, data: T, params: Params[T]): Option[(String, Any)]
}

object DefaultDiver extends Diver {
  def dive[T](key: String, data: T, params: Params[T]): Option[(String, Any)] = {
    params.adapter.get(data, key) match {
      case Some(v) => Some(("", v))
      case None => None
    }
  }
}

case class FlatDiver(separator: String) extends Diver {
  def dive[T](key: String, data: T, params: Params[T]): Option[(String, Any)] = {
    Some((key + separator, data))
  }
}