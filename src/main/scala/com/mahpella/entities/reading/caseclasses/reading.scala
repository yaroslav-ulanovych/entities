package com.mahpella.entities.reading.caseclasses

import com.mahpella.util.ClassUtils
import com.mahpella.util.reflection.RichField

import scala.reflect.ClassTag

object read {

  def apply[E](implicit classTag: ClassTag[E], params: Params = DefaultParams) = new {
    def from(adapter: Adapter): E = {

      val klass = classTag.runtimeClass

      val instantiator = params.instantiators.find(_.klass == klass) match {
        case Some(x) => x
        case None => {
          val className = klass.getName

          val endsWithNumberRegex = """^(.+)(\d+)$""".r

          val companionObjectClassName = className match {
            case endsWithNumberRegex(x, y) => x + (y.toInt + 1) + "$"
            case _ => className + "$"
          }

          val companionObjectClass = ClassUtils.forName(companionObjectClassName) match {
            case Some(x) => x
            case None => instantiationException(
              s"$className seems not to be a case class cause we couldn't find it's companion object class $companionObjectClassName"
            )
          }

          val companionObject = try {
            val field = companionObjectClass.getField("MODULE$")
            field.get(null)
          } catch {
            case e: NoSuchFieldException => throw new NotCompanionObjectException(companionObjectClass, s"it has no MODULE$$ field")
          }

          CompanionObjectInstantiator(companionObject)
        }
      }

      val argTypes = instantiator.applyMethod.getParameterTypes.toSeq

      val allFields = klass.getDeclaredFields

      val fields = allFields.filter(_.isPrivate)

      if (fields.length == argTypes.length) {
        val args = fields zip argTypes map { case (field, tpe) =>
          adapter.get(field.getName).get
        }
        val castArgs = args.asInstanceOf[Array[Object]]
        val instance = instantiator.applyMethod.invoke(instantiator.companionObject, castArgs: _*)
        instance.asInstanceOf[E]
      } else {
        throw new RuntimeException(s"fields: ${fields.length}, ctor arity: ${argTypes.length}")
      }
    }
  }
}
