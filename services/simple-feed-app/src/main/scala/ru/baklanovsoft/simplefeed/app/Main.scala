package ru.baklanovsoft.simplefeed.app

import cats.effect.kernel.Async
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax._
import ru.baklanovsoft.simplefeed.config.AppConfiguration
import ru.baklanovsoft.simplefeed.jdbc.app.Migrator

import scala.annotation.nowarn

@nowarn
object Main extends IOApp {

  private def app[F[_]: Async] = for {
    implicit0(logger: Logger[F]) <- Slf4jLogger.create[F]
    config                       <- ConfigSource.default.loadF[F, AppConfiguration]
    _                            <- Migrator.migrate.local[AppConfiguration](_.db).run(config)
  } yield ()

  override def run(args: List[String]): IO[ExitCode] = app[IO] >> IO(ExitCode.Success)
}
