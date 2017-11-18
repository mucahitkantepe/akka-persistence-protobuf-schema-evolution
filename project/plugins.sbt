addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")
//resolvers += "bintray-sbt-plugin-releases" at "http://dl.bintray.com/content/sbt/sbt-plugin-releases"
addSbtPlugin("org.duhemm" % "sbt-errors-summary" % "0.6.0")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.12")

libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.6.6"