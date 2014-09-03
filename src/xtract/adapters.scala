package xtract

import java.util.{Map => JavaMap}

import scala.collection.GenMap
import scala.reflect.ClassTag

// def begin Adapter
abstract class Adapter[-T: ClassTag] {
  def accepts(klass: Class[_]): Boolean = implicitly[ClassTag[T]].runtimeClass.isAssignableFrom(klass)
  def get(data: T, key: String): Option[Any]
}
// def end

// def begin MapAdapter
object MapAdapter extends Adapter[GenMap[String, Any]] {
  def get(data: GenMap[String, Any], key: String): Option[Any] = data.get(key)
}
// def end

// def begin JavaMapAdapter
object JavaMapAdapter extends Adapter[JavaMap[String, Any]] {
  def get(data: JavaMap[String, Any], key: String): Option[Any] = Option(data.get(key))
}
// def end