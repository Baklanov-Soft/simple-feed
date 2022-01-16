package ru.baklanovsoft.simplefeed.jdbc.app

import cats.data.Kleisli
import cats.effect.Resource
import cats.effect.kernel.Async
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import ru.baklanovsoft.simplefeed.jdbc.model.DBConfig

object Transactor {

  private def transactorResource[F[_]: Async](config: DBConfig): Resource[F, HikariTransactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](config.threads)
      xa <- HikariTransactor.newHikariTransactor[F](
              config.driver,
              config.url,
              config.user,
              config.password,
              ce
            )
    } yield xa

  def make[F[_]: Async]: Kleisli[Resource[F, *], DBConfig, HikariTransactor[F]] =
    Kleisli(config => transactorResource[F](config))
}
