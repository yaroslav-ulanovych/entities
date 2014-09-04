package xtract

// def begin Params
case class Params[-T](
// [Adapter](#adapters-section) is an interface to support different data structures.
  adapter: Adapter[T],
// [Converters](#converters-section) even discrepancies between
// types of case class fields and types of data source fields.
  converters: Seq[Converter],
// [Field naming convention](#field-naming-conventions-section)
  fieldNamingConvention: FieldNamingConvention,
// [Instantiators](#instantiators-section) solve problem of nested case classes.
  instantiators: Seq[CompanionObjectInstantiator],
  layout: Layout,
  typeHintLocation: TypeHintLocation
)
// def end
{
// def begin ParamsMethods
  def +[U](x: Adapter[U]) = copy(adapter = x)

  def +(x: Converter) = copy(converters = converters :+ x)

  def +(x: FieldNamingConvention) = copy(fieldNamingConvention = x)

  def +(x: CompanionObjectInstantiator) = copy(instantiators = instantiators :+ x)

  def +(x: Layout) = copy(layout = x)
// def end
}

// def begin DefaultParams
object DefaultParams extends Params(
  adapter = MapAdapter,
  converters = Seq(JavaEnumConverter),
  fieldNamingConvention = LowerCamelCase.noDelimiter,
  instantiators = Seq(),
  layout = DefaultLayout,
  typeHintLocation = InFieldNearTypeHintLocation("type")
)
// def end
