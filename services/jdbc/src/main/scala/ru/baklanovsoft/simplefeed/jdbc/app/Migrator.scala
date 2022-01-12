package ru.baklanovsoft.simplefeed.jdbc.app

import cats.data.Kleisli
import cats.effect.Sync
import cats.implicits._
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationState
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.typelevel.log4cats.Logger
import ru.baklanovsoft.simplefeed.jdbc.model.DBConfig

import scala.jdk.CollectionConverters._

object Migrator {

  private def validate[F[_]: Logger: Sync](flywayConfig: FluentConfiguration): F[Unit] =
    for {
      validated <- Sync[F].delay(
                     flywayConfig
                       .ignoreMigrationPatterns("*:pending")
                       .load()
                       .validateWithResult
                   )

      _ <- Sync[F].whenA(!validated.validationSuccessful)(
             validated.invalidMigrations.asScala.toList.traverse(error => Logger[F].error(s"Invalid migration: $error"))
           )

      _ <- Sync[F].whenA(!validated.validationSuccessful)(
             Sync[F].raiseError(new Error("Migrations validation failed (see the logs)"))
           )
    } yield ()

  def migrate[F[_]: Logger: Sync]: Kleisli[F, DBConfig, Unit] = Kleisli { config =>
    for {
      _ <- Logger[F].info(
             s"Starting the migrations module for database: ${config.url} " +
               s"with driver: ${config.driver}, migrations enabled: ${config.migrateOnStart}"
           )

      flywayConfig =
        Flyway.configure
          .loggers("log4j2")
          .dataSource(
            config.url,
            config.user,
            config.password
          )
          .group(true)
          .outOfOrder(false)
          .locations(config.migrationsLocation)
          .failOnMissingLocations(true)
          .baselineOnMigrate(true)

      _ <- validate(flywayConfig)
      _ <- Logger[F].info("Migrations validation successful")

      count <- Sync[F].ifM(Sync[F].pure(config.migrateOnStart))(
                 Sync[F].delay(flywayConfig.load().migrate().migrationsExecuted),
                 Sync[F].pure(0)
               )

      _ <- Logger[F].info(s"Migrations executed: $count")

      _ <- flywayConfig.load().info().all().toList.traverse { i =>
             i.getState match {
               case MigrationState.SUCCESS => Sync[F].unit
               case e                      =>
                 Sync[F]
                   .raiseError[Unit](new Error(s"Migration ${i.getDescription} status is not SUCCESS: ${e.toString}"))
             }
           }

    } yield ()
  }
}
