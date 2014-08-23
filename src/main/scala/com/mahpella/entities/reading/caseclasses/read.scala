package com.mahpella.entities.reading.caseclasses

import com.mahpella.util.reflection.RichField

import scala.reflect.ClassTag

object read {
  def apply[E: ClassTag] = new {
    def from[T](adapter: Adapter[T]): E = {
      val classTag = implicitly[ClassTag[E]]
      val klass = classTag.runtimeClass
      None/*params.instantiators.findFor(klass)*/ match {
//        case Some(x) => {
//          val allFields = klass.getDeclaredFields
//          val fields = allFields.filter(_.isPrivate)
//          val argTypes = x.argTypes
//          if (fields.length == argTypes.length) {
//            val args = fields zip argTypes map { case (field, tpe) =>
//              params.reader.get(data, field.getName).get
//            }
//            val instance = x.instantiate(args)
//            instance.asInstanceOf[E]
//          } else {
//            throw new RuntimeException(s"fields: ${fields.length}, ctor arity: ${argTypes.length}")
//          }
//        }
        case None => {
          if (klass.getEnclosingClass ne null) {
            throw new RuntimeException(s"instantiation error: ${klass.getName} is nested, supply custom instantiator")
          }
          val constructors = klass.getConstructors
          constructors match {
            case Array(ctor) => {
              val allFields = klass.getDeclaredFields
              val fields = allFields.filter(_.isPrivate)
              val isNested = klass.getEnclosingClass ne null
              val parameterTypes = ctor.getParameterTypes
              if (fields.length == parameterTypes.length) {
                val args = fields zip parameterTypes map { case (field, tpe) =>
                  adapter.get(field.getName).get
                }
                val castArgs = args.asInstanceOf[Array[Object]]
                val instance = ctor.newInstance(castArgs: _*)
                instance.asInstanceOf[E]
              } else {
                throw new RuntimeException(s"fields: ${fields.length}, ctor arity: ${parameterTypes.length}")
              }
            }
            case x => ???
          }
        }
      }

    }
  }
}
