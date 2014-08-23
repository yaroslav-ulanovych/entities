package com.mahpella.entities.reading.caseclasses

case class ReadParams[T](
  reader: Adapter[T],
  instantiators: Instantiators


) {
  def +(x: Instantiator): ReadParams[T] = copy(instantiators = instantiators + x)
}

object ReadParams {
//  def apply(): ReadParams[Map[String, Any]] = ReadParams(MapReader, Instantiators(Seq()))
}