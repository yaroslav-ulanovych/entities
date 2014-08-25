package com.mahpella.entities.reading.caseclasses

// def begin Params
case class Params[-T](
// [Adapter](#adapters-section) is an interface to support different data structures.
  adapter: Adapter[T],
// [Converters](#converters-section) even discrepancies between
// types of case class fields and types of data source fields.
  converters: Seq[Converter[_, _]],
// [Field naming convention](#field-naming-conventions-section)
  fieldNamingConvention: FieldNamingConvention,
// [Instantiators](#instantiators-section) solve problem of nested case classes.
  instantiators: Seq[CompanionObjectInstantiator]
)
// def end
{
// def begin ParamsMethods
  def +[U](x: Adapter[U]) = copy(adapter = x)

  def +(x: Converter[_, _]) = copy(converters = converters :+ x)

  def +(x: FieldNamingConvention) = copy(fieldNamingConvention = x)

  def +(x: CompanionObjectInstantiator) = copy(instantiators = instantiators :+ x)
// def end
}

// def begin DefaultParams
object DefaultParams extends Params(
  adapter = MapAdapter,
  converters = Seq(),
  fieldNamingConvention = LowerCamelCase.noDelimiter,
  instantiators = Seq()
)
// def end
