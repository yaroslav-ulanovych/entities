package com.mahpella.entities.reading.caseclasses.docs

import com.mahpella.entities.FunSuite


// doc begin
// # Brief intro example
case class Person(id: Int, name: String)
import com.mahpella.entities.reading.caseclasses.read
// doc end

class BriefIntroSection extends FunSuite {
  test("brief intro example") {
// doc begin
    val person = read[Person] from Map("id" -> 2, "name" -> "John")
    person shouldBe Person(2, "John")
// doc end
  }
}