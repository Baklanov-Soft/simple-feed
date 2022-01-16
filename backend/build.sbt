ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / testFrameworks += new TestFramework("weaver.framework.CatsEffect")

lazy val commonDeps = Seq(
  addCompilerPlugin(("org.typelevel" % "kind-projector" % "0.13.2").cross(CrossVersion.full)),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  scalafmtOnCompile := true,
  libraryDependencies += Dependencies.cats,
  libraryDependencies += Dependencies.catsEffect,
  libraryDependencies += Dependencies.fs2,
  libraryDependencies ++= Dependencies.logging
)

lazy val core = project
  .in(file("./services/core"))
  .settings(commonDeps)
  .settings(
    name := "core"
  )

lazy val jdbc = project
  .in(file("./services/jdbc"))
  .settings(commonDeps)
  .settings(
    name := "jdbc",
    libraryDependencies ++= Dependencies.doobie,
    libraryDependencies ++= Dependencies.flyway,
    libraryDependencies += Dependencies.doobiePostgres,
    libraryDependencies += Dependencies.postgresql,
    libraryDependencies ++= Dependencies.pureconfig
  )
  .dependsOn(core)

lazy val simpleFeedApp = project
  .in(file("./services/simple-feed-app"))
  .settings(commonDeps)
  .settings(
    name := "simple-feed-app",
    libraryDependencies ++= Dependencies.pureconfig,
    libraryDependencies += Dependencies.doobieH2,
    libraryDependencies += Dependencies.h2db,
    libraryDependencies ++= Dependencies.testing
  )
  .dependsOn(jdbc)
  .dependsOn(core)
