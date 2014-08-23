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