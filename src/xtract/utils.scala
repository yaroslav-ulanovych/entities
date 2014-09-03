package xtract

import java.lang.reflect.{Field, Modifier}

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

  // naive check
  def isCaseClass(klass: Class[_]): Boolean = classOf[Product].isAssignableFrom(klass)

  def isAbstract(klass: Class[_]) = Modifier.isAbstract(klass.getModifiers)

  def isPrivate(field: Field) = Modifier.isPrivate(field.getModifiers)

  def toNotPrimitive(klass: Class[_]) = klass.getName match {
    case "int" => classOf[Integer]
    case x => klass
  }
}