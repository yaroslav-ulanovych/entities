package com.mahpella.entities.reading.caseclasses

case class Params(instantiators: Seq[CompanionObjectInstantiator]) {
  def +(x: CompanionObjectInstantiator) = copy(instantiators = instantiators :+ x)
}

object DefaultParams extends Params(Seq())
