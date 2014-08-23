package com.mahpella.entities.reading.caseclasses

class ReadException(msg: String) extends Exception(msg)

class InstantiationException(msg: String) extends Exception(msg)

object instantiationException {
  def apply(msg: String) = throw new InstantiationException(msg)
}