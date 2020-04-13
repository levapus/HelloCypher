import org.apache.spark.sql.DataFrame
import org.opencypher.morpheus.api.MorpheusSession
import org.opencypher.morpheus.api.io.{MorpheusNodeTable, MorpheusRelationshipTable}
//import org.opencypher.morpheus.util.App

import org.apache.spark.sql._
import org.apache.log4j.{Level, Logger}
//Logger.getLogger("org").setLevel(Level.OFF)


/**
 * Demonstrates basic usage of the Morpheus API by loading an example graph from [[DataFrame]]s.
 */
object DataFrameInputExample extends App {
  // 1) Create Morpheus session and retrieve Spark session
  implicit val morpheus: MorpheusSession = MorpheusSession.local()
//  val spark = morpheus.sparkSession

  val spark = {
    SparkSession.builder()
      .master("local[*]")
      .getOrCreate()
  }

  import spark.sqlContext.implicits._

  // 2) Generate some DataFrames that we'd like to interpret as a property graph.
  val nodesDF = spark.createDataset(Seq(
    (0L, "Alice", 42L),
    (1L, "Bob", 23L),
    (2L, "Eve", 84L)
  )).toDF("id", "name", "age")
  val relsDF = spark.createDataset(Seq(
    (0L, 0L, 1L, "23/01/1987"),
    (1L, 1L, 2L, "12/12/2009")
  )).toDF("id", "source", "target", "since")

  // 3) Generate node- and relationship tables that wrap the DataFrames. The mapping between graph elements and columns
  //    is derived using naming conventions for identifier columns.
  val personTable = MorpheusNodeTable(Set("Person"), nodesDF)
  val friendsTable = MorpheusRelationshipTable("KNOWS", relsDF)

  // 4) Create property graph from graph scans
  val graph = morpheus.readFrom(personTable, friendsTable)

  // 5) Execute Cypher query and print results
  val result = graph.cypher("MATCH (n:Person) RETURN n.name")

  // 6) Collect results into string by selecting a specific column.
  //    This operation may be very expensive as it materializes results locally.
  val names: Set[String] = result.records.table.df.collect().map(_.getAs[String]("n_name")).toSet

  println(names)
}