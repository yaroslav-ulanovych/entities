# Brief intro example

```scala
case class Person(id: Int, name: String)
import xtract.read
```


```scala
val person = read[Person] from Map("id" -> 2, "name" -> "John")
person shouldBe Person(2, "John")
```

# Configuration
Reading can be customized in a variety of ways.

```scala
case class Params[-T](
```

[Adapter](#adapters-section) is an interface to support different data structures.

```scala
adapter: Adapter[T],
```

[Converters](#converters-section) even discrepancies between
types of case class fields and types of data source fields.

```scala
converters: Seq[Converter],
```

[Field naming convention](#field-naming-conventions-section)

```scala
fieldNamingConvention: FieldNamingConvention,
```

[Instantiators](#instantiators-section) solve problem of nested case classes.

```scala
  instantiators: Seq[CompanionObjectInstantiator]
)
```

Read function accepts configuration via an implicit parameter.
What you saw in intro was achieved with default parameters

```scala
import xtract.DefaultParams
```

You can always start with defaults

```scala
object DefaultParams extends Params(
  adapter = MapAdapter,
  converters = Seq(),
  fieldNamingConvention = LowerCamelCase.noDelimiter,
  instantiators = Seq()
)
```

and customize what you need via methods

```scala
def +[U](x: Adapter[U]) = copy(adapter = x)

def +(x: Converter) = copy(converters = converters :+ x)

def +(x: FieldNamingConvention) = copy(fieldNamingConvention = x)

def +(x: CompanionObjectInstantiator) = copy(instantiators = instantiators :+ x)
```

# <a name="adapters-section">Adapters</a>
One of the goals of this library is to provide an ability to work with different data structures.
To make read function independent from data it's reading from, a notion of adapters was introduced.
Adapter is a key value like interface to your data source.

```scala
abstract class Adapter[-T] {
  def get(data: T, key: String): Option[Any]
}
```

So you can parse into case classes json, xml, jdbc result sets, virtually everything you can write
an adapter for. And it's not hard to do so, let's write one for jdbc result set, for example.

```scala
import xtract.Adapter
```


```scala
import java.sql._

object ResultSetAdapter extends Adapter[ResultSet] {
  def get(data: ResultSet, key: String): Option[Any] = {
    val meta = data.getMetaData
    val columns = (1 to meta.getColumnCount).map({ i =>
      (meta.getColumnName(i), meta.getColumnType(i))
    }).toMap
    columns.get(key.toUpperCase) match {
```

And yeah, you don't need to use SQL_NAMING_CONVENTION in case classes
or perform field naming conversions in adapters, there is a dedicated
[field naming conventions](#field-naming-conventions-section) support.
I use `toUpperCase` here for simplicity, cause example is focused on
data extracting, not field naming conventions.

```scala
      case Some(Types.INTEGER) => Some(data.getInt(key))
      case Some(Types.VARCHAR) => Some(data.getString(key))
      case Some(_) => ??? // and so on
      case None => None
    }
  }
}
```

Let's check it.

```scala
Class.forName("org.h2.Driver")
val conn = DriverManager.getConnection("jdbc:h2:mem:")
val stmt = conn.createStatement()
stmt.execute("create table if not exists Person(id int, name varchar)")
stmt.execute("delete from Person")
stmt.execute("insert into Person values(2, 'John')")
val resultSet = stmt.executeQuery("select * from Person")
resultSet.next() shouldBe true
implicit val params = DefaultParams + ResultSetAdapter
val person = read[Person] from resultSet
person shouldBe Person(2, "John")
```

# <a name="converters-section">Converters</a>
There are cases when a data source contains a value of type that is
slightly different from what you expect in your case class.
For instance json ast from json4s uses big ints,
while in scala we usually use ints. Obviously you'll get a [bad field
value exception](#bad-field-value-exception-section), when you try to read such data source.

```scala
import org.json4s.JObject
import org.json4s.native.JsonParser
```


```scala
val json = """{"id": 2, "name": "John"}"""
val jval = JsonParser.parse(json)
val jobj = jval.asInstanceOf[JObject]
```


```scala
val e = intercept[BadFieldValueException] {
  read[Person] from jobj.values
}
e.valueType shouldBe classOf[BigInt]
```

We could fix that problem writing a proper adapter.

```scala
object JsonAdapter extends Adapter[JObject] {
  def get(data: JObject, key: String): Option[Any] = {
    data.values.get(key) map {
      case x: BigInt => x.toInt
      case x => x
    }
  }
}
read[Person].from(jobj)(DefaultParams + JsonAdapter) shouldBe Person(2, "John")
```

But that burdens adapter, whose task is
data extracting, while data converting can be a separate step. That's more
composable, since you can use one converter with different adapters and one
adapter with different converters.
Converter is just a function from one type to an option of another type

```scala
trait Converter {
  def canConvertFrom(klass: Class[_]): Boolean
  def canConvertTo(klass: Class[_]): Boolean
  def convert(value: Any, expected: Class[_]): Option[Any]
}
```

In fact it's a little bite more than just a function, cause it should carry
types, so that read function is able to find a proper converter,
but that is done implicitly and hidden from user.
Let's create one.

```scala
import xtract.Converter
val BigIntToInt = Converter((x: BigInt) => if (x.isValidInt) Some(x.toInt) else None)
```

and check that it works

```scala
val person = read[Person].from(jobj.values)(DefaultParams + BigIntToInt)
person shouldBe Person(2, "John")
```

Another possible case for converters is parsing enumerations,
which are often kept as strings or integers in data source, but
we want a normal enumeration in case class.

```scala
object JavaEnumConverter extends Converter {
```

It accepts strings or integers

```scala
def canConvertFrom(klass: Class[_]) = {
  klass == classOf[Integer] || klass == classOf[String]
}
```

can convert to any enumeration.

```scala
def canConvertTo(klass: Class[_]) = klass.isEnum
```

Well, I don't know what to explain here :)

```scala
  def convert(value: Any, expected: Class[_]): Option[Any] = {
    val xs = expected.getEnumConstants()
    value match {
      case i: Integer => {
        if (i >= 0 && i < xs.length) Some(xs(i)) else None
      }
      case s: String => xs.find(_.asInstanceOf[Enum[_]].name() == s)
    }
  }
}
```


```scala
import java.lang.Thread.State
case class Thread(state: State)
```


```scala
    implicit val params = DefaultParams + JavaEnumConverter
//
    read[Thread] from Map("state" -> 1) shouldBe {
      Thread(State.RUNNABLE)
    }

    read[Thread] from Map("state" -> "WAITING") shouldBe {
      Thread(State.WAITING)
    }
    
    intercept[BadFieldValueException] {
      read[Thread] from Map("state" -> "not an enum")
    }
  }
}
```

# <a name="field-naming-conventions-section">Field naming conventions</a>
Standard convention for class fields in scala is lowerCase.
But your data source may use a DIFFERENT_ONE. Obviously you can rename
case class fields to match fields of your data source, but that
would deprive you from the ability to reuse such a case class for different
data sources (json rest api and relational database, for instance).
And just look weird `case class Person(HOME_ADDRESS: String, WORK_ADDRESS: String)`.
Field naming convention is here to help. It consists of two parts.
Casing decides which letters are capitals and which are not.
It takes a list of field name parts and applies casing rules to them.

```scala
trait Casing {
  def apply(xs: List[String]): List[String]
```

Casing also has a bit of dsl to allow construction of field naming convention
by supplying a second part of it — delimiter.

```scala
  def delimitedBy(delimiter: Delimiter) = FieldNamingConvention(this, delimiter)
  def noDelimiter = delimitedBy(NoDelimiter)
}
```

There are some built int ones.

```scala
import xtract.{LowerCamelCase, LowerCase, UpperCase}
```

Lower camel case isn't actually doing anything,
cause we expect only case class field names to be passed here,
which are already in lower camel case.

```scala
LowerCamelCase(List("home", "Address")) shouldBe List("home", "Address")
LowerCamelCase(List("HOME", "ADDRESS")) shouldBe List("HOME", "ADDRESS")

LowerCase(List("home", "Address")) shouldBe List("home", "address")

UpperCase(List("home", "Address")) shouldBe List("HOME", "ADDRESS")
```

Delimiter concatenates parts of names using given delimiter string.

```scala
case class Delimiter(value: String) {
  def apply(xs: List[String]): String = xs.mkString(value)
}
```

Built in

```scala
import xtract.{NoDelimiter, Underscore}
```


```scala
NoDelimiter(List("home", "Address")) shouldBe "homeAddress"
Underscore(List("home", "Address")) shouldBe "home_Address"
```

Field naming convention just applies casing and delimiter in turn.

```scala
case class FieldNamingConvention(casing: Casing, delimiter: Delimiter) {
  def apply(xs: List[String]): String = delimiter(casing(xs))
}
```


```scala
import xtract.{read, DefaultParams}
val params = DefaultParams + UpperCase.noDelimiter
val person = read[Person].from(Map("ID" -> 2, "NAME" -> "John"))(params)
person shouldBe Person(2, "John")
```

