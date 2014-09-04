package xtract



trait Layout {
  def dive[T](key: String, data: T, params: Params[T]): Option[(String, Any)]
}

object DefaultLayout extends Layout {
  def dive[T](key: String, data: T, params: Params[T]): Option[(String, Any)] = {
    params.adapter.get(data, key) match {
      case Some(v) => Some(("", v))
      case None => None
    }
  }
}

case class FlatLayout(separator: String) extends Layout {
  def dive[T](key: String, data: T, params: Params[T]): Option[(String, Any)] = {
    Some((key + separator, data))
  }
}