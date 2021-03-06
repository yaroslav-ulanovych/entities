package xtract

class ReadException(msg: String) extends Exception(msg)

class InstantiationException(msg: String) extends Exception(msg)

object instantiationException {
  def apply(msg: String) = throw new InstantiationException(msg)
}

class MissingFieldException(
  val klass: Class[_],
  val field: String,
  val data: Any
) extends Exception(
  s"missing field ${klass.getSimpleName}.$field in $data"
)

class BadFieldValueException(
  val klass: Class[_],
  val field: String,
  val fieldType: Class[_],
  val value: Any,
  val valueType: Class[_],
  val converter: Option[Converter]
) extends Exception(
  s"bad value for ${klass.getSimpleName}.$field field of ${fieldType.getSimpleName} type: ${valueType.getSimpleName}($value), converter: $converter"
)

object badFieldValue {
  def apply(
    klass: Class[_],
    field: String,
    fieldType: Class[_],
    value: Any,
    valueType: Class[_],
    converter: Option[Converter]
  ) = throw new BadFieldValueException(klass, field, fieldType, value, valueType, converter)
}