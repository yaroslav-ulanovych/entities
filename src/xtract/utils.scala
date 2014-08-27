package xtract

object Utils {
  def splitFieldNameIntoParts(x: String): List[String] = x.split("""(?=\p{Lu})""").toList
}
