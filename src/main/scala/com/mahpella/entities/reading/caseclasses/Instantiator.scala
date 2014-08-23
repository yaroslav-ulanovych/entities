package com.mahpella.entities.reading.caseclasses

import com.mahpella.util.ClassUtils

import scala.reflect.ClassTag

sealed trait Instantiator {
  val resultClass: Class[_]

  val argTypes: Array[Class[_]]

  def instantiate(args: Array[Any]): Any
}

//case class ReflectionInstantiator(klass: Class[_]) {
//  def tryInstantiate(args: Array[Any])
//}

case class Instantiators(xs: Seq[Instantiator]) {
  def +(x: Instantiator) = Instantiators(xs :+ x)

  def findFor(klass: Class[_]): Option[Instantiator] = xs.find(_.resultClass == klass)
}

case class NotCompanionObjectException(obj: Any, reason: String) extends Exception(
  s"${obj.getClass.getName}($obj) seems not to be a companion object, since $reason"
)

case class CompanionObjectInstantiator(obj: AnyRef) {
  val companionObjectClass = obj.getClass

  val companionObjectClassName = companionObjectClass.getName

  def notCompanionObject(reason: String) = throw NotCompanionObjectException(obj, reason)

  val classNameWithoutDollar = companionObjectClassName.endsWith("$") match {
    case true => companionObjectClassName.dropRight(1)
    case false => notCompanionObject(s"it's class name doesn't end with a dollar sign")
  }

  val endsWithNumberRegex = """^(.+)(\d+)$""".r

  val className = classNameWithoutDollar match {
    case endsWithNumberRegex(prefix, suffix) => prefix + (suffix.toInt - 1)
    case _ => classNameWithoutDollar
  }

  val klass = ClassUtils.forName(className).getOrElse {
    notCompanionObject(s"it's companion class $className not found")
  }

  val applyMethods = companionObjectClass.getMethods.filter(_.getName == "apply")

  val applyMethod = applyMethods.find(_.getReturnType == klass) getOrElse {
    notCompanionObject(s"it has no apply method that returns $className among ${applyMethods.toList}")
  }

  val argTypes = applyMethod.getParameterTypes.toSeq
}


object Instantiator {
  def apply(obj: AnyRef) = ???
  def apply[A: ClassTag, B: ClassTag, Z: ClassTag](f: Function2[A, B, Z]) = new Instantiator {
  override val resultClass: Class[_] = implicitly[ClassTag[Z]].runtimeClass

  override def instantiate(args: Array[Any]): Any = f.apply(
    args(0).asInstanceOf[A],
    args(1).asInstanceOf[B]
  )

  override val argTypes: Array[Class[_]] = Array(
    implicitly[ClassTag[A]].runtimeClass,
    implicitly[ClassTag[B]].runtimeClass
  )
}
}
