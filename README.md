# Simple case class reading.
Let's create a case class for examples below.

```scala
case class Person(id: Int, name: String)
```

All you need to read a case class is read function

```scala
import com.mahpella.entities.reading.caseclasses.read
```

Reading java map?

```scala
val data = new java.util.HashMap[String, Any]()
data.put("id", 2)
data.put("name", "John")
```

Just use proper adapter

```scala
import com.mahpella.entities.reading.caseclasses.JavaMapAdapter
val person = read[Person] from JavaMapAdapter(data)
person shouldBe Person(2, "John")
```

