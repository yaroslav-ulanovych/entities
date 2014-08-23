# Simple case class reading
Let's create a case class for examples below.

```scala
case class Person(id: Int, name: String)
```

To read a case class you need a read function

```scala
import com.mahpella.entities.reading.caseclasses.read
```

and an adapter to your data source you want to read from.
Say, we want to read from a map

```scala
val data = Map("id" -> 2, "name" -> "John")
```

then we use built in map adapter

```scala
import com.mahpella.entities.reading.caseclasses.MapAdapter
```

Reading is simple

```scala
    val person = read[Person] from MapAdapter(data)

    person shouldBe Person(2, "John")
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

