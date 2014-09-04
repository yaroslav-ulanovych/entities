package xtract.docs.divers

import xtract.{FlatLayout, DefaultParams, read, FunSuite}

case class Person(homeAddress: Address)
case class Address(country: String, postCode: String)

class DiversTest extends FunSuite {
  val layout1 = Map(
    "homeAddress" -> Map(
      "country" -> "USA",
      "postCode" -> "90503"
    )
  )
  val flatLayout = Map(
    "homeAddress_country" -> "USA",
    "homeAddress_postCode" -> "90503"
  )

  test("default layout") {
    val person = read[Person] from layout1
    person shouldBe Person(Address("USA", "90503"))
  }

  test("flat layout") {
    implicit val params = DefaultParams + FlatLayout("_")
    val person = read[Person] from flatLayout
    person shouldBe Person(Address("USA", "90503"))
  }
}

trait Figure
case class Circle(x: Int, y: Int, radius: Int)
case class Rectangle(x: Int, y: Int, width: Int, height: Int)

class PolymorphicLayoutExamples {

}

