package com.mahpella.entities.reading.caseclasses.docs

import com.mahpella.entities.FunSpec
import com.mahpella.entities.reading.caseclasses.{BadFieldValueException, MissingFieldException, NotCompanionObjectException}

// doc begin
// # Simple case class reading
// Let's create a case class for examples below
case class Person(id: Int, name: String)
// doc end

// doc begin
// To read a case class you need a read function
import com.mahpella.entities.reading.caseclasses.read
// and an adapter to your data source you want to read from.
// There are few build in adapters
import com.mahpella.entities.reading.caseclasses.MapAdapter
import com.mahpella.entities.reading.caseclasses.JavaMapAdapter
// doc end

class Part1 extends FunSpec {
  it("SimpleTopLevelCaseClass") {

// doc begin
// Given some map
    val data = Map("id" -> 2, "name" -> "John")
// reading is simple
    val person = read[Person] from MapAdapter(data)

    person shouldBe Person(2, "John")
// doc end
  }

  it("java map") {
// doc begin
// Reading java map?
    val data = new java.util.HashMap[String, Any]()
    data.put("id", 2)
    data.put("name", "John")
// Just use proper adapter
    val person = read[Person] from JavaMapAdapter(data)

    person shouldBe Person(2, "John")
// doc end
  }

  it("missing field") {
// doc begin
// If you try to read some trash an exception will be thrown.
// Exceptions tend to contain complete information, so
// missing field exception
    val data = Map[String, Any]()
    try {
      read[Person] from MapAdapter(data)
// doc end
      fail("an exception should be thrown")
// doc begin
// gives you information about class we were trying to instantiate,
// missing field name and data we were looking for the field in
    } catch {
      case e: MissingFieldException => {
        e.getMessage shouldBe "missing field Person.id in Map()"
// You also have direct access to those parts for, I don't know,
// localized error messages?
        e.klass shouldBe classOf[Person]
        e.field shouldBe "id"
        e.data shouldBe data
      }
    }
// doc end
  }

  it("bad field value") {
// doc begin
// When your data source contains a value of type you don't expect,
// like bool instead of int in example below
    val data = Map[String, Any]("id" -> false, "name" -> "John")
// you also get a detailed exception
    try {
      read[Person] from MapAdapter(data)
      fail("an exception should be thrown")
    } catch {
      case e: BadFieldValueException => {
        e.getMessage shouldBe "bad value for Person.id field of int type: Boolean(false)"
        e.klass shouldBe classOf[Person]
        e.field shouldBe "id"
        e.fieldType shouldBe classOf[Int]
        e.value shouldBe false
        e.valueType shouldBe classOf[java.lang.Boolean]
      }
    }
// doc end
  }

  describe("nested case classes") {
    it("exception") {
// doc begin
// There are some caveats with nested case classes.
      def enclosingMethod = {
        case class Nested(id: Int, name: String)
// One can't just instantiate it without an instance of enclosing class.
// If you try to read it
        try {
          read[Nested] from MapAdapter(Map())
// doc end
          fail("an exception should be thrown")
// doc begin
// you'll get an exception with a message about MODULE$ field
        } catch {
          case e: NotCompanionObjectException => {
            e.reason shouldBe "it has no MODULE$ field"
          }
        }
      }
// doc end
      enclosingMethod
    }
  }
}


// doc begin
// You can fix that by supplying companion objects to the read function explicitly.
// Read function takes read parameters implicitly, they are defaulted to
import com.mahpella.entities.reading.caseclasses.DefaultParams
// For instantiation a part of params called instantiators is responsible.
// You can construct then via instantiator factory object
import com.mahpella.entities.reading.caseclasses.Instantiator
// doc end

class Part2 extends FunSpec {
  it("read nested case class") {
// doc begin
    def enclosingMethod {
      case class Nested(id: Int, name: String)
      val data = Map("id" -> 2, "name" -> "John")
      implicit val params = DefaultParams + Instantiator(Nested)
      val nested = read[Nested] from MapAdapter(data)
      nested shouldBe Nested(2, "John")
    }
// doc end
    enclosingMethod
  }
}