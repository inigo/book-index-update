lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "net.surguy.reindexer",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Reindexer",
    
    libraryDependencies ++= Seq(
      "org.specs2" % "specs2-core_2.12" % "3.8.9" % Test
      , "org.apache.pdfbox" % "pdfbox" % "2.0.9"

    )
)
