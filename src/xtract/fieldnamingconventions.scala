package xtract

// def begin FieldNamingConvention
case class FieldNamingConvention(casing: Casing, delimiter: Delimiter) {
  def apply(xs: List[String]): String = delimiter(casing(xs))
}
// def end

// def begin Casing
// Casing decides which letters are capitals and which are not.
// It takes a list of field name parts and applies casing rules to them.
trait Casing {
  def apply(xs: List[String]): List[String]
// Casing also has a bit of dsl to allow construction of field naming convention
// by supplying a second part of it — delimiter.
  def delimitedBy(delimiter: Delimiter) = FieldNamingConvention(this, delimiter)
  def noDelimiter = delimitedBy(NoDelimiter)
}
// def end

object LowerCamelCase extends Casing {
  def apply(xs: List[String]): List[String] = {
    xs.map(_.toLowerCase) match {
      case x :: xs => x :: (xs map capitalizeFirstLetter)
      case Nil => Nil
    }
  }

  def capitalizeFirstLetter(s: String): String = {
    val chars = s.toCharArray
    chars(0) = Character.toUpperCase(chars(0))
    new String(chars)
  }
}

object LowerCase extends Casing {
  def apply(xs: List[String]): List[String] = xs.map(_.toLowerCase)
}

object UpperCase extends Casing {
  def apply(xs: List[String]): List[String] = xs.map(_.toUpperCase)
}

object AsIsCase extends Casing {
  def apply(xs: List[String]): List[String] = xs
}

// def begin Delimiter
// Delimiter concatenates parts of names using given delimiter string.
case class Delimiter(value: String) {
  def apply(xs: List[String]): String = xs.mkString(value)
}
// def end

object Underscore extends Delimiter("_")

object NoDelimiter extends Delimiter("")