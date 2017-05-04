scalaVersion := "2.12.2"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked",
  "-target:jvm-1.8"
)

PB.targets in Compile := Seq(
  scalapb.gen(grpc = false, flatPackage = true, javaConversions = false) -> (sourceManaged in Compile).value
)

assemblyOutputPath in assembly := file("target/myvault.jar")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"
)
