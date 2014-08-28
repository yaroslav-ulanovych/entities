package xtract

import com.mahpella.util.ClassUtils
import com.mahpella.util.reflection.RichField

import scala.reflect.ClassTag

object read {

  def apply[E](implicit classTag: ClassTag[E]) = new {
    def from[T](data: T)(implicit params: Params[T] = DefaultParams): E = {
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
        val args = fields zip argTypes map { case (field, fieldType) =>
          val key = params.fieldNamingConvention.apply(Utils.splitFieldNameIntoParts(field.getName))
          params.adapter.get(data, key) match {
            case Some(value) => {
              val valueType = value.getClass
              val fieldTypeNotPrimitive = fieldType.getName match {
                case "int" => classOf[Integer]
                case x => fieldType
              }
              val convertedValue = if (!fieldTypeNotPrimitive.isAssignableFrom(valueType)) {
                params.converters find { x =>
                  x.canConvertFrom(valueType) && x.canConvertTo(fieldType)
                } match {
                  case Some(converter) => {
                    val option = converter.convert(value, fieldType)
                    option match {
                      case Some(convertedValue) => convertedValue
                      case None => {
                        badFieldValue(klass, key, fieldType, value, valueType, Some(converter))
                      }
                    }
                  }
                  case None => badFieldValue(klass, key, fieldType, value, valueType, None)
                }
              } else {
                value
              }
              convertedValue
            }
            case None => throw new MissingFieldException(klass, key, data)
          }
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
