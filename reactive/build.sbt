
resolvers += Resolver.bintrayRepo("dmbl","dinogroup")

lazy val root = (project in file("."))
  .settings(
    organization := "com.gpevnev",
    name := "reactive",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.11",
    libraryDependencies ++= Seq(
      "org.mongodb.scala"     %% "mongo-scala-driver"  % "2.8.0",
      "com.lightbend.akka"    %% "akka-stream-alpakka-mongodb" % "1.1.2",
      "com.typesafe.akka" %% "akka-http"   % "10.1.11",
      "com.typesafe.akka" %% "akka-stream" % "2.6.4",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11"
),
)
