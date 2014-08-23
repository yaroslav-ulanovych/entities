package com.mahpella.entities.reading.caseclasses

import scala.collection.GenMap
import java.util.{Map => JavaMap}

trait Adapter[T] {
  def get(key: String): Option[Any]
}

case class MapAdapter(map: GenMap[String, Any]) extends Adapter[GenMap[String, Any]] {
  def get(key: String): Option[Any] = map.get(key)
}

case class JavaMapAdapter(map: JavaMap[String, Any]) extends Adapter[JavaMap[String, Any]] {
  def get(key: String): Option[Any] = Option(map.get(key))
}
