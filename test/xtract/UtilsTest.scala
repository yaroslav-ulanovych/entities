package xtract

import com.mahpella.entities.FunSuite

class UtilsTest extends FunSuite {
  test("splitFieldNameIntoParts") {
    Utils.splitFieldNameIntoParts("favouriteDogName") shouldBe List("favourite", "Dog", "Name")
  }
}
