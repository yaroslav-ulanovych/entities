package com.mahpella.entities.reading.caseclasses

import com.mahpella.entities.FunSuite

class VarTest extends FunSuite {
  test("nested case class companion object bytecode naming") {
    case class Nested()
    classOf[Nested].getName shouldBe "com.mahpella.entities.reading.caseclasses.VarTest$$anonfun$1$Nested$2"
    Nested.getClass.getName shouldBe "com.mahpella.entities.reading.caseclasses.VarTest$$anonfun$1$Nested$3$"
  }
}
