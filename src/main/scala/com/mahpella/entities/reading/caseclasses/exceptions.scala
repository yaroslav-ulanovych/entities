package com.mahpella.entities.reading.caseclasses

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
  val valueType: Class[_]
) extends Exception(
  s"bad value for ${klass.getSimpleName}.$field field of ${fieldType.getSimpleName} type: ${valueType.getSimpleName}($value)"
)