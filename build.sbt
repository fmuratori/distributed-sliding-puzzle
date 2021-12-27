name := "pcd-assignment-03"

version := "1.0"

scalaVersion := "2.13.1"

lazy val akkaVersion = "2.6.14"

libraryDependencies ++= Seq(
  "org.apache.pdfbox" % "pdfbox" % "2.0.22",

  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-multi-node-testkit"    % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test

)
