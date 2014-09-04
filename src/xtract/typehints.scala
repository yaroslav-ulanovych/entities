package xtract


trait TypeHintNamingStrategy {
  def guessType(klass: Class[_], typeHint: String): Option[Class[_]]
}

object SamePackageTypeHintNamingStrategy extends TypeHintNamingStrategy {
  def guessType(klass: Class[_], typeHint: String): Option[Class[_]] = {
    val pkg = klass.getPackage.getName
    (
      ClassUtils.forName(s"$pkg.${typeHint}"),
      ClassUtils.forName(s"$pkg.${typeHint}${klass.getSimpleName}")
    ) match {
      case (Some(x), _) => Some(x)
      case (None, Some(x)) => Some(x)
      case (None, None) => None
    }
  }
}

trait TypeHintLocation {
  def get[T](field: List[String], data: T, params: Params[T]): Option[Either[Any, String]]
}

case class InFieldNearTypeHintLocation(postfix: String) extends TypeHintLocation {
  def get[T](field: List[String], data: T, params: Params[T]): Option[Either[Any, String]] = {
    val key = params.fieldNamingConvention.apply(field :+ "type")
    params.adapter.get(data, key) match {
      case Some(x: String) => Some(Right(x))
      case Some(x) => Some(Left(x))
      case None => None
    }
  }
}
