package com.mahpella.entities.reading.caseclasses.docs

import com.mahpella.entities.FunSuite

// doc begin
// # <a name="field-naming-conventions-section">Field naming conventions</a>
// Standard convention for class fields in scala is lowerCase.
// But your data source may use a DIFFERENT_ONE. Obviously you can rename
// case class fields to match fields of your data source, but that
// would deprive you from the ability to reuse such a case class for different
// data sources (json rest api and relational database, for instance).
// And just look weird `case class Person(HOME_ADDRESS: String, WORK_ADDRESS: String)`.
// Field naming convention is here to help. It consists of two parts.
// doc end
class FieldNamingConventionsSection extends FunSuite {
// doc begin
// include Casing
// There are some built int ones.
  import com.mahpella.entities.reading.caseclasses.{LowerCamelCase, LowerCase, UpperCase}
// doc end
  test("casing") {
// doc begin
// Lower camel case isn't actually doing anything,
// cause we expect only case class field names to be passed here,
// which are already in lower camel case.
    LowerCamelCase(List("home", "Address")) shouldBe List("home", "Address")
    LowerCamelCase(List("HOME", "ADDRESS")) shouldBe List("HOME", "ADDRESS")

    LowerCase(List("home", "Address")) shouldBe List("home", "address")

    UpperCase(List("home", "Address")) shouldBe List("HOME", "ADDRESS")
// doc end
  }
// doc begin
// include Delimiter
// Built in
  import com.mahpella.entities.reading.caseclasses.{NoDelimiter, Underscore}
// doc end
  test("delimiter") {
// doc begin
    NoDelimiter(List("home", "Address")) shouldBe "homeAddress"
    Underscore(List("home", "Address")) shouldBe "home_Address"
// doc end
  }
// doc begin
// Field naming convention just applies casing and delimiter in turn.
// include FieldNamingConvention
// doc end
  test("reading with field naming convention") {
// doc begin
    import com.mahpella.entities.reading.caseclasses.{read, DefaultParams}
    val params = DefaultParams + UpperCase.noDelimiter
    val person = read[Person].from(Map("ID" -> 2, "NAME" -> "John"))(params)
    person shouldBe Person(2, "John")
// doc end
  }
}
