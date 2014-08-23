package com.mahpella.entities.reading.caseclasses.docs

// doc begin
// # Simple case class reading.
// Let's create a case class for examples below.
case class Person(id: Int, name: String)
// doc end

// doc begin
// All you need to read a case class is read function
import com.mahpella.entities.reading.caseclasses.read
// doc end


class ReadTest extends com.mahpella.entities.FunSuite {
  test("SimpleTopLevelCaseClass") {

// and an adapter to your data source you want to read from.
// Say, we want to read a from a map
    val data = Map("id" -> 2, "name" -> "John")
// then we use build in map adapter
    import com.mahpella.entities.reading.caseclasses.MapAdapter
// reading is as simple as that
    val person = read[Person] from MapAdapter(data)

    person shouldBe Person(2, "John")
// doc end
  }

  test("java map") {
// doc begin
// Reading java map?
    val data = new java.util.HashMap[String, Any]()
    data.put("id", 2)
    data.put("name", "John")
// Just use proper adapter
    import com.mahpella.entities.reading.caseclasses.JavaMapAdapter
    val person = read[Person] from JavaMapAdapter(data)
    person shouldBe Person(2, "John")
// doc end
  }
}
