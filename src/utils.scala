package xtract



object Utils {
  def splitFieldNameIntoParts(x: String): List[String] = x.split("""(?=\p{Lu})""").toList
}

object ClassUtils {
  def forName[T](name: String): Option[Class[T]] = {
    try {
      val klass = Class.forName(name)
      Some(klass.asInstanceOf[Class[T]])
    } catch {
      case _: ClassNotFoundException => None
    }
  }
}