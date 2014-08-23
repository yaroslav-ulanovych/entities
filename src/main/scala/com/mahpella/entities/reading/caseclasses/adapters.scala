package com.mahpella.entities.reading.caseclasses

import scala.collection.GenMap
import java.util.{Map => JavaMap}

trait Adapter {
  def get(key: String): Option[Any]
}

case class MapAdapter(map: GenMap[String, Any]) extends Adapter {
  def get(key: String): Option[Any] = map.get(key)
}

case class JavaMapAdapter(map: JavaMap[String, Any]) extends Adapter {
  def get(key: String): Option[Any] = Option(map.get(key))
}

object NilAdapter extends Adapter {
  override def get(key: String): Option[Any] = None
}