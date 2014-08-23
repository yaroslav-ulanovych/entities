package com.mahpella.entities.reading.caseclasses

import com.mahpella.entities.FunSuite


class ClassThatIsNotCompanionObject
class ClassThatEndsWithDollarButIsNotCompanionObject$

class CompanionObjectInstantiatorTest extends FunSuite {
  case class Nested(id: Int, name: String)
  test("1") {
    intercept[NotCompanionObjectException] {
      CompanionObjectInstantiator(new ClassThatIsNotCompanionObject)
    }
  }
  test("2") {
    intercept[NotCompanionObjectException] {
      CompanionObjectInstantiator(new ClassThatEndsWithDollarButIsNotCompanionObject$)
    }
  }

  test("3") {
    case class Person(id: Int, Name: String)
    println(CompanionObjectInstantiator(Person).argTypes)
  }
}
