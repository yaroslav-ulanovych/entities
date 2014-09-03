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
  def get[T](field: String, data: T, adapter: Adapter[T]): Option[Either[Any, String]] = {
    ???
  }
}

case class InFieldNearTypeHintLocation(postfix: String) extends TypeHintLocation {
}
