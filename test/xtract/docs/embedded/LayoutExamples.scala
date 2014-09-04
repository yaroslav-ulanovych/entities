package xtract.docs.embedded

// doc begin
// There is more than one way to layout embedded classes in a tree-like data structure.
// doc end
object LayoutExamples {
  val layout1 = Map(
    "figure" -> Map(
      "type" -> "Rectangle",
      "args" -> Map(
        "x" -> 0,
        "y" -> 0,
        "radius" -> 10
      )
    )
  )

  val layout2 = Map(
    "figure" -> Map(
      "type" -> "Circle",
      "x" -> 0,
      "y" -> 0,
      "radius" -> 10
    )
  )

  val layout3 = Map(
    "figureType" -> "Circle",
    "figure" -> Map(
      "x" -> 0,
      "y" -> 0,
      "radius" -> 10
    )
  )

  val layout4 = Map(
    "figureType" -> "Circle",
    "figure/circle/x" -> 0,
    "figure/circle/y" -> 0,
    "figure/circle/radius" -> 10
  )
}
