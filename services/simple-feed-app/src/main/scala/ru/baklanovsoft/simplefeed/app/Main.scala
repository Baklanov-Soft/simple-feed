package ru.baklanovsoft.simplefeed.app

import cats.effect.kernel.Async
import cats.effect.{ExitCode, IO, IOApp, Resource}
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax._
import ru.baklanovsoft.simplefeed.config.AppConfiguration
import ru.baklanovsoft.simplefeed.jdbc.app.{Migrator, Transactor}

import scala.annotation.nowarn

@nowarn
object Main extends IOApp {

  private def app[F[_]: Async] =
    Resource.eval(ConfigSource.default.loadF[F, AppConfiguration]).flatMap { cfg =>
      {
        for {
          _          <- Migrator.migrate[F].mapF(Resource.eval)
          transactor <- Transactor.make[F]
        } yield transactor
      }.run(cfg.db)
    }

  override def run(args: List[String]): IO[ExitCode] =
    app[IO].use(_ => IO(ExitCode.Success))
}
