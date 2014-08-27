package xtract.docs.adapters

case class Person(id: Int, name: String)

import com.mahpella.entities.FunSuite

import xtract.read
import xtract.DefaultParams

// doc begin
// # <a name="adapters-section">Adapters</a>
// One of the goals of this library is to provide an ability to work with different data structures.
// To make read function independent from data it's reading from, a notion of adapters was introduced.
// Adapter is a key value like interface to your data source.
// include Adapter
// So you can parse into case classes json, xml, jdbc result sets, virtually everything you can write
// an adapter for. And it's not hard to do so, let's write one for jdbc result set, for example.
import xtract.Adapter
// doc end
class AdaptersSection extends FunSuite {
  test("writing custom adapter") {
// doc begin
    import java.sql._

    object ResultSetAdapter extends Adapter[ResultSet] {
      def get(data: ResultSet, key: String): Option[Any] = {
        val meta = data.getMetaData
        val columns = (1 to meta.getColumnCount).map({ i =>
          (meta.getColumnName(i), meta.getColumnType(i))
        }).toMap
        columns.get(key.toUpperCase) match {
// And yeah, you don't need to use SQL_NAMING_CONVENTION in case classes
// or perform field naming conversions in adapters, there is a dedicated
// [field naming conventions](#field-naming-conventions-section) support.
// I use `toUpperCase` here for simplicity, cause example is focused on
// data extracting, not field naming conventions.
          case Some(Types.INTEGER) => Some(data.getInt(key))
          case Some(Types.VARCHAR) => Some(data.getString(key))
          case Some(_) => ??? // and so on
          case None => None
        }
      }
    }
// Let's check it.
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
// doc end
    stmt.execute("drop table Person")
  }
}
