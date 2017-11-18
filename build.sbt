import com.trueaccord.scalapb.compiler.Version.scalapbVersion

name := "akka-persistence-protobuf-schema-evolution"

version := "1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.6"


libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime" % scalapbVersion % "protobuf"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

// Akka
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" % "akka-slf4j_2.12" % akkaVersion
)

//LevelDB plugin
libraryDependencies ++= Seq(
  "org.iq80.leveldb" % "leveldb" % "0.9",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
)

//logback
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.qos.logback" % "logback-core" % "1.2.3",
)
