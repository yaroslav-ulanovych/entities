package com.mahpella.entities.reading.caseclasses.docs

import com.mahpella.entities.FunSuite
import com.mahpella.entities.reading.caseclasses.{read, DefaultParams}

// doc begin
// # <a name="instantiators-section">Instantiators</a>
// There are some caveats with nested case classes.
// One can't just instantiate it without an instance of enclosing class.
// If you try to read it, you'll get [an exception](#not-companion-object-exception-section).
// To fix that you have to supply a companion object
// (as for nested case classes it carries enclosing class instance) explicitly via instantiator
import com.mahpella.entities.reading.caseclasses.Instantiator
// doc end
class InstantiatorsSection extends FunSuite {
  test("nested case classes") {
// doc begin
    def enclosingMethod {
      case class Nested(id: Int, name: String)
      val data = Map("id" -> 2, "name" -> "John")
      implicit val params = DefaultParams + Instantiator(Nested)
      val nested = read[Nested] from data
      nested shouldBe Nested(2, "John")
    }
    enclosingMethod
// doc end
  }
}
