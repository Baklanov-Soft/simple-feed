import sbt._

object Dependencies {

  private object Versions {
    val cats       = "2.7.0"
    val catsEffect = "3.3.4"

    val doobie = "1.0.0-RC1"

    val flyway = "8.4.1"
    val fs2    = "3.2.4"

    val h2db = "2.0.206"

    val log4cats = "2.1.1"
    val log4j    = "2.17.1"
    val logback  = "1.2.10"

    val postgresql = "42.3.1"
    val pureconfig = "0.17.1"

    val scalatest = "3.2.10"

    val weaver = "0.7.9"
  }

  lazy val cats       = "org.typelevel" %% "cats-core"   % Versions.cats
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % Versions.catsEffect

  lazy val doobie         = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari"
  ).map(_ % Versions.doobie)

  lazy val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % Versions.doobie
  lazy val doobieH2       = "org.tpolecat" %% "doobie-h2"       % Versions.doobie % Test

  lazy val fs2 = "co.fs2" %% "fs2-core" % Versions.fs2

  lazy val h2db = "com.h2database" % "h2" % Versions.h2db % Test

  lazy val logging = Seq(
    "org.apache.logging.log4j" % "log4j-core"      % Versions.log4j,
    "ch.qos.logback"           % "logback-classic" % Versions.logback,
    "org.typelevel"           %% "log4cats-core"   % Versions.log4cats,
    "org.typelevel"           %% "log4cats-slf4j"  % Versions.log4cats
  )

  lazy val flyway     = Seq(
    "org.flywaydb" % "flyway-core",
    "org.flywaydb" % "flyway-maven-plugin"
  ).map(_ % Versions.flyway)

  lazy val postgresql = "org.postgresql" % "postgresql" % Versions.postgresql

  lazy val pureconfig = Seq(
    "com.github.pureconfig" %% "pureconfig-core"        % Versions.pureconfig,
    "com.github.pureconfig" %% "pureconfig-generic"     % Versions.pureconfig,
    "com.github.pureconfig" %% "pureconfig-cats-effect" % Versions.pureconfig
  )

  lazy val testing = Seq(
    "org.scalatest"       %% "scalatest"   % Versions.scalatest,
    "com.disneystreaming" %% "weaver-cats" % Versions.weaver
  ).map(_ % Test)

}
