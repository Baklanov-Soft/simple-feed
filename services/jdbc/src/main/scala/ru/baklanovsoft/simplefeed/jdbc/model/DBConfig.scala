package ru.baklanovsoft.simplefeed.jdbc.model

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

case class DBConfig(
    driver: String,
    url: String,
    user: String,
    password: String,
    migrationsLocation: String,
    threads: Int,
    migrateOnStart: Boolean
)

object DBConfig {
  implicit val coder: ConfigReader[DBConfig] = deriveReader[DBConfig]
}
