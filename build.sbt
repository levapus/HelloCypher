name := "HelloCypher"

version := "0.1"

scalaVersion := "2.12.11"

libraryDependencies += "org.opencypher" % "morpheus-spark-cypher" % "0.4.2"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.5",
  "org.apache.spark" %% "spark-sql" % "2.4.5",
  "org.apache.spark" %% "spark-hive" % "2.4.5"
)